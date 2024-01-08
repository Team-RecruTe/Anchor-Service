package com.anchor.domain.image.api.service.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class S3ImageUrl {

  String imageUrl;

  private S3ImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public static S3ImageUrl of(String imageUrl) {
    return new S3ImageUrl(imageUrl);
  }

}
