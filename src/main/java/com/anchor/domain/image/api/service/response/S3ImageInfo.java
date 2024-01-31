package com.anchor.domain.image.api.service.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class S3ImageInfo {

  Long imageId;
  String imageUrl;

  private S3ImageInfo(Long imageId, String imageUrl) {
    this.imageId = imageId;
    this.imageUrl = imageUrl;
  }

  public static S3ImageInfo of(Long imageId, String imageUrl) {
    return new S3ImageInfo(imageId, imageUrl);
  }

}
