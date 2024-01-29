package com.anchor.global.exception.handler;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.response.ServiceErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ServiceException.class)
  public ResponseEntity<ServiceErrorResponse> handleServiceException(ServiceException ex) {
    ServiceErrorResponse response = new ServiceErrorResponse(ex);
    return ResponseEntity.status(response.getStatus())
        .body(response);
  }

}
