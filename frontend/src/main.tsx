import React from 'react'; // React의 개발용 검사 기능인 StrictMode 사용
import App from './app/App'; // 화면에 표시할 최상위 컴포넌트
import { createRoot } from 'react-dom/client'; // React 컴포넌트를 HTML에 랜더링하는 함수
import './index.css'; // 이걸 import해야 브라우저가 Tailwind CSS를 실제로 받는다. 안 하면 파일만 있고 적용은 안 된다.

createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
);
