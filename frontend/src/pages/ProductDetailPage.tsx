import { useParams } from 'react-router-dom';
import { useProduct } from '@/features/product/hooks/useProducts';
import Header from '@/shared/components/layout/Header';
import Footer from '@/shared/components/layout/Footer';

function ProductDetailPage() {
  const { productId } = useParams<{ productId: string }>();
  const parsedProductId = Number(productId);
  const isInvalidProductId = !Number.isInteger(parsedProductId) || parsedProductId < 1;
  const { data: product, isPending, isError } = useProduct(parsedProductId);

  return (
    <>
      <Header />
      <main className="mx-auto min-h-[600px] max-w-[1200px] px-6 py-12 lg:px-10">
        {isInvalidProductId && <p className="text-red-600">잘못된 상품 번호입니다.</p>}
        {!isInvalidProductId && isPending && <p className="text-gray-500">상품을 불러오고 있습니다.</p>}
        {!isInvalidProductId && isError && <p className="text-red-600">상품을 불러오지 못했습니다.</p>}
        {!isInvalidProductId && !isPending && !isError && !product && <p className="text-gray-500">상품을 찾을 수 없습니다.</p>}
        {!isInvalidProductId && !isPending && !isError && product && (
          <section className="grid gap-10 lg:grid-cols-2">
            <div className="aspect-square overflow-hidden rounded-2xl bg-gray-100">
              {product.imageUrls[0] ? (
                <img src={product.imageUrls[0]} alt={product.productName} className="h-full w-full object-cover" />
              ) : (
                <div className="flex h-full items-center justify-center text-gray-400">상품 이미지가 없습니다.</div>
              )}
            </div>
            <div>
              <h1 className="text-3xl font-bold">{product.productName}</h1>
              <p className="mt-5 text-2xl font-bold">{product.price.toLocaleString()}원</p>
              <p className="mt-3">재고 {product.stockQuantity}개</p>
              <p className="mt-8 leading-7">{product.description}</p>
            </div>
          </section>
        )}
      </main>
      <Footer />
    </>
  );
}

export default ProductDetailPage;
