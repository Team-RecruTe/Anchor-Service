package com.anchor.domain.user.api.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserImageRequest {

  private String image;

  public UserImageRequest(String image) {
    this.image = image;
  }

}