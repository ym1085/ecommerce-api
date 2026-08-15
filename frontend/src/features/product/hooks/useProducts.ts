import { useQuery } from '@tanstack/react-query';
import { getProducts, getProduct } from '@/features/product/api/productApi';

/**
 * 상품 목록의 조회 상태와 캐시를 제공한다
 */
export function useProducts(page: number, size: number) {
  return useQuery({
    queryKey: ['products', page, size],
    queryFn: () => getProducts(page, size),
  });
}

/**
 * 상품 상세의 조회 상태와 캐시를 제공한다
 *
 * @param productId 상품 ID
 */
export function useProduct(productId: number) {
  return useQuery({
    queryKey: ['product', productId],
    queryFn: () => getProduct(productId),
    enabled: Number.isInteger(productId) && productId > 0,
  });
}
