package com.anchor.global.util;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BaseResponse extends ResponseDto {

  private ResponseType result;

  public BaseResponse(ResponseType result) {
    this.result = result;
  }

}
