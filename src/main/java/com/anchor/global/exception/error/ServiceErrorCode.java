package com.anchor.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ServiceErrorCode {

  CUSTOM_ERROR_CODE(HttpStatus.BAD_REQUEST, "커스텀 에러 코드");

  private final HttpStatus status;
  private final String message;

}
