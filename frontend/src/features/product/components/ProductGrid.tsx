import { useProducts } from '@/features/product/hooks/useProducts';
import ProductCard from './ProductCard';

/**
 * 상품 목록을 격자로 배치하는 컴포넌트
 * 상품 목록 API 결과를 ProductCard로 반복 렌더링한다
 * 화면 폭에 따라 열 수를 1에서 3까지 늘린다
 */
function ProductGrid() {
  const { data, isPending, isError } = useProducts(0, 9);
  const products = data?.content ?? [];

  return (
    <section className="mx-auto max-w-[1200px] px-6 py-14 md:py-24 lg:px-10">
      <div className="mb-11">
        <p className="mb-3 text-sm font-bold tracking-widest text-emerald-700">NOW HARVESTING</p>
        <h2 className="text-3xl font-extrabold tracking-tight text-gray-950">지금 수확했어요</h2>
        <p className="mt-3 text-base text-gray-600">생산자가 정성껏 키운 제철 농산물을 만나보세요.</p>
      </div>

      {isPending && <p className="text-gray-500">상품을 불러오고 있습니다.</p>}

      {isError && <p className="text-red-600">상품을 불러오지 못했습니다.</p>}

      {!isPending && !isError && products.length === 0 && (
        <p className="text-gray-500">현재 판매 중인 상품이 없습니다.</p>
      )}

      {!isPending && !isError && products.length > 0 && (
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {products.map((product) => (
            <ProductCard key={product.productId} product={product} />
          ))}
        </div>
      )}
    </section>
  );
}

export default ProductGrid;
