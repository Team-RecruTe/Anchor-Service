package com.anchor.domain.user.api.service;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.anchor.domain.user.api.controller.request.UserNicknameRequest;
import com.anchor.domain.user.api.service.response.UserInfoResponse;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.UserRole;
import com.anchor.domain.user.domain.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  UserRepository userRepository;

  @InjectMocks
  UserService userService;

  private UserInfoResponse userInfoResponse(User user) {
    return new UserInfoResponse(user);
  }


  @DisplayName("회원 정보 조회 - 성공")
  @Test
  void getProfile() {
    //given
    String userEmail = "mark@smtown";
    User user = User.builder()
        .email(userEmail)
        .nickname("mark")
        .role(UserRole.USER)
        .build();
    userRepository.save(user);
    when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

    // when
    UserInfoResponse userInfoResponse = userService.getProfile(userEmail);

    // then
    assertNotNull(userInfoResponse);
    assertEquals("mark", userInfoResponse.getNickname());
    assertEquals(userEmail, userInfoResponse.getEmail());
    assertEquals(UserRole.USER, userInfoResponse.getRole());

  }

  @DisplayName("회원 정보 조회 - 실패 (유저를 찾을 수 없음)")
  @Test
  void getProfile_Fail_UserNotFound() {
    // given
    String userEmail = "nonexistent@smtown";
    when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

    // when, then
    assertThrows(RuntimeException.class, () -> userService.getProfile(userEmail));
  }


  @DisplayName("닉네임 수정 - 성공")
  @Test
  void editNickname_Success() {
    // given
    String userEmail = "mark@smtown";
    User user = User.builder()
        .email(userEmail)
        .nickname("mark")
        .role(UserRole.USER)
        .build();
    when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
    UserNicknameRequest userNicknameRequest = new UserNicknameRequest("newMark");

    // when
    assertDoesNotThrow(() -> userService.editNickname(userEmail, userNicknameRequest));

    // then
    assertEquals("newMark", user.getNickname());
  }

  @DisplayName("닉네임 수정 - 실패 (유저를 찾을 수 없음)")
  @Test
  void editNickname_Fail_UserNotFound() {
    // given
    String userEmail = "nonexistent@smtown";
    UserNicknameRequest userNicknameRequest = new UserNicknameRequest("newMark");
    when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

    // when, then
    assertThrows(RuntimeException.class, () -> userService.editNickname(userEmail, userNicknameRequest));
  }


  @DisplayName("유저 삭제 - 성공")
  @Test
  void deleteUser_Success() {
    // given
    String userEmail = "mark@smtown";
    User user = User.builder()
        .email(userEmail)
        .nickname("mark")
        .role(UserRole.USER)
        .build();
    when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));

    // when
    assertDoesNotThrow(() -> userService.deleteUser(userEmail));

    // then
    verify(userRepository, times(1)).delete(user);
  }

  @DisplayName("유저 삭제 - 실패 (유저를 찾을 수 없음)")
  @Test
  void deleteUser_Fail_UserNotFound() {
    // given
    String userEmail = "nonexistent@smtown";
    when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

    // when, then
    assertThrows(RuntimeException.class, () -> userService.deleteUser(userEmail));
    verify(userRepository, never()).delete(any());
  }

}