/**
 * 모든 화면 하단에 붙는 푸터
 * 소이팜마켓 소개와 쇼핑·이용 안내·스토리 링크를 4열로 배치한다
 */
function Footer() {
  const columns = [
    {
      title: '쇼핑',
      items: [
        { name: '전체상품', href: '#' },
        { name: '제철 농산물', href: '#' },
        { name: '가공식품', href: '#' },
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
      title: '스토리',
      items: [
        { name: '소이팜마켓 이야기', href: '#' },
        { name: 'YouTube', href: 'https://www.youtube.com/@Farmersoy' },
      ],
    },
  ];

  return (
    <footer className="border-t border-gray-100 bg-gray-50">
      <div className="mx-auto grid max-w-6xl grid-cols-2 gap-8 px-6 py-12 md:grid-cols-4">
        <div>
          <p className="text-lg font-bold text-emerald-700">소이팜마켓</p>
          <p className="mt-3 text-sm leading-relaxed text-gray-500">
            생산자가 키운 제철 먹거리와
            <br />
            그 안의 이야기를 전합니다
          </p>
          <div className="mt-5 text-sm">
            <p className="text-lg font-bold text-emerald-700">계좌 안내</p>
            <p className="mt-2 text-gray-500">우리은행 1002-933-607032</p>
          </div>
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
          © 2026 소이팜마켓. Created by ymkim · soy
        </p>
      </div>
    </footer>
  );
}

export default Footer;
