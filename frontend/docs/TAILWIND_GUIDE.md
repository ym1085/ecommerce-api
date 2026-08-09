# Tailwind CSS 가이드

> 이 문서는 `frontend`에서 사용하는 Tailwind CSS 4 클래스의 의미와 조합 기준을 정리한다.
> 색상과 컴포넌트 디자인 기준은 루트의 `docs/UI_GUIDE.md`를 따른다.

## 적용 구조

| 위치             | 역할                                                       |
| ---------------- | ---------------------------------------------------------- |
| `src/index.css`  | Tailwind 기본 스타일과 유틸리티 클래스를 전역으로 불러온다 |
| `vite.config.ts` | Vite 빌드 과정에 Tailwind 플러그인을 연결한다              |
| `src/main.tsx`   | `index.css`를 애플리케이션에 적용한다                      |
| `className`      | 컴포넌트에 Tailwind 유틸리티 클래스를 조합한다             |

```text
vite.config.ts
  → index.css
    → main.tsx
      → 컴포넌트의 className
```

## 클래스 읽는 법

```tsx
<header className="sticky top-0 border-b bg-white px-6 py-4" />
```

| 클래스     | 의미                               |
| ---------- | ---------------------------------- |
| `sticky`   | 스크롤 위치에 따라 요소를 고정한다 |
| `top-0`    | 위쪽 기준 위치를 `0`으로 지정한다  |
| `border-b` | 아래쪽 테두리를 표시한다           |
| `bg-white` | 배경색을 흰색으로 지정한다         |
| `px-6`     | 좌우 안쪽 여백을 지정한다          |
| `py-4`     | 위아래 안쪽 여백을 지정한다        |

클래스는 보통 다음 순서로 읽는다.

```text
상태 조건 → 배치 → 크기 → 여백 → 색상 → 글자 → 효과
```

## 배치

| 클래스         | CSS 의미                | 용도                                     |
| -------------- | ----------------------- | ---------------------------------------- |
| `block`        | `display: block`        | 한 줄 전체를 차지하는 요소               |
| `inline-block` | `display: inline-block` | 인라인 배치와 크기 지정이 모두 필요할 때 |
| `hidden`       | `display: none`         | 요소 숨김                                |
| `flex`         | `display: flex`         | 한 방향 정렬                             |
| `grid`         | `display: grid`         | 행과 열을 이용한 배치                    |
| `relative`     | `position: relative`    | 자식의 절대 위치 기준                    |
| `absolute`     | `position: absolute`    | 기준 요소 안에서 위치 지정               |
| `sticky`       | `position: sticky`      | 스크롤 중 지정 위치에 고정               |
| `inset-0`      | 상하좌우 위치 `0`       | 부모 영역 전체 덮기                      |

## Flex 정렬

| 클래스            | 의미                                       |
| ----------------- | ------------------------------------------ |
| `flex-row`        | 자식을 가로로 배치한다                     |
| `flex-col`        | 자식을 세로로 배치한다                     |
| `items-start`     | 교차축 시작점에 정렬한다                   |
| `items-center`    | 교차축 중앙에 정렬한다                     |
| `items-end`       | 교차축 끝점에 정렬한다                     |
| `justify-start`   | 주축 시작점에 정렬한다                     |
| `justify-center`  | 주축 중앙에 정렬한다                       |
| `justify-between` | 양 끝에 배치하고 사이 공간을 균등하게 둔다 |
| `gap-4`           | 자식 사이에 `1rem` 간격을 둔다             |
| `flex-1`          | 남은 공간을 채운다                         |

## Grid 배치

| 클래스        | 의미                              |
| ------------- | --------------------------------- |
| `grid-cols-1` | 한 열로 배치한다                  |
| `grid-cols-2` | 두 열로 배치한다                  |
| `grid-cols-3` | 세 열로 배치한다                  |
| `grid-cols-4` | 네 열로 배치한다                  |
| `gap-4`       | 행과 열 사이에 `1rem` 간격을 둔다 |
| `gap-x-4`     | 열 사이 간격만 지정한다           |
| `gap-y-4`     | 행 사이 간격만 지정한다           |

## 여백

