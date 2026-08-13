# 프론트엔드 패키지 구조

## 현재 구조

```text
frontend/
├─ public/                         # 브라우저가 직접 제공하는 정적 파일
│  └─ images/                      # 로고, 배너, 상품 이미지
├─ docs/                           # 프론트엔드 개발 문서
├─ src/
│  ├─ main.tsx                     # React 애플리케이션 마운트
│  ├─ index.css                    # 전역 스타일
│  ├─ app/                         # 앱 전역 조립과 라우팅
│  │  ├─ App.tsx                   # RouterProvider 연결
│  │  └─ routes.tsx                # URL과 Page 매핑
│  ├─ pages/                       # URL에 직접 연결되는 화면
│  │  └─ HomePage.tsx              # / 화면 조립
│  ├─ features/                    # 기능별로 응집된 코드
│  │  ├─ home/
│  │  │  └─ components/
│  │  │     └─ Hero.tsx            # 홈 화면 전용 대표 배너
│  │  └─ product/
│  │     ├─ components/
│  │     │  ├─ ProductCard.tsx     # 상품 한 건 UI
│  │     │  └─ ProductGrid.tsx     # 상품 목록 UI
│  │     └─ model/
│  │        └─ product.ts          # 상품 타입과 임시 데이터
│  └─ shared/                      # 특정 기능에 속하지 않는 공통 코드
│     └─ components/
│        └─ layout/
│           ├─ Header.tsx
│           └─ Footer.tsx
```

## 배치 기준

| 위치 | 책임 | 예시 |
| --- | --- | --- |
| `app` | 앱 전체에 한 번만 존재하는 설정 | 라우터, Provider |
| `pages` | URL 단위 화면을 조립 | `HomePage`, `ProductDetailPage` |
| `features/{기능}` | 하나의 사용자 기능에 속한 UI·데이터·상태 | `product`, `cart`, `auth` |
| `shared` | 두 개 이상의 기능에서 재사용하는 코드 | Header, Button, Modal |

`pages`는 기능 코드를 직접 구현하는 곳이 아니라 `features`와 `shared`의 컴포넌트를 조립하는 곳이다. `Hero`는 `/` URL 자체가 아니라 홈 화면 안의 섹션이므로 `features/home/components`에 둔다.

## features 확장 규칙

`features`는 앞으로 여러 개가 되는 것이 정상이다. 기능마다 필요한 폴더만 만들고, 사용하지 않는 빈 폴더는 만들지 않는다.

```text
features/
├─ home/
│  └─ components/
├─ product/
│  ├─ api/                         # 상품 API 요청
│  ├─ components/                  # ProductCard, ProductGrid, ProductInfo
│  ├─ hooks/                       # useProducts, useProduct
│  └─ model/                       # Product 타입, 조회 조건, 상태
├─ cart/
│  ├─ api/
│  ├─ components/
│  ├─ hooks/
│  └─ model/
├─ order/
├─ auth/
├─ review/
└─ user/
```

기능 내부 폴더의 역할은 다음과 같다.

| 폴더 | 넣는 코드 |
| --- | --- |
| `api` | 서버 요청 함수와 요청·응답 변환 |
| `components` | 해당 기능에서만 쓰는 UI |
| `hooks` | API 호출, 상태 조합 등 기능 전용 Hook |
| `model` | 타입, 상태, 상수, 순수 데이터 처리 |

둘 이상의 기능에서 재사용하게 된 컴포넌트만 `shared`로 옮긴다. 예를 들어 `ProductCard`는 상품 기능에 남기고, 어떤 기능에서도 사용할 수 있는 `Button`이나 `Pagination`은 `shared/components`에 둔다.
