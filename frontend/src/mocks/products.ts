/**
 * 홈 화면 상품 목록용 가짜 데이터
 * 백엔드 상품 API가 아직 없어 화면 개발용으로 임시 사용한다
 * 이미지는 placeholder(picsum)이며 실제 연동 시 통째로 교체한다
 */
export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  imageUrl: string;
  status: 'ON_SALE' | 'UPCOMING' | 'OUT_OF_STOCK';
}

export const products: Product[] = [
  {
    id: 1,
    name: '햇 감자 3kg',
    description: '주말농장에서 직접 수확한 햇감자',
    price: 9900,
    imageUrl: 'https://picsum.photos/seed/potato/600/450',
    status: 'ON_SALE',
  },
  {
    id: 2,
    name: '청양고추 500g',
    description: '알싸한 맛이 살아 있는 제철 고추',
    price: 6900,
    imageUrl: 'https://picsum.photos/seed/pepper/600/450',
    status: 'UPCOMING',
  },
  {
    id: 3,
    name: '양배추 1통',
    description: '파종부터 수확까지 기록한 양배추',
    price: 5900,
    imageUrl: 'https://picsum.photos/seed/cabbage/600/450',
    status: 'ON_SALE',
  },
  {
    id: 4,
    name: '공심채 300g',
    description: '여름 농장에서 자란 아삭한 공심채',
    price: 4500,
    imageUrl: 'https://picsum.photos/seed/waterspinach/600/450',
    status: 'UPCOMING',
  },
  {
    id: 5,
    name: '참외 2kg',
    description: '여름 햇볕을 맞으며 자란 제철 참외',
    price: 15900,
    imageUrl: 'https://picsum.photos/seed/melon/600/450',
    status: 'OUT_OF_STOCK',
  },
  {
    id: 6,
    name: '애플수박 1통',
    description: '초보 농부의 도전으로 키운 애플수박',
    price: 12900,
    imageUrl: 'https://picsum.photos/seed/watermelon/600/450',
    status: 'OUT_OF_STOCK',
  },
];
