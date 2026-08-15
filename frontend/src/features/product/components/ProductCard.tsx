import type { ProductSummary } from '@/features/product/model/product';
import { Link } from 'react-router-dom';

/**
 * 상품 한 건을 보여주는 카드
 * 대표 이미지, 판매 상태, 이름, 가격을 한 카드 안에 표시한다
 */
interface ProductCardProps {
  product: ProductSummary;
}

function ProductCard({ product }: ProductCardProps) {
  const status = {
    ON_SALE: { label: '판매 중', className: 'bg-emerald-700 text-white' },
    UPCOMING: { label: '수확 예정', className: 'bg-amber-100 text-amber-800' },
    OUT_OF_STOCK: { label: '품절', className: 'bg-gray-800 text-white' },
  }[product.productStatus];

  return (
    <Link to={`/products/${product.productId}`}>
      <article className="group cursor-pointer overflow-hidden rounded-2xl border border-gray-200 bg-white">
        <div className="relative aspect-[4/3] overflow-hidden bg-gray-100">
          {product.representativeImageUrl ? (
            <img
              src={product.representativeImageUrl}
              alt={product.productName}
              className="h-full w-full object-cover transition duration-300 group-hover:scale-105"
            />
          ) : (
            <div className="flex h-full items-center justify-center text-sm text-gray-400">
              이미지 준비 중
            </div>
          )}
          <span
            className={`absolute top-4 left-4 rounded-md px-2.5 py-1 text-xs font-semibold ${status.className}`}
          >
            {status.label}
          </span>
        </div>

        <div className="p-5">
          <h3 className="text-lg font-bold text-gray-950">{product.productName}</h3>
          <p className="mt-5 text-lg font-extrabold text-gray-950">
            {product.price.toLocaleString()}원
          </p>
        </div>
      </article>
    </Link>
  );
}

export default ProductCard;
