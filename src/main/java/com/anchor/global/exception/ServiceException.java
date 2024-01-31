package com.anchor.global.exception;

import com.anchor.global.exception.error.ServiceErrorCode;
import com.anchor.global.exception.response.ErrorDetail;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public abstract class ServiceException extends RuntimeException {

  private ServiceErrorCode errorCode;
  private ErrorDetail details;

  protected ServiceException(ServiceErrorCode errorCode) {
    super(errorCode.getMessage());
    printErrorMessage();
  }

  protected ServiceException(ServiceErrorCode errorCode, Exception ex) {
    super(errorCode.getMessage());
    printErrorMessage();
    printStackTrace(ex);
  }

  protected ServiceException(ServiceErrorCode errorCode, ErrorDetail details) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.details = details;
    printErrorMessage();
  }

  protected ServiceException(ServiceErrorCode errorCode, ErrorDetail details, Exception ex) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.details = details;
    printErrorMessage();
    printStackTrace(ex);
  }

  private void printErrorMessage() {
    log.error(errorCode.getMessage());
  }

  private void printStackTrace(Exception ex) {
    log.error("Exception Stack Trace :: ", ex);
  }
}
