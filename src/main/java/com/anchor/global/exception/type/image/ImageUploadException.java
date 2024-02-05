package com.anchor.global.exception.type.image;

import com.anchor.global.exception.ExternalApiException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class ImageUploadException extends ExternalApiException {

  public ImageUploadException(Throwable ex) {
    super(AnchorErrorCode.IMAGE_UPLOAD_ERROR, ex);
  }

}
