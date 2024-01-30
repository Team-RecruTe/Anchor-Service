package com.anchor.global.exception.type.image;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;

public class ImageDeleteException extends ServiceException {

  public ImageDeleteException(Exception ex) {
    super(ServiceErrorCode.IMAGE_DELETE_ERROR, ex);
  }

}
