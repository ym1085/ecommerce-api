import { products } from '../../mocks/products';
import ProductCard from './ProductCard';

/**
 * 상품 목록을 격자로 배치하는 컴포넌트
 * mock 데이터를 순회하며 ProductCard를 반복 렌더링한다
 * 화면 폭에 따라 열 수를 1에서 3까지 늘린다
 */
function ProductGrid() {
  return (
    <section className="mx-auto max-w-[1200px] px-6 py-24 lg:px-10">
      <div className="mb-11">
        <p className="mb-3 text-sm font-bold tracking-widest text-emerald-700">THIS WEEK</p>
        <h2 className="text-3xl font-extrabold tracking-tight text-gray-950">이번 주 수확</h2>
        <p className="mt-3 text-base text-gray-600">농부소이의 주말농장에서 직접 키운 제철 농산물입니다.</p>
      </div>

      <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {products.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </section>
  );
}

export default ProductGrid;
