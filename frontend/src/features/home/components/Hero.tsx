/**
 * 홈 화면 최상단 대표 배너(Hero)
 * 농장 작업 이미지 위에 소이팜마켓의 제철 농산물 메시지를 배치한다
 */
function Hero() {
  return (
    <section className="relative overflow-hidden text-white">
      <img
        src="/images/soy-farm-market-hero.webp"
        alt="제철 농산물이 놓인 소이팜마켓 농장"
        className="absolute inset-0 h-full w-full object-cover object-[42%_center] md:object-[center_28%]"
      />
      <div className="absolute inset-0 bg-gradient-to-r from-black/75 via-black/50 to-black/15" />

      <div className="relative mx-auto flex min-h-[360px] max-w-[1200px] items-center px-6 py-16 md:min-h-[460px] md:py-24 lg:px-10">
        <div className="max-w-2xl">
          <p className="mb-3 text-sm font-bold tracking-widest text-emerald-300">SOY FARM MARKET</p>
          <h1 className="text-4xl font-extrabold leading-tight tracking-tight md:text-[42px]">
            생산자의 제철을 가장 가까이
          </h1>
          <p className="mt-5 text-base leading-7 text-gray-200 md:text-lg">
            정성껏 키운 제철 농산물을
            <br />
            생산자의 이야기와 함께 소개합니다
          </p>
        </div>
      </div>
    </section>
  );
}

export default Hero;
