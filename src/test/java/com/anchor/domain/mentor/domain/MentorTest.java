package com.anchor.domain.mentor.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MentorTest {

  @Test
  @DisplayName("멘토 빌더 테스트")
  void builder() {
    Mentor mentor = Mentor.builder()
        .companyEmail("09090@naver.com")
        .accountNumber("01092-778-3406")
        .career(Career.JUNIOR)
        .accountName("이주윤")
        .bankName("NH농협")
        .build();

    Assertions.assertThat(mentor.getAccountName()).isEqualTo("이주윤");
    Assertions.assertThat(mentor.getBankName()).isEqualTo("NH농협");
    Assertions.assertThat(mentor.getAccountNumber()).isEqualTo("01092-778-3406");
    Assertions.assertThat(mentor.getCareer()).isEqualTo(Career.JUNIOR);
    Assertions.assertThat(mentor.getCompanyEmail()).isEqualTo("09090@naver.com");
  }

}