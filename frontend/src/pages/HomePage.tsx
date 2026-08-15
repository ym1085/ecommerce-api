/**
 * 홈 화면(/)
 * 공통 레이아웃(Header, Footer)과 홈 전용 Hero, 상품 목록을 조립한다
 */
import Header from '@/shared/components/layout/Header';
import Footer from '@/shared/components/layout/Footer';
import Hero from '@/features/home/components/Hero';
import ProductGrid from '@/features/product/components/ProductGrid';

function HomePage() {
  return (
    <div>
      <Header />
      <Hero />
      <main>
        <ProductGrid />
      </main>
      <Footer />
    </div>
  );
}

export default HomePage;
