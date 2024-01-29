package com.anchor.domain.mentor.api.service;

import com.anchor.domain.mentor.api.controller.request.MentorRegisterInfo;
import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.user.domain.User;
import com.anchor.global.auth.SessionUser;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class MentorServiceTest {

  @InjectMocks
  private MentorService mentorService;

  @Mock
  private MentorRepository mentorRepository;


  @Test
  @DisplayName("멘토 등록 성공")
  void registerSuccess() {
    SessionUser sessionUser = new SessionUser(User.builder()
        .email("test@test.com")
        .build());
    Mentor mentor = Mentor.builder()
        .bankName("NH농협")
        .accountNumber("020-3039-765")
        .companyEmail("00000@naver.com")
        .accountName("leejuyoon")
        .career(Career.JUNIOR)
        .build();

    Mockito.when(mentorRepository.save(mentor))
        .thenReturn(mentor);

    MentorRegisterInfo mentorRegisterInfo = MentorRegisterInfo.builder()
        .bankName("NH농협")
        .accountNumber("020-3039-765")
        .companyEmail("00000@naver.com")
        .accountName("leejuyoon")
        .career(Career.JUNIOR)
        .build();
    Mentor registerResult = mentorService.register(mentorRegisterInfo, sessionUser);

    Assertions.assertThat(registerResult.getCompanyEmail())
        .isEqualTo(mentor.getCompanyEmail());
  }

  @Test
  @DisplayName("멘토 등록 실패")
  void registerFail() {
    SessionUser sessionUser = new SessionUser(User.builder()
        .email("test@test.com")
        .build());
    Mentor mentor = Mentor.builder()
        .bankName("NH농협")
        .accountNumber("020-3039-765")
        .companyEmail("0000@naver.com")
        .accountName("leejuyoon")
        .career(Career.JUNIOR)
        .build();

    Mockito.when(mentorRepository.findByCompanyEmail("0000@naver.com"))
        .thenReturn(Optional.of(mentor));

    MentorRegisterInfo mentorRegisterInfo = MentorRegisterInfo.builder()
        .bankName("NH농협")
        .accountNumber("020-3039-765")
        .companyEmail("0000@naver.com")
        .accountName("leejuyoon")
        .career(Career.JUNIOR)
        .build();

    Assertions.assertThatThrownBy(() ->
            mentorService.register(mentorRegisterInfo, sessionUser))
        .isInstanceOf(IllegalStateException.class);
  }
}