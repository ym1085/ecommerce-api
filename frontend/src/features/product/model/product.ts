/**
 * 상품 목록에 노출되는 판매 상태
 */
export type ProductStatus = 'ON_SALE' | 'UPCOMING' | 'OUT_OF_STOCK';

/**
 * 상품 목록 API 응답 한 건
 */
export interface ProductSummary {
  productId: number;
  productName: string;
  price: number;
  productStatus: ProductStatus;
  representativeImageUrl: string | null;
}

/**
 * Spring Data 페이지 응답
 */
export interface PageResponse<T> {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

/**
 * 상품 상세 목록 API 응답 한 건
 */
export interface ProductDetail {
  productId: number;
  productName: string;
  description: string;
  price: number;
  productStatus: ProductStatus;
  stockQuantity: number;
  imageUrls: string[];
}
