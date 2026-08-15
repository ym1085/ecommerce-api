/**
 * 홈 화면 최상단 대표 배너(Hero)
 * 농부소이 YouTube 썸네일을 정리한 이미지 위에 브랜드 문구를 배치하고
 * 왼쪽 그라데이션으로 문구와 농장 작업 장면을 함께 보여준다
 */
function Hero() {
  return (
    <section className="relative overflow-hidden text-white">
      <img
        src="/images/farmersoy-hero.jpg"
        alt="주말농장에서 수박 지지대를 설치하는 농부소이"
        className="absolute inset-0 h-full w-full object-cover object-center"
      />
      <div className="absolute inset-0 bg-gradient-to-r from-black/75 via-black/50 to-black/15" />

      <div className="relative mx-auto flex min-h-[460px] max-w-[1200px] items-center px-6 py-24 lg:px-10">
        <div className="max-w-2xl">
          <p className="mb-3 text-sm font-bold tracking-widest text-emerald-300">FARMER SOY</p>
          <h1 className="text-4xl font-extrabold leading-tight tracking-tight md:text-[42px]">
            서툴지만, 직접 키웁니다
          </h1>
          <p className="mt-5 text-base leading-7 text-gray-200 md:text-lg">
            신혼부부의 주말농장에서 시작된
            <br />
            농사 기록과 제철 먹거리를 소개합니다
          </p>
        </div>
      </div>
    </section>
  );
}

export default Hero;
