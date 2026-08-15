/**
 * 모든 화면 상단에 고정되는 헤더
 * 로고와 전역 내비게이션을 담으며 스크롤해도 상단에 붙어 있다
 */
function Header() {
  const navigationItems = [
    {
      name: '전체상품',
      children: ['과일', '채소', '곡물·잡곡', '김치·반찬', '즉석식품', '장류'],
    },
    {
      name: '제철 농산물',
      children: ['과일', '채소', '곡물·잡곡'],
    },
    {
      name: '가공식품',
      children: ['김치·반찬', '즉석식품', '장류'],
    },
    {
      name: '스토리',
      children: [],
    },
  ];

  return (
    <header className="sticky top-0 z-50 border-b border-gray-200 bg-white">
      <div className="mx-auto flex h-[76px] max-w-[1200px] items-center justify-between px-4 sm:px-6 lg:px-10">
        <a href="/" className="shrink-0 text-xl font-extrabold tracking-tight text-emerald-700 sm:text-2xl">
          소이팜마켓
        </a>

        {/* 주요 화면 이동 메뉴 */}
        <nav className="hidden h-full items-center gap-10 md:flex">
          {navigationItems.map((item, index) => (
            <div key={item.name} className="group relative flex h-full items-center">
              <a
                href="#"
                className={`flex h-full items-center border-b-2 text-base font-semibold transition ${
                  index === 0
                    ? 'border-emerald-700 text-gray-950'
                    : 'border-transparent text-gray-600 hover:text-emerald-700'
                }`}
              >
                {item.name}
              </a>

              {item.children.length > 0 && (
                <div className="invisible absolute top-full left-1/2 z-50 w-44 -translate-x-1/2 border border-gray-200 bg-white py-2 opacity-0 shadow-lg transition group-hover:visible group-hover:opacity-100">
                  {item.children.map((child) => (
                    <a
                      key={child}
                      href="#"
                      className="block px-5 py-2.5 text-sm text-gray-600 hover:bg-gray-50 hover:text-emerald-700"
                    >
                      {child}
                    </a>
                  ))}
                </div>
              )}
            </div>
          ))}
        </nav>

        {/* 사용자 메뉴 */}
        <div className="flex items-center gap-2 text-sm font-medium text-gray-600 sm:gap-4">
          <button className="hidden transition hover:text-emerald-700 sm:block">검색</button>
          <button className="transition hover:text-emerald-700">로그인</button>
          <button className="relative whitespace-nowrap rounded-lg bg-emerald-700 px-3 py-2 font-semibold text-white transition hover:bg-emerald-800 sm:px-4 sm:py-2.5">
            장바구니
            <span className="ml-1.5 rounded-full bg-white/20 px-1.5 py-0.5 text-xs text-white">
              0
            </span>
          </button>
        </div>
      </div>
    </header>
  );
}

export default Header;
