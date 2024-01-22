package com.anchor.global.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseType {
  SUCCESS, FAIL;

  public static ResponseType of(boolean isSuccess) {
    return (isSuccess) ? SUCCESS : FAIL;
  }
}
