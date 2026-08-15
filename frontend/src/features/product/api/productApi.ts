import type { PageResponse, ProductDetail, ProductSummary } from '@/features/product/model/product';
import { apiClient } from '@/shared/api/apiClient';

const PRODUCT_API_PATH = '/v1/products';

/**
 * 상품 목록을 페이지 단위로 조회한다
 */
export async function getProducts(
  page: number,
  size: number,
): Promise<PageResponse<ProductSummary>> {
  const response = await apiClient.get<PageResponse<ProductSummary>>(PRODUCT_API_PATH, {
    params: { page, size },
  });

  return response.data;
}

/**
 * 상품 상세를 조회한다
 *
 * @param productId 상품 ID
 */
export async function getProduct(productId: number): Promise<ProductDetail> {
  const response = await apiClient.get<ProductDetail>(`/v1/products/${productId}`);

  return response.data;
}
