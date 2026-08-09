/**
 * 애플리케이션 최상위 컴포넌트
 * 레이아웃(Header, Hero, Footer)과 화면 콘텐츠를 조립하는 역할만 하고
 * 자체 UI 로직은 두지 않는다
 */
import Header from '../components/layout/Header';
import Hero from '../components/layout/Hero';
import Footer from '../components/layout/Footer';
import ProductGrid from '../components/product/ProductGrid';

function App() {
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

export default App;
