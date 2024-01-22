package com.anchor.domain.mentoring.api.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringApplicationUserInfo {

  private String nickname;
  private String email;
  private String tel;

}
