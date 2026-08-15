# DEVELOP 사용법

인터랙티브 멀티에이전트 워크플로우.
메인 세션이 오케스트레이터가 되어 서브에이전트를 순서대로 호출하고,
**각 단계 사이에서 멈춰 내 확인을 받은 뒤** 다음으로 넘어간다.
(정의: `.claude/commands/develop.md`, `.claude/agents/*.md`)

## 언제 쓰나

- 설계·구현·테스트·리뷰를 단계마다
  직접 보고받고 조율하며 진행하고 싶을 때.
- 무인으로 step을 쭉 돌리고 싶으면 `/harness`를 쓴다
  (→ `.claude/docs/HARNESS.md`).

## 파이프라인

```
/develop 주문 생성 기능
1. 설계    software-architect  → 설계 지시서 보고 → 승인/수정
2. 구현    developer           → 만든 파일·빌드 결과 보고 → 확인
3. 테스트  test-engineer       → 테스트 작성·실행 결과 보고 → 확인
4. 검증    code-reviewer       → 리뷰 리포트 보고 → 위반 시 수정 방향 결정
```

각 단계에서 "멈춰"·"다시"라고 하면 다음으로 넘어가지 않는다.
커밋은 명시적으로 지시하기 전에는 하지 않는다.

## 서브에이전트

| 에이전트 | 역할 | 코드 작성 |
| --- | --- | --- |
| `software-architect` | 요구사항 → 레이어·시그니처·핵심 규칙 설계 지시서 | X |
| `developer` | 설계 지시서 → Domain→Repository→DTO→Service→Controller 구현 | O |
| `test-engineer` | Service·Controller 대표 성공 + 주요 실패 테스트 작성·실행 | O |
| `code-reviewer` | 변경 사항을 ARCHITECTURE·ADR·CLAUDE.md 기준으로 검증 | X |