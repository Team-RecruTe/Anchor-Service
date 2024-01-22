package com.anchor.domain.mentor.api.service;

import static org.junit.jupiter.api.Assertions.*;

import com.anchor.domain.mentor.api.controller.request.MentorRegisterInfo;
import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import java.lang.reflect.Member;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class MentorServiceTest {

  private MentorService mentorService;

  @MockBean
  private MentorRepository mentorRepository;

  @BeforeEach
  void setUp() {
    mentorService = new MentorService(mentorRepository);
  }

  @Test
  @DisplayName("멘토 등록 성공")
  void registerSuccess() {
    Mentor mentor = Mentor.builder()
        .bankName("NH농협")
        .accountNumber("020-3039-765")
        .companyEmail("00000@naver.com")
        .accountName("leejuyoon")
        .career(Career.JUNIOR)
        .build();

    Mockito.when(mentorRepository.save(mentor)).thenReturn(mentor);

    MentorRegisterInfo mentorRegisterInfo = MentorRegisterInfo.builder()
        .bankName("NH농협")
        .accountNumber("020-3039-765")
        .companyEmail("00000@naver.com")
        .accountName("leejuyoon")
        .career(Career.JUNIOR)
        .build();
    Mentor registerResult = mentorService.register(mentorRegisterInfo);


    Assertions.assertThat(registerResult.getCompanyEmail()).isEqualTo(mentor.getCompanyEmail());
  }

  @Test
  @DisplayName("멘토 등록 실패")
  void registerFail() {
    Mentor mentor = Mentor.builder()
        .bankName("NH농협")
        .accountNumber("020-3039-765")
        .companyEmail("0000@naver.com")
        .accountName("leejuyoon")
        .career(Career.JUNIOR)
        .build();

    Mockito.when(mentorRepository.findByCompanyEmail("0000@naver.com")).thenReturn(Optional.of(mentor));

    MentorRegisterInfo mentorRegisterInfo = MentorRegisterInfo.builder()
        .bankName("NH농협")
        .accountNumber("020-3039-765")
        .companyEmail("0000@naver.com")
        .accountName("leejuyoon")
        .career(Career.JUNIOR)
        .build();

    Assertions.assertThatThrownBy(() ->
            mentorService.register(mentorRegisterInfo))
        .isInstanceOf(IllegalStateException.class);
  }
}