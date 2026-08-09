/**
 * 모든 화면 상단에 고정되는 헤더
 * 로고와 전역 내비게이션을 담으며 스크롤해도 상단에 붙어 있다
 */
function Header() {
  const navigationItems = ['농산물', '농장일기', '농부소이 소개'];

  return (
    <header className="sticky top-0 z-50 border-b border-gray-200 bg-white">
      <div className="mx-auto flex h-[76px] max-w-[1200px] items-center justify-between px-6 lg:px-10">
        <a href="/" className="text-2xl font-extrabold tracking-tight text-emerald-700">
          농부소이
        </a>

        {/* 주요 화면 이동 메뉴 */}
        <nav className="hidden h-full items-center gap-10 md:flex">
          {navigationItems.map((name, index) => (
            <a
              key={name}
              href="#"
              className={`flex h-full items-center border-b-2 text-base font-semibold transition ${
                index === 0
                  ? 'border-emerald-700 text-gray-950'
                  : 'border-transparent text-gray-600 hover:text-emerald-700'
              }`}
            >
              {name}
            </a>
          ))}
        </nav>

        {/* 사용자 메뉴 */}
        <div className="flex items-center gap-4 text-sm font-medium text-gray-600">
          <button className="hidden transition hover:text-emerald-700 sm:block">검색</button>
          <button className="transition hover:text-emerald-700">로그인</button>
          <button className="relative rounded-lg bg-emerald-700 px-4 py-2.5 font-semibold text-white transition hover:bg-emerald-800">
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
