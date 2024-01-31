package com.anchor.domain.user.api.controller.request;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserNicknameRequest {

  private String nickname;

  public UserNicknameRequest(String nickname) {
    this.nickname = nickname;
  }

}