| 접두어                 | 의미                             | 예시        |
| ---------------------- | -------------------------------- | ----------- |
| `m`                    | 바깥 여백 전체                   | `m-4`       |
| `mt`, `mr`, `mb`, `ml` | 위, 오른쪽, 아래, 왼쪽 바깥 여백 | `mt-3`      |
| `mx`, `my`             | 좌우, 위아래 바깥 여백           | `mx-auto`   |
| `p`                    | 안쪽 여백 전체                   | `p-6`       |
| `pt`, `pr`, `pb`, `pl` | 위, 오른쪽, 아래, 왼쪽 안쪽 여백 | `pt-4`      |
| `px`, `py`             | 좌우, 위아래 안쪽 여백           | `px-6 py-4` |
| `space-x`, `space-y`   | 자식 요소 사이 간격              | `space-y-8` |

자주 사용하는 간격 값은 다음과 같다.

| 값   | 실제 크기        |
| ---- | ---------------- |
| `1`  | `0.25rem` / 4px  |
| `2`  | `0.5rem` / 8px   |
| `3`  | `0.75rem` / 12px |
| `4`  | `1rem` / 16px    |
| `6`  | `1.5rem` / 24px  |
| `8`  | `2rem` / 32px    |
| `12` | `3rem` / 48px    |

## 크기

| 클래스          | 의미                               |
| --------------- | ---------------------------------- |
| `w-full`        | 너비를 부모의 100%로 지정한다      |
| `h-full`        | 높이를 부모의 100%로 지정한다      |
| `size-10`       | 너비와 높이를 같은 값으로 지정한다 |
| `h-80`          | 높이를 `20rem`으로 지정한다        |
| `max-w-lg`      | 최대 너비를 `32rem`으로 제한한다   |
| `max-w-6xl`     | 최대 너비를 `72rem`으로 제한한다   |
| `max-w-7xl`     | 최대 너비를 `80rem`으로 제한한다   |
| `aspect-square` | 가로세로 비율을 1:1로 유지한다     |

## 글자

| 클래스            | 의미                    |
| ----------------- | ----------------------- |
| `text-xs`         | 매우 작은 글자 크기     |
| `text-sm`         | 작은 글자 크기          |
| `text-base`       | 기본 글자 크기          |
| `text-xl`         | 큰 글자 크기            |
| `text-2xl`        | 페이지 제목 크기        |
| `text-4xl`        | 대표 문구 크기          |
| `font-normal`     | 기본 굵기               |
| `font-medium`     | 중간 굵기               |
| `font-semibold`   | 강조 굵기               |
| `font-bold`       | 강한 강조 굵기          |
| `text-left`       | 왼쪽 정렬               |
| `text-center`     | 가운데 정렬             |
| `leading-relaxed` | 줄 간격을 넓게 지정한다 |
| `line-through`    | 취소선을 표시한다       |

## 색상

색상 클래스는 `속성-색상-단계` 형식으로 작성한다.

```text
bg-gray-100
text-gray-900
border-indigo-600
```

| 접두어   | 적용 대상      | 예시                      |
| -------- | -------------- | ------------------------- |
| `bg`     | 배경색         | `bg-white`, `bg-gray-100` |
| `text`   | 글자색         | `text-gray-900`           |
| `border` | 테두리색       | `border-gray-200`         |
| `ring`   | 포커스 링 색상 | `ring-indigo-600`         |

색상 단계는 일반적으로 숫자가 작을수록 밝고, 클수록 어둡다.

```text
50 → 100 → 200 → 300 → 400 → 500 → 600 → 700 → 800 → 900 → 950
```

투명도는 `/` 뒤에 백분율을 붙인다.

```tsx
<div className="bg-black/40" />
```

## 테두리와 모서리

| 클래스            | 의미                     |
| ----------------- | ------------------------ |
| `border`          | 전체 테두리를 표시한다   |
| `border-t`        | 위쪽 테두리를 표시한다   |
| `border-b`        | 아래쪽 테두리를 표시한다 |
| `rounded-md`      | 중간 크기의 둥근 모서리  |
| `rounded-lg`      | 큰 둥근 모서리           |
| `rounded-full`    | 원형 또는 캡슐 모양      |
| `overflow-hidden` | 영역 밖의 내용을 숨긴다  |

## 이미지

| 클래스           | 의미                                    |
| ---------------- | --------------------------------------- |
| `object-cover`   | 비율을 유지하며 영역을 가득 채운다      |
| `object-contain` | 이미지 전체가 보이도록 영역 안에 맞춘다 |
| `bg-cover`       | 배경 이미지가 영역을 가득 채우게 한다   |
| `bg-center`      | 배경 이미지의 중심을 기준으로 배치한다  |
| `aspect-square`  | 상품 이미지를 1:1 비율로 유지한다       |

## 상태와 효과

