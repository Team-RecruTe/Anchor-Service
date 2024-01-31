package com.anchor.global.exception.handler;

import com.anchor.global.exception.AnchorException;
import com.anchor.global.exception.response.ServiceErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AnchorException.class)
  public ResponseEntity<ServiceErrorResponse> handleServiceException(AnchorException ex) {
    ServiceErrorResponse response = new ServiceErrorResponse(ex);
    return ResponseEntity.status(response.getStatus())
        .body(response);
  }

}
