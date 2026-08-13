#!/usr/bin/env python3
"""
Harness Step Executor — phase 내 step을 순차 실행하고 자가 교정한다.

Usage:
    python3 scripts/execute.py <phase-dir> [--push]
"""

import argparse
import contextlib
import json
import os
import subprocess
import sys
import threading
import time
import types
from datetime import datetime, timezone, timedelta
from pathlib import Path
from typing import Optional

ROOT = Path(__file__).resolve().parent.parent


@contextlib.contextmanager
def progress_indicator(label: str):
    """터미널 진행 표시기. with 문으로 사용하며 .elapsed 로 경과 시간을 읽는다."""
    frames = "◐◓◑◒"
    stop = threading.Event()
    t0 = time.monotonic()

    def _animate():
        idx = 0
        while not stop.wait(0.12):
            sec = int(time.monotonic() - t0)
            sys.stderr.write(f"\r{frames[idx % len(frames)]} {label} [{sec}s]")
            sys.stderr.flush()
            idx += 1
        sys.stderr.write("\r" + " " * (len(label) + 20) + "\r")
        sys.stderr.flush()

    th = threading.Thread(target=_animate, daemon=True)
    th.start()
    info = types.SimpleNamespace(elapsed=0.0)
    try:
        yield info
    finally:
        stop.set()
        th.join()
        info.elapsed = time.monotonic() - t0


