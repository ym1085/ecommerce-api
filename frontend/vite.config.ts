import tailwindcss from '@tailwindcss/vite'; // Tailwind CSS를 Vite에서 사용할 수 있게 하는 플러그인
import react from '@vitejs/plugin-react'; // JSX/TSX를 JavaScript로 변환하고 React Fast Refresh 지원
import { defineConfig } from 'vite'; // Vite Config 설정하기 위한 lib
import { fileURLToPath, URL } from 'node:url'; // @ alias를 src 절대경로로 풀기 위한 URL 변환

export default defineConfig({
  // Vite가 개발 서버 실행 / 빌드할 때 아래 플러그인의 기능 같이 적용
  plugins: [react(), tailwindcss()],
  // tsconfig의 "@/*"와 짝을 맞춰 번들러도 같은 경로로 해석하게 한다
  resolve: {
    alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) },
  },
  server: {
    port: 5173,
  },
});
