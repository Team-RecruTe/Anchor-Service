package com.anchor.domain.mentoring.api.controller.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringApplicationUserInfo {

  private String nickname;
  private String email;
  private String tel;

  @Builder
  private MentoringApplicationUserInfo(String nickname, String email, String tel) {
    this.nickname = nickname;
    this.email = email;
    this.tel = tel;
  }
}
