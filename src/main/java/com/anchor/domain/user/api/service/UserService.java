package com.anchor.domain.user.api.service;

import com.anchor.domain.user.api.controller.request.UserNicknameRequest;
import com.anchor.domain.user.api.service.response.UserInfoResponse;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public UserInfoResponse getProfile(String email){
    User user = userRepository.findByEmail(email)
        .orElseThrow(()->{
          return new RuntimeException("해당 유저를 찾을 수 없습니다.");
        });
    return new UserInfoResponse(user);
  }

  @Transactional
  public void editNickname(String email, UserNicknameRequest userNicknameRequest){
    User user = userRepository.findByEmail(email)
        .orElseThrow(()->{
          return new RuntimeException("해당 유저를 찾을 수 없습니다.");
        });
    user.editNickname(userNicknameRequest);
  }

  @Transactional
  public void deleteUser(String email){
    User user = userRepository.findByEmail(email)
        .orElseThrow(()->{
          return new RuntimeException("해당 유저를 찾을 수 없습니다.");
        });
    userRepository.delete(user);
  }

}
