# UI 디자인 가이드

> 이 가이드는 B2C 쇼핑몰 프론트엔드 구현 시 준수해야 할 디자인 기준이다.
> 스택: **Tailwind CSS** / 테마: **라이트모드 기본** / 폰트: **Pretendard**

---

## 디자인 원칙

1. **상품 중심** — UI는 상품 이미지와 가격이 주인공이다. 크롬(chrome)은 최소화한다.
2. **신뢰와 명확성** — 구매 흐름(탐색 → 장바구니 → 주문 → 결제)에서 사용자가 현재 위치와 다음 행동을 명확히 알 수 있어야 한다.
3. **행동 유도 일관성** — CTA(Call-to-Action) 버튼은 페이지 당 하나의 주요 액션만 강조한다. 버튼 경쟁을 만들지 않는다.

---

## 안티패턴 — 하지 마라

| 금지 사항 | 이유 |
| --- | --- |
| `backdrop-filter: blur()` / glassmorphism | 상품 이미지와 겹치면 가독성 저하 |
| gradient-text (텍스트 그라데이션) | 가격·상품명 등 핵심 정보의 가독성 해침 |
| box-shadow 글로우 / 네온 효과 | 쇼핑몰 컨텍스트에서 신뢰감 저하 |
| 배경 gradient orb (blur-3xl 원형 장식) | 상품 카드 배경을 오염시킴 |
| 모든 카드에 동일한 `rounded-2xl` | 균일한 둥근 모서리는 저품질 템플릿 느낌 |
| 다크모드 기본값 | 상품 이미지 색상 왜곡, 가격 비교 난해 |
| 자동 재생 배너 슬라이더 | 사용자 집중 분산, 접근성 저해 |

---

## 색상

### 브랜드 팔레트

| 토큰 | Hex | Tailwind | 용도 |
| --- | --- | --- | --- |
| `primary-600` | `#4F46E5` | `indigo-600` | 링크, 포커스 링, 주요 액션 |
| `primary-700` | `#4338CA` | `indigo-700` | primary 호버 상태 |
| `accent-500` | `#F97316` | `orange-500` | 구매 CTA 버튼, 프로모션 배지 |
| `accent-600` | `#EA6C00` | `orange-600` | accent 호버 상태 |

### 배경

| 용도 | 값 | Tailwind |
| --- | --- | --- |
| 페이지 기본 | `#FFFFFF` | `bg-white` |
| 섹션 구분 | `#F9FAFB` | `bg-gray-50` |
| 카드 | `#FFFFFF` | `bg-white` |
| 비활성 영역 | `#F3F4F6` | `bg-gray-100` |

### 텍스트

| 용도 | 값 | Tailwind |
| --- | --- | --- |
| 주 텍스트 (제목·가격) | `#111827` | `text-gray-900` |
| 본문 | `#374151` | `text-gray-700` |
| 보조 (레이블·메타) | `#6B7280` | `text-gray-500` |
| 비활성·플레이스홀더 | `#9CA3AF` | `text-gray-400` |

### 시맨틱 색상

| 상태 | 배경 | 텍스트 | 용도 |
| --- | --- | --- | --- |
| 성공 | `#DCFCE7` | `#16A34A` | 결제 완료, 배송 완료 |
| 경고 | `#FEF3C7` | `#D97706` | 재고 부족, 배송 지연 |
| 오류 | `#FEE2E2` | `#DC2626` | 결제 실패, 입력 오류 |
| 정보 | `#DBEAFE` | `#2563EB` | 배송 중, 처리 중 |

### 보더

| 용도 | 값 | Tailwind |
| --- | --- | --- |
| 기본 카드·입력 | `#E5E7EB` | `border-gray-200` |
| 강조 (포커스) | `#4F46E5` | `border-indigo-600` |
| 에러 | `#DC2626` | `border-red-600` |

---

## 타이포그래피

폰트: `Pretendard` (한국어 최적화). fallback: `-apple-system, BlinkMacSystemFont, sans-serif`

| 용도 | 크기 | 굵기 | Tailwind |
| --- | --- | --- | --- |
| 페이지 제목 | 24px | 700 | `text-2xl font-bold text-gray-900` |
| 섹션 제목 | 20px | 600 | `text-xl font-semibold text-gray-900` |
| 카드 제목 / 상품명 | 16px | 500 | `text-base font-medium text-gray-900` |
| 본문 | 14px | 400 | `text-sm text-gray-700 leading-relaxed` |
| 가격 (주요) | 20px | 700 | `text-xl font-bold text-gray-900` |
| 가격 (할인 전) | 14px | 400 | `text-sm text-gray-400 line-through` |
| 배지·레이블 | 12px | 500 | `text-xs font-medium` |
| 버튼 텍스트 | 14px | 600 | `text-sm font-semibold` |

---

## 컴포넌트

### 버튼

