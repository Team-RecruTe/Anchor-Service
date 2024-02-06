package com.anchor.global.exception.response;

import com.anchor.global.exception.error.AnchorErrorCode;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Getter
public class ValidationErrorResponse extends ErrorResponse {


  private final List<CustomFieldError> errors;

  private ValidationErrorResponse(AnchorErrorCode errorCode, List<CustomFieldError> errors) {
    super(errorCode);
    this.errors = errors;
  }

  public static ValidationErrorResponse of(AnchorErrorCode errorCode, BindingResult bindingResult) {
    return new ValidationErrorResponse(errorCode, CustomFieldError.of(bindingResult));
  }

  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  static class CustomFieldError {

    private String field;
    private String message;
    private String value;

    private CustomFieldError(FieldError fieldError) {
      this.field = fieldError.getField();
      this.message = Objects.requireNonNullElse(fieldError.getDefaultMessage(), "");
      this.value = Objects.requireNonNullElse(fieldError.getRejectedValue(), "")
          .toString();
    }

    private static List<CustomFieldError> of(BindingResult bindingResult) {
      final List<FieldError> fieldErrors = bindingResult.getFieldErrors();
      return fieldErrors.stream()
          .map(CustomFieldError::new)
          .toList();
    }

  }

}
