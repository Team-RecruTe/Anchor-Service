package com.anchor.global.exception.handler;

import com.anchor.global.exception.AnchorException;
import com.anchor.global.exception.error.AnchorErrorCode;
import com.anchor.global.exception.response.ErrorResponse;
import com.anchor.global.exception.response.ServiceErrorResponse;
import com.anchor.global.exception.response.ValidationErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler({MethodArgumentNotValidException.class, AnchorException.class})
  public ResponseEntity<ErrorResponse> handleServiceAndValidationException(Exception ex) {
    ErrorResponse response = createErrorResponse(ex);
    return ResponseEntity.status(response.getStatus())
        .body(response);
  }

  private ErrorResponse createErrorResponse(Exception ex) {
    if (ex instanceof MethodArgumentNotValidException validException) {
      return ValidationErrorResponse.of(AnchorErrorCode.INVALID_INPUT_VALUE,
          validException.getBindingResult());
    }
    return new ServiceErrorResponse((AnchorException) ex);
  }

}
