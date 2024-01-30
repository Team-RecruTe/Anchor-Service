package com.anchor.global.exception.response;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public class ServiceErrorResponse {

  @JsonIgnore
  private final HttpStatus status;
  private final ErrorCode error;
  private final ErrorDetail details;

  public ServiceErrorResponse(ServiceException ex) {
    ServiceErrorCode errorCode = ex.getErrorCode();
    this.status = errorCode.getStatus();
    this.error = new ErrorCode(
        errorCode.name(),
        ex.getMessage()
    );
    this.details = Objects.requireNonNullElse(ex.getDetails(), new SimpleErrorDetail(""));
  }

  @Getter
  @RequiredArgsConstructor
  static class ErrorCode {

    private final String code;
    private final String message;

  }

}