```
Primary CTA (구매·결제):
  bg-orange-500 text-white hover:bg-orange-600
  px-6 py-3 rounded-lg text-sm font-semibold
  transition-colors duration-150

Secondary (일반 액션):
  bg-indigo-600 text-white hover:bg-indigo-700
  px-5 py-2.5 rounded-lg text-sm font-semibold
  transition-colors duration-150

Outline (취소·목록 이동):
  border border-gray-300 text-gray-700 hover:bg-gray-50
  px-5 py-2.5 rounded-lg text-sm font-semibold
  transition-colors duration-150

Destructive (삭제):
  text-red-600 hover:text-red-700 hover:bg-red-50
  px-4 py-2 rounded-md text-sm font-medium
  transition-colors duration-150
```

### 입력 필드

```
기본:
  border border-gray-200 rounded-lg px-4 py-3
  text-sm text-gray-900 placeholder-gray-400
  focus:outline-none focus:ring-2 focus:ring-indigo-600 focus:border-transparent

에러 상태:
  border-red-500 focus:ring-red-500
  + 하단 text-xs text-red-600 에러 메시지
```

### 카드

```
상품 카드:
  bg-white border border-gray-200 rounded-lg overflow-hidden
  hover:shadow-md transition-shadow duration-200

일반 콘텐츠 카드:
  bg-white border border-gray-200 rounded-lg p-6

섹션 카드 (주문 요약 등):
  bg-gray-50 border border-gray-200 rounded-lg p-5
```

### 배지 (주문·배송 상태)

```
주문 대기:   bg-yellow-100 text-yellow-800  rounded-full px-2.5 py-0.5 text-xs font-medium
결제 완료:   bg-blue-100   text-blue-800
배송 준비:   bg-purple-100 text-purple-800
배송 중:     bg-indigo-100 text-indigo-800
배송 완료:   bg-green-100  text-green-800
주문 취소:   bg-gray-100   text-gray-600
결제 실패:   bg-red-100    text-red-800
```

### 페이지네이션

```
컨테이너: flex items-center justify-center gap-1 mt-8

페이지 번호:
  w-9 h-9 flex items-center justify-center rounded-md text-sm
  기본: text-gray-700 hover:bg-gray-100
  현재: bg-indigo-600 text-white font-semibold

이전/다음 버튼:
  동일 크기, border border-gray-200 text-gray-500
  비활성: opacity-40 cursor-not-allowed
```

---

## 쇼핑몰 특화 패턴

### 상품 카드

```
레이아웃: 이미지(상단) + 정보(하단)
이미지: aspect-square object-cover (1:1 비율 고정)
정보 영역: p-4
  - 상품명: font-medium text-gray-900 2줄 제한 (line-clamp-2)
  - 가격: font-bold text-gray-900
  - 할인율: text-orange-500 font-semibold
  - 원가 (취소선): text-gray-400 text-sm line-through
품절 오버레이: absolute inset-0 bg-white/60 + "품절" 텍스트 중앙
```

### 장바구니 항목

```
레이아웃: 이미지(좌, 80×80) + 상품명+옵션(중, flex-1) + 수량+가격(우)
수량 조절: border rounded-md, 버튼(-/+) + 숫자 인라인
삭제 버튼: 우측 상단 text-gray-400 hover:text-red-500
```

### 주문 요약 패널

```
위치: 우측 사이드바 (lg:w-80) 또는 하단 (모바일)
구성:
  - 소계 / 배송비 / 할인 / 최종 합계 (bold 강조)
  - 구분선: border-t border-gray-200
  - CTA: bg-orange-500 w-full
```

### 주문 상세 타임라인

```
세로 타임라인: 왼쪽 점(dot) + 선(line) + 우측 텍스트
완료 단계: dot bg-indigo-600, 텍스트 text-gray-900
현재 단계: dot ring-2 ring-indigo-600 bg-white, 텍스트 font-semibold
미래 단계: dot bg-gray-200, 텍스트 text-gray-400
```

---

## 레이아웃

| 항목 | 값 |
| --- | --- |
| 최대 너비 | `max-w-7xl mx-auto` (전체 페이지) |
| 콘텐츠 패딩 | `px-4 sm:px-6 lg:px-8` |
| 상품 그리드 | `grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4` |
| 섹션 간격 | `space-y-8` 또는 `py-8` |
| 폼 최대 너비 | `max-w-lg` |

---

## 아이콘

- 라이브러리: `heroicons` (outline 스타일 기본)
- stroke-width: `1.5`
- 크기: `w-5 h-5` (기본), `w-4 h-4` (인라인/배지 내부)
- 아이콘을 둥근 배경 박스(컨테이너)로 감싸지 않는다. 텍스트와 인라인으로 배치한다.
- 장식 목적(의미 없는) 아이콘 사용 금지.

---

## 애니메이션

허용:
- `transition-colors duration-150` — 버튼·링크 색상 전환
- `transition-shadow duration-200` — 카드 호버 그림자
- `transition-opacity duration-200` — 모달·드롭다운 등장

금지:
- 페이지 진입 시 카드 순차 fade-in (스크롤 차단, 체감 속도 저하)
- 로딩 스피너 외 회전 애니메이션
- `animate-bounce`, `animate-ping` (배너/알림 외 사용 금지)
- 마우스 추적 파티클·배경 움직임