class StepExecutor:
    """Phase 디렉토리 안의 step들을 순차 실행하는 하네스."""

    MAX_RETRIES = 3
    DEFAULT_IMPL_AGENT = "backend-developer"
    FEAT_MSG = "feat: step {num} — {name} ({phase})"
    CHORE_MSG = "chore: step {num} output ({phase})"
    TZ = timezone(timedelta(hours=9))

    ROLE_RULES = {
        "impl": (
            "## 역할: 구현\n\n"
            "1. 아래 step의 `## 작업`에 명시된 구현만 하라. 테스트 작성·index.json 갱신·커밋은 다음 단계에서 하니 하지 마라.\n"
            "2. 이전 step 코드와 일관성을 유지하고 기존 테스트를 깨뜨리지 마라.\n"
            "3. 레이어 규칙을 지켜라 — Service·Controller는 api-server, Entity·Repository·DTO는 core.\n"
        ),
        "test": (
            "## 역할: 테스트\n\n"
            "1. 방금 구현된 코드를 읽고, CLAUDE.md 테스트 작성 규약에 따라 대표 성공 1개 + 주요 실패·분기만 작성하고 실행하라.\n"
            "2. 운영 코드를 수정하지 마라. 테스트 코드만 추가하라.\n"
            "3. index.json을 갱신하지 마라.\n"
        ),
        "review": (
            "## 역할: 리뷰\n\n"
            "1. 이번 step의 git 변경분(git diff)을 ARCHITECTURE.md·ADR·CLAUDE.md 기준으로 검토하라.\n"
            "2. 지적사항을 `파일:라인 — 문제` 형식으로 stdout에 출력하라. 문제가 없으면 첫 줄에 `LGTM`만 출력하라.\n"
            "3. 코드를 수정하지 말고 index.json도 갱신하지 마라. 리뷰만 하라.\n"
        ),
        "fix": (
            "## 역할: 리뷰 반영\n\n"
            "1. 아래 코드 리뷰 지적사항을 반영해 코드를 수정하라.\n"
            "2. 지적사항을 넘어선 추가 변경을 하지 마라. index.json·커밋은 다음 단계에서 한다.\n"
        ),
        "verify": (
            "## 역할: 검증 및 상태 기록\n\n"
            "1. 이 step의 `## Acceptance Criteria` 커맨드를 직접 실행해 통과를 확인하라.\n"
            "2. /phases/{dir}/index.json의 해당 step status를 갱신하라:\n"
            "   - AC 통과 -> \"completed\" + \"summary\"에 산출물 한 줄 요약\n"
            "   - AC 실패(수정 불가) -> \"error\" + \"error_message\"\n"
            "   - 사용자 개입 필요(API 키·인증·수동 설정 등) -> \"blocked\" + \"blocked_reason\" 후 중단\n"
            "3. 커밋은 하지 마라. 하네스가 커밋한다.\n"
        ),
    }

    def __init__(self, phase_dir_name: str, *, auto_push: bool = False, only_step: Optional[int] = None):
        self._root = str(ROOT)
        self._phases_dir = ROOT / "phases"
        self._phase_dir = self._phases_dir / phase_dir_name
        self._phase_dir_name = phase_dir_name
        self._top_index_file = self._phases_dir / "index.json"
        self._auto_push = auto_push
        self._only_step = only_step

        if not self._phase_dir.is_dir():
            print(f"ERROR: {self._phase_dir} not found")
            sys.exit(1)

        self._index_file = self._phase_dir / "index.json"
        if not self._index_file.exists():
            print(f"ERROR: {self._index_file} not found")
            sys.exit(1)

        idx = self._read_json(self._index_file)
        self._project = idx.get("project", "project")
        self._phase_name = idx.get("phase", phase_dir_name)
        self._total = len(idx["steps"])

    def run(self):
        self._print_header()
        self._check_blockers()
        self._checkout_branch()
        self._ensure_created_at()
        if self._only_step is not None:
            self._execute_one(self._only_step)
        else:
            self._execute_all_steps()
            self._finalize()

    # --- timestamps ---

    def _stamp(self) -> str:
        return datetime.now(self.TZ).strftime("%Y-%m-%dT%H:%M:%S%z")

    # --- JSON I/O ---

    @staticmethod
    def _read_json(p: Path) -> dict:
        return json.loads(p.read_text(encoding="utf-8"))

    @staticmethod
    def _write_json(p: Path, data: dict):
        p.write_text(json.dumps(data, indent=2, ensure_ascii=False), encoding="utf-8")

    # --- git ---

    def _run_git(self, *args) -> subprocess.CompletedProcess:
        cmd = ["git"] + list(args)
        return subprocess.run(cmd, cwd=self._root, capture_output=True, text=True)

    def _checkout_branch(self):
        branch = f"feat-{self._phase_name}"

        r = self._run_git("rev-parse", "--abbrev-ref", "HEAD")
        if r.returncode != 0:
            print(f"  ERROR: git을 사용할 수 없거나 git repo가 아닙니다.")
            print(f"  {r.stderr.strip()}")
            sys.exit(1)

        if r.stdout.strip() == branch:
            return

        r = self._run_git("rev-parse", "--verify", branch)
        r = self._run_git("checkout", branch) if r.returncode == 0 else self._run_git("checkout", "-b", branch)

        if r.returncode != 0:
            print(f"  ERROR: 브랜치 '{branch}' checkout 실패.")
            print(f"  {r.stderr.strip()}")
            print(f"  Hint: 변경사항을 stash하거나 commit한 후 다시 시도하세요.")
            sys.exit(1)

        print(f"  Branch: {branch}")

    def _commit_step(self, step_num: int, step_name: str):
        output_rel = f"phases/{self._phase_dir_name}/step{step_num}-output.json"
        review_rel = f"phases/{self._phase_dir_name}/step{step_num}-review.md"
        index_rel = f"phases/{self._phase_dir_name}/index.json"

        self._run_git("add", "-A")
        self._run_git("reset", "HEAD", "--", output_rel)
        self._run_git("reset", "HEAD", "--", review_rel)
        self._run_git("reset", "HEAD", "--", index_rel)

        if self._run_git("diff", "--cached", "--quiet").returncode != 0:
            msg = self.FEAT_MSG.format(phase=self._phase_name, num=step_num, name=step_name)
            r = self._run_git("commit", "-m", msg)
            if r.returncode == 0:
                print(f"  Commit: {msg}")
            else:
                print(f"  WARN: 코드 커밋 실패: {r.stderr.strip()}")

        self._run_git("add", "-A")
        if self._run_git("diff", "--cached", "--quiet").returncode != 0:
            msg = self.CHORE_MSG.format(phase=self._phase_name, num=step_num)
            r = self._run_git("commit", "-m", msg)
            if r.returncode != 0:
                print(f"  WARN: housekeeping 커밋 실패: {r.stderr.strip()}")

    # --- top-level index ---

    def _update_top_index(self, status: str):
        if not self._top_index_file.exists():
            return
        top = self._read_json(self._top_index_file)
        ts = self._stamp()
        for phase in top.get("phases", []):
            if phase.get("dir") == self._phase_dir_name:
                phase["status"] = status
                ts_key = {"completed": "completed_at", "error": "failed_at", "blocked": "blocked_at"}.get(status)
                if ts_key:
                    phase[ts_key] = ts
                break
        self._write_json(self._top_index_file, top)

    # --- guardrails & context ---

    def _load_guardrails(self) -> str:
        sections = []
        claude_md = ROOT / "CLAUDE.md"
        if claude_md.exists():
            sections.append(f"## 프로젝트 규칙 (CLAUDE.md)\n\n{claude_md.read_text()}")
        docs_dir = ROOT / "docs"
        if docs_dir.is_dir():
            for doc in sorted(docs_dir.glob("*.md")):
                sections.append(f"## {doc.stem}\n\n{doc.read_text()}")
        return "\n\n---\n\n".join(sections) if sections else ""

    @staticmethod
    def _build_step_context(index: dict) -> str:
        lines = [
            f"- Step {s['step']} ({s['name']}): {s['summary']}"
            for s in index["steps"]
            if s["status"] == "completed" and s.get("summary")
        ]
        if not lines:
            return ""
        return "## 이전 Step 산출물\n\n" + "\n".join(lines) + "\n\n"

    def _role_prompt(self, role: str, guardrails: str, step_context: str,
                     prev_error: Optional[str], step_body: str) -> str:
        retry_section = ""
        if prev_error:
            retry_section = (
                f"\n## ⚠ 이전 시도 실패 — 아래 에러를 반드시 참고하여 수정하라\n\n"
                f"{prev_error}\n\n---\n\n"
            )
        rules = self.ROLE_RULES[role].format(dir=self._phase_dir_name)
        return (
            f"당신은 {self._project} 프로젝트의 개발자입니다.\n\n"
            f"{guardrails}\n\n---\n\n"
            f"{step_context}{retry_section}"
            f"{rules}\n---\n\n"
            f"{step_body}"
        )

    # --- Claude 호출 ---

    def _run_claude(self, prompt: str, agent: Optional[str] = None) -> subprocess.CompletedProcess:
        cmd = ["claude", "-p", "--dangerously-skip-permissions", "--output-format", "json"]
        if agent:
            cmd += ["--agent", agent]
        cmd.append(prompt)
        result = subprocess.run(cmd, cwd=self._root, capture_output=True, text=True, timeout=1800)
        if result.returncode != 0:
            print(f"\n  WARN: Claude가 비정상 종료됨 (code {result.returncode})")
            if result.stderr:
                print(f"  stderr: {result.stderr[:500]}")
        return result

    @staticmethod
    def _extract_text(result: subprocess.CompletedProcess) -> str:
        try:
            data = json.loads(result.stdout)
            if isinstance(data, dict) and "result" in data:
                return str(data["result"])
        except (json.JSONDecodeError, ValueError):
            pass
        return result.stdout or ""

    @staticmethod
    def _review_is_clean(text: str) -> bool:
        t = text.strip()
        return (not t) or t.upper().startswith("LGTM") or len(t) < 40

    def _invoke_pipeline(self, step: dict, guardrails: str, step_context: str,
                         prev_error: Optional[str], tag: str) -> int:
        """구현→테스트→리뷰→리뷰반영→검증을 역할별 서브에이전트로 순차 실행한다.

        핸드오프는 공유 작업트리로 하고, 마지막 검증 단계가 index.json 상태를 기록한다."""
        step_num, step_name = step["step"], step["name"]
        step_file = self._phase_dir / f"step{step_num}.md"
        if not step_file.exists():
            print(f"  ERROR: {step_file} not found")
            sys.exit(1)

        step_body = step_file.read_text()
        impl_agent = step.get("agent", self.DEFAULT_IMPL_AGENT)
        results = []
        total_elapsed = 0.0

        def stage(label, role, agent, extra=""):
            nonlocal total_elapsed
            prompt = self._role_prompt(role, guardrails, step_context, prev_error, step_body) + extra
            with progress_indicator(f"{tag} · {label}") as pi:
                r = self._run_claude(prompt, agent)
                total_elapsed += pi.elapsed
            results.append((label, r))
            return r

        stage("구현", "impl", impl_agent)
        stage("테스트", "test", "test-engineer")

        review = stage("리뷰", "review", "code-reviewer")
        review_text = self._extract_text(review)
        (self._phase_dir / f"step{step_num}-review.md").write_text(review_text, encoding="utf-8")

        if not self._review_is_clean(review_text):
            stage("리뷰 반영", "fix", impl_agent, extra=f"\n\n## 코드 리뷰 지적사항\n\n{review_text}\n")

        stage("검증", "verify", None)

        output = {
            "step": step_num, "name": step_name,
            "stages": [
                {"stage": label, "exitCode": r.returncode, "stdout": r.stdout, "stderr": r.stderr}
                for label, r in results
            ],
        }
        out_path = self._phase_dir / f"step{step_num}-output.json"
        with open(out_path, "w", encoding="utf-8") as f:
            json.dump(output, f, indent=2, ensure_ascii=False)

        return int(total_elapsed)

    # --- 헤더 & 검증 ---

    def _print_header(self):
        print(f"\n{'='*60}")
        print(f"  Harness Step Executor")
        print(f"  Phase: {self._phase_name} | Steps: {self._total}")
        if self._auto_push:
            print(f"  Auto-push: enabled")
        print(f"{'='*60}")

    def _check_blockers(self):
        index = self._read_json(self._index_file)
        for s in reversed(index["steps"]):
            if s["status"] == "error":
                print(f"\n  ✗ Step {s['step']} ({s['name']}) failed.")
                print(f"  Error: {s.get('error_message', 'unknown')}")
                print(f"  Fix and reset status to 'pending' to retry.")
                sys.exit(1)
            if s["status"] == "blocked":
                print(f"\n  ⏸ Step {s['step']} ({s['name']}) blocked.")
                print(f"  Reason: {s.get('blocked_reason', 'unknown')}")
                print(f"  Resolve and reset status to 'pending' to retry.")
                sys.exit(2)
            if s["status"] != "pending":
                break

    def _ensure_created_at(self):
        index = self._read_json(self._index_file)
        if "created_at" not in index:
            index["created_at"] = self._stamp()
            self._write_json(self._index_file, index)

    # --- 실행 루프 ---

    def _execute_single_step(self, step: dict) -> bool:
        """단일 step 실행 (재시도 포함). 완료되면 True, 실패/차단이면 False."""
        step_num, step_name = step["step"], step["name"]
        done = sum(1 for s in self._read_json(self._index_file)["steps"] if s["status"] == "completed")
        prev_error = None

        for attempt in range(1, self.MAX_RETRIES + 1):
            index = self._read_json(self._index_file)
            step_context = self._build_step_context(index)
            guardrails = self._load_guardrails()

            tag = f"Step {step_num}/{self._total - 1} ({done} done): {step_name}"
            if attempt > 1:
                tag += f" [retry {attempt}/{self.MAX_RETRIES}]"

            elapsed = self._invoke_pipeline(step, guardrails, step_context, prev_error, tag)

            index = self._read_json(self._index_file)
            status = next((s.get("status", "pending") for s in index["steps"] if s["step"] == step_num), "pending")
            ts = self._stamp()

            if status == "completed":
                for s in index["steps"]:
                    if s["step"] == step_num:
                        s["completed_at"] = ts
                self._write_json(self._index_file, index)
                self._commit_step(step_num, step_name)
                print(f"  ✓ Step {step_num}: {step_name} [{elapsed}s]")
                return True

            if status == "blocked":
                for s in index["steps"]:
                    if s["step"] == step_num:
                        s["blocked_at"] = ts
                self._write_json(self._index_file, index)
                reason = next((s.get("blocked_reason", "") for s in index["steps"] if s["step"] == step_num), "")
                print(f"  ⏸ Step {step_num}: {step_name} blocked [{elapsed}s]")
                print(f"    Reason: {reason}")
                self._update_top_index("blocked")
                sys.exit(2)

            err_msg = next(
                (s.get("error_message", "Step did not update status") for s in index["steps"] if s["step"] == step_num),
                "Step did not update status",
            )

            if attempt < self.MAX_RETRIES:
                for s in index["steps"]:
                    if s["step"] == step_num:
                        s["status"] = "pending"
                        s.pop("error_message", None)
                self._write_json(self._index_file, index)
                prev_error = err_msg
                print(f"  ↻ Step {step_num}: retry {attempt}/{self.MAX_RETRIES} — {err_msg}")
            else:
                for s in index["steps"]:
                    if s["step"] == step_num:
                        s["status"] = "error"
                        s["error_message"] = f"[{self.MAX_RETRIES}회 시도 후 실패] {err_msg}"
                        s["failed_at"] = ts
                self._write_json(self._index_file, index)
                self._commit_step(step_num, step_name)
                print(f"  ✗ Step {step_num}: {step_name} failed after {self.MAX_RETRIES} attempts [{elapsed}s]")
                print(f"    Error: {err_msg}")
                self._update_top_index("error")
                sys.exit(1)

        return False  # unreachable

    def _execute_one(self, step_num: int):
        """지정한 step 하나만 실행하고 멈춘다. 마지막 pending step이 끝난 경우에만 phase를 완료 처리한다."""
        index = self._read_json(self._index_file)
        step = next((s for s in index["steps"] if s["step"] == step_num), None)
        if step is None:
            print(f"  ERROR: step {step_num}을(를) 찾을 수 없습니다.")
            sys.exit(1)
        if step["status"] == "completed":
            print(f"  Step {step_num} ({step['name']})은(는) 이미 완료되었습니다.")
            return
        if step["status"] != "pending":
            print(f"  ERROR: step {step_num}의 status가 '{step['status']}'라 실행할 수 없습니다. pending으로 되돌린 뒤 재실행하세요.")
            sys.exit(1)

        if "started_at" not in step:
            step["started_at"] = self._stamp()
            self._write_json(self._index_file, index)

        self._execute_single_step(step)

        remaining = [s for s in self._read_json(self._index_file)["steps"] if s["status"] == "pending"]
        if not remaining:
            self._finalize()
        else:
            print(f"\n  Step {step_num} 완료. 남은 pending step {len(remaining)}개. 다음 step은 --step 으로 진행하세요.")

    def _execute_all_steps(self):
        while True:
            index = self._read_json(self._index_file)
            pending = next((s for s in index["steps"] if s["status"] == "pending"), None)
            if pending is None:
                print("\n  All steps completed!")
                return

            step_num = pending["step"]
            for s in index["steps"]:
                if s["step"] == step_num and "started_at" not in s:
                    s["started_at"] = self._stamp()
                    self._write_json(self._index_file, index)
                    break

            self._execute_single_step(pending)

    def _finalize(self):
        index = self._read_json(self._index_file)
        index["completed_at"] = self._stamp()
        self._write_json(self._index_file, index)
        self._update_top_index("completed")

        self._run_git("add", "-A")
        if self._run_git("diff", "--cached", "--quiet").returncode != 0:
            msg = f"chore({self._phase_name}): mark phase completed"
            r = self._run_git("commit", "-m", msg)
            if r.returncode == 0:
                print(f"  ✓ {msg}")

        if self._auto_push:
            branch = f"feat-{self._phase_name}"
            r = self._run_git("push", "-u", "origin", branch)
            if r.returncode != 0:
                print(f"\n  ERROR: git push 실패: {r.stderr.strip()}")
                sys.exit(1)
            print(f"  ✓ Pushed to origin/{branch}")

        print(f"\n{'='*60}")
        print(f"  Phase '{self._phase_name}' completed!")
        print(f"{'='*60}")


def main():
    parser = argparse.ArgumentParser(description="Harness Step Executor")
    parser.add_argument("phase_dir", help="Phase directory name (e.g. 0-mvp)")
    parser.add_argument("--push", action="store_true", help="Push branch after completion")
    parser.add_argument("--step", type=int, default=None, help="지정한 step 하나만 실행하고 멈춘다")
    args = parser.parse_args()

    StepExecutor(args.phase_dir, auto_push=args.push, only_step=args.step).run()


if __name__ == "__main__":
    main()
