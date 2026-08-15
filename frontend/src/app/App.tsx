/**
 * 애플리케이션 최상위 컴포넌트
 * 라우터를 꽂는 역할만 하고 화면 조립·UI 로직은 각 page가 담당한다
 */
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { RouterProvider } from 'react-router-dom';
import { router } from './routes';

const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
    </QueryClientProvider>
  );
}

export default App;
