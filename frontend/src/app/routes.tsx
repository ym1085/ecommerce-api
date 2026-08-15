/**
 * 애플리케이션 라우트 정의
 * URL 한 개당 page 한 개를 매핑하고 화면이 늘면 여기에 추가한다
 */
import { createBrowserRouter } from 'react-router-dom';
import HomePage from '@/pages/HomePage';
import ProductDetailPage from '@/pages/ProductDetailPage';

export const router = createBrowserRouter([
  { path: '/', element: <HomePage /> },
  { path: '/products/:productId', element: <ProductDetailPage /> },
]);
