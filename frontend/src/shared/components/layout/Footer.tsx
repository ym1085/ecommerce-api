/**
 * 모든 화면 하단에 붙는 푸터
 * 농부소이 소개와 쇼핑·이용 안내·채널 링크를 4열로 배치하고
 * 맨 아래 저작권 줄을 별도 구분선으로 분리한다
 */
function Footer() {
  const columns = [
    {
      title: '쇼핑',
      items: [
        { name: '농산물', href: '#' },
        { name: '주문조회', href: '#' },
        { name: '장바구니', href: '#' },
      ],
    },
    {
      title: '안내',
      items: [
        { name: '배송안내', href: '#' },
        { name: '자주 묻는 질문', href: '#' },
        { name: '문의', href: '#' },
      ],
    },
    {
      title: '농부소이',
      items: [
        { name: '농장일기', href: '#' },
        { name: 'YouTube', href: 'https://www.youtube.com/@Farmersoy' },
      ],
    },
  ];

  return (
    <footer className="border-t border-gray-100 bg-gray-50">
      <div className="mx-auto grid max-w-6xl grid-cols-2 gap-8 px-6 py-12 md:grid-cols-4">
        <div>
          <p className="text-lg font-bold text-emerald-700">농부소이</p>
          <p className="mt-3 text-sm leading-relaxed text-gray-500">
            주말농장에서 직접 키운
            <br />
            제철 먹거리를 소개합니다
          </p>
        </div>

        {columns.map((col) => (
          <div key={col.title}>
            <p className="text-sm font-semibold text-gray-800">{col.title}</p>
            <ul className="mt-3 space-y-2">
              {col.items.map((item) => (
                <li key={item.name}>
                  <a href={item.href} className="text-sm text-gray-500 transition hover:text-emerald-700">
                    {item.name}
                  </a>
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>

      <div className="border-t border-gray-100 px-6 py-4">
        <p className="mx-auto max-w-6xl text-xs text-gray-400">
          © 2026 농부소이. All rights reserved.
        </p>
      </div>
    </footer>
  );
}

export default Footer;