| 클래스              | 적용 시점                          | 예시                      |
| ------------------- | ---------------------------------- | ------------------------- |
| `hover:*`           | 마우스를 올렸을 때                 | `hover:bg-indigo-700`     |
| `focus:*`           | 키보드나 입력 포커스를 받았을 때   | `focus:ring-2`            |
| `disabled:*`        | 비활성 상태일 때                   | `disabled:opacity-40`     |
| `group-hover:*`     | 부모 `group`에 마우스를 올렸을 때  | `group-hover:scale-105`   |
| `transition`        | 속성 변화를 부드럽게 처리한다      | `transition duration-300` |
| `transition-colors` | 색상 변화만 부드럽게 처리한다      | `transition-colors`       |
| `shadow-md`         | 중간 크기의 그림자를 표시한다      | `hover:shadow-md`         |
| `scale-105`         | 요소를 105%로 확대한다             | `group-hover:scale-105`   |
| `cursor-pointer`    | 마우스 포인터를 클릭 형태로 바꾼다 | `cursor-pointer`          |

`group-hover`를 사용할 때는 부모에 `group`을 함께 지정한다.

```tsx
<div className="group">
  <img className="transition group-hover:scale-105" />
</div>
```

## 반응형

반응형 클래스는 `화면크기:클래스` 형식으로 작성한다. 지정한 크기 이상에서 적용된다.

| 접두어 | 최소 화면 너비 | 예시             |
| ------ | -------------- | ---------------- |
| `sm`   | 640px          | `sm:grid-cols-3` |
| `md`   | 768px          | `md:grid-cols-3` |
| `lg`   | 1024px         | `lg:grid-cols-4` |
| `xl`   | 1280px         | `xl:px-8`        |
| `2xl`  | 1536px         | `2xl:max-w-7xl`  |

모바일 스타일을 기본으로 작성하고 큰 화면의 변경만 접두어로 추가한다.

```tsx
<div className="grid grid-cols-2 gap-6 md:grid-cols-3 lg:grid-cols-4" />
```

## 현재 프로젝트 사용 예시

| 컴포넌트             | 클래스 조합                                                         | 역할                                 |
| -------------------- | ------------------------------------------------------------------- | ------------------------------------ |
| `Header`             | `sticky top-0 border-b bg-white px-6 py-4`                          | 상단 고정, 테두리, 배경, 여백        |
| `Hero`               | `relative flex h-80 items-center justify-center bg-cover bg-center` | 대표 배너 크기와 중앙 정렬           |
| `Hero` 오버레이      | `absolute inset-0 bg-black/40`                                      | 배경 이미지 위에 반투명 검은 막 표시 |
| `ProductGrid`        | `grid grid-cols-2 gap-6 md:grid-cols-3 lg:grid-cols-4`              | 화면 너비에 따라 상품 열 수 변경     |
| `ProductCard` 이미지 | `aspect-square overflow-hidden rounded-lg bg-gray-100`              | 이미지 비율 고정과 모서리 처리       |
| `ProductCard` 확대   | `transition duration-300 group-hover:scale-105`                     | 카드 호버 시 이미지 확대             |

## 작성 규칙

| 규칙          | 기준                                                       |
| ------------- | ---------------------------------------------------------- |
| 클래스 정렬   | Prettier의 Tailwind 플러그인 결과를 따른다                 |
| 중복 스타일   | 같은 조합이 반복되면 컴포넌트로 분리한다                   |
| 동적 값       | 실제 런타임 값만 `style` 속성을 사용한다                   |
| 임의 값       | 디자인 토큰으로 표현할 수 없을 때만 `[값]` 문법을 사용한다 |
| 조건부 클래스 | 완성된 클래스 문자열을 조건에 따라 선택한다                |
| 전역 스타일   | 폰트, 기본 배경, CSS 변수처럼 애플리케이션 공통 값만 둔다  |

Tailwind가 클래스를 찾을 수 있도록 클래스명을 문자열 조합으로 만들지 않는다.

```tsx
// 사용하지 않는다
const className = `bg-${color}-500`;

// 완성된 클래스명을 선택한다
const colorClass = color === 'green' ? 'bg-green-500' : 'bg-red-500';
```

## 확인 순서

1. 원하는 CSS 속성이 배치, 여백, 글자, 색상 중 어디에 속하는지 판단한다.
2. 기본 모바일 스타일을 먼저 작성한다.
3. 필요한 화면 크기에만 `sm:`, `md:`, `lg:`를 추가한다.
4. 상호작용이 있으면 `hover:`, `focus:`, `disabled:` 상태를 추가한다.
5. Prettier로 클래스 순서를 정리한다.
