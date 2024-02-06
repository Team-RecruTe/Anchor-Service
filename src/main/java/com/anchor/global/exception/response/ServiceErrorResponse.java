package com.anchor.global.exception.response;

import com.anchor.global.exception.AnchorException;
import java.util.Objects;
import lombok.Getter;

@Getter
public class ServiceErrorResponse extends ErrorResponse {

  private final ErrorDetail details;

  public ServiceErrorResponse(AnchorException ex) {
    super(ex.getErrorCode());
    this.details = Objects.requireNonNullElse(ex.getDetails(), new SimpleErrorDetail(""));
  }

}
