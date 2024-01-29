package com.anchor.global.exception.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class ErrorDetail {

  private final String message;

}
