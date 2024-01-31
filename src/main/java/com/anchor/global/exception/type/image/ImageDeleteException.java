package com.anchor.global.exception.type.image;

import com.anchor.global.exception.ExternalApiException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class ImageDeleteException extends ExternalApiException {

  public ImageDeleteException(Throwable ex) {
    super(AnchorErrorCode.IMAGE_DELETE_ERROR, ex);
  }

}
