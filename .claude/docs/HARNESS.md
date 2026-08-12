# HARNESS 사용법

무인 실행 워크플로우. 설계 지시서(`stepN.md`)를 만들고,
step을 하나씩 `execute.py`로 돌려 자가교정·커밋까지 자동으로 맡긴다.
사람이 각 step 사이에서 리뷰하고 다음 step을 지시한다.
(정의: `.claude/commands/harness.md`, `scripts/execute.py`)

## 언제 쓰나

- 작업을 여러 step으로 쪼개 무인으로 구현시키고,
  step마다 내가 검토하며 넘어가고 싶을 때.
- 인터랙티브하게 에이전트와 주고받고 싶으면 `/develop`를 쓴다
  (→ `.claude/docs/DEVELOP.md`).

## 흐름

```
/harness 주문 생성 기능       → 설계 → 승인
승인                          → phases/{task}/ + step0~N.md 생성 후 멈춤
0번 진행해                    → python3 scripts/execute.py {task} --step 0
리뷰해                        → /review  (방금 step 커밋 범위만)
1번 진행해                    → python3 scripts/execute.py {task} --step 1
...                           → 마지막 step이 끝나면 phase가 completed 처리됨
```

`--step N` 한 번이 하는 일:
`feat-{task}` 브랜치 checkout → 가드레일(CLAUDE.md·docs) 주입
→ 구현·테스트(AC 실패 시 3회 자가교정) → `feat`/`chore` 2단계 커밋
→ index.json step을 completed 기록 → 멈춤.

전체를 한 번에 무인으로 돌리려면 `--step` 없이: `python3 scripts/execute.py {task}`

## 막혔을 때

`phases/{task}/index.json`의 해당 step을 보고:

- `error` → `status`를 `"pending"`으로 되돌리고
  `error_message` 삭제 후 `--step N` 재실행.
- `blocked` → `blocked_reason` 사유를 해결한 뒤
  `status`를 `"pending"`으로, `blocked_reason` 삭제 후 재실행.

이미 `completed`인 step은 `--step`이 그냥 넘어간다.
다시 돌리려면 `pending`으로 되돌린다.