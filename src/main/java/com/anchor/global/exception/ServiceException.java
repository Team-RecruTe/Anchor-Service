package com.anchor.global.exception;

import com.anchor.global.exception.error.ServiceErrorCode;
import com.anchor.global.exception.response.ErrorDetail;
import lombok.Getter;

@Getter
public abstract class ServiceException extends RuntimeException {

  private ServiceErrorCode errorCode;
  private ErrorDetail details;

}
