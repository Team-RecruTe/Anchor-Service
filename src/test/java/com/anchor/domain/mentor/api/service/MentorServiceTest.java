package com.anchor.domain.mentor.api.service;

import static org.mockito.ArgumentMatchers.anyString;

import com.anchor.domain.mentor.api.controller.request.MentorRegisterInfo;
import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.UserRole;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.exception.type.mentor.DuplicateEmailException;
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

  @Mock
  private UserRepository userRepository;

  @Test
  @DisplayName("멘토 등록 성공")
  void registerSuccess() {
    User user = User.builder()
        .email("test@test.com")
        .image("image")
        .role(UserRole.USER)
        .nickname("nickname")
        .build();
    SessionUser sessionUser = new SessionUser(user);
    Mentor mentor = Mentor.builder()
        .bankName("NH농협")
        .accountNumber("020-3039-765")
        .companyEmail("00000@naver.com")
        .accountName("leejuyoon")
        .career(Career.JUNIOR)
        .build();

    Mockito.when(mentorRepository.save(mentor))
        .thenReturn(mentor);
    Mockito.when(mentorRepository.existsMentorByCompanyEmail(anyString()))
        .thenReturn(false);
    Mockito.when(userRepository.findByEmail(anyString()))
        .thenReturn(Optional.ofNullable(user));

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
    User user = User.builder()
        .email("test@test.com")
        .image("image")
        .role(UserRole.USER)
        .nickname("nickname")
        .build();
    SessionUser sessionUser = new SessionUser(user);
    Mentor mentor = Mentor.builder()
        .bankName("NH농협")
        .accountNumber("020-3039-765")
        .companyEmail("0000@naver.com")
        .accountName("leejuyoon")
        .career(Career.JUNIOR)
        .build();

    Mockito.when(mentorRepository.save(mentor))
        .thenReturn(mentor);
    Mockito.when(mentorRepository.existsMentorByCompanyEmail(anyString()))
        .thenReturn(true);
    Mockito.when(userRepository.findByEmail(anyString()))
        .thenReturn(Optional.ofNullable(user));

    MentorRegisterInfo mentorRegisterInfo = MentorRegisterInfo.builder()
        .bankName("NH농협")
        .accountNumber("020-3039-765")
        .companyEmail("0000@naver.com")
        .accountName("leejuyoon")
        .career(Career.JUNIOR)
        .build();

    Assertions.assertThatThrownBy(() ->
            mentorService.register(mentorRegisterInfo, sessionUser))
        .isInstanceOf(DuplicateEmailException.class);
  }
}