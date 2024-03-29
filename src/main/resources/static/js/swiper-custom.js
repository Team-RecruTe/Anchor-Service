new Swiper('.swiper', {
  // autoplay: {
  //   delay: 5000
  // },
  loop: true,
  slidesPerView: 4,
  slidesPerGroup: 1,
  spaceBetween: 8,
  centeredSlides: true,

  pagination: {
    el: '.swiper-pagination',
    clickable: true,
  },
  navigation: {
    prevEl: '.swiper-button-prev',
    nextEl: '.swiper-button-next'
  },

  on: {
    click: function (e) {
      let slide = e.target.closest('.swiper-slide');
      if (slide.classList.contains('swiper-slide-prev')) {
        this.slidePrev();
        return;
      }
      if (slide.classList.contains('swiper-slide-next')) {
        this.slideNext();
        return;
      }
      if (slide.classList.contains('swiper-slide-active')) {
        let mentorIcon = e.target.closest('#mentor-icon');
        if (mentorIcon) {
          mentorIcon.querySelector('a').click();
        }
        location.href = `/mentorings/${slide.id}`
      }
    }
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