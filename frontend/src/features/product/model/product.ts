/**
 * 상품 목록에 노출되는 판매 상태
 */
export type ProductStatus = 'ON_SALE' | 'UPCOMING' | 'OUT_OF_STOCK';

/**
 * 상품 목록 API 응답 한건 - Class
 */
export interface ProductSummary {
  productId: number;
  productName: string;
  price: number;
  productStatus: ProductStatus;
  representativeImageUrl: string;
}

/**
 * 화면 렌더링용 상품 모델
 * ProductCard·ProductGrid가 이 모양을 그대로 그린다
 */
export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  imageUrl: string;
  status: ProductStatus;
}

/**
 * 화면 개발용 임시 목데이터
 * 백엔드 상품 API 연동 전까지만 쓰고 연동 시 통째로 교체한다
 */
export const products: Product[] = [
  // TODO: 상품 목데이터를 여기에 채운다
];