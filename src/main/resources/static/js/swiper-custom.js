new Swiper('.swiper', {
  autoplay: {
    delay: 5000
  },
  loop: true,
  slidesPerView: 3,
  slidesPerGroup: 1,
  spaceBetween: 30,
  centeredSlides: true,

  pagination: {
    el: '.swiper-pagination',
    clickable: true
  },
  navigation: {
    prevEl: '.swiper-button-prev',
    nextEl: '.swiper-button-next'
  },


  // 반응형 웹 디자인을 위한 breakpoints 설정
  breakpoints: {
    // 화면 너비가 768px 미만일 때
    768: {
      slidesPerView: 1,
      slidesPerGroup: 1
    },
    // 화면 너비가 768px 이상 1024px 미만일 때
    1024: {
      slidesPerView: 2,
      slidesPerGroup: 2
    }
  }
})