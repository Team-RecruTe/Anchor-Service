package com.anchor.global.exception.type.image;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;

public class ImageUploadException extends ServiceException {

  public ImageUploadException(Exception ex) {
    super(ServiceErrorCode.IMAGE_UPLOAD_ERROR, ex);
  }

}
