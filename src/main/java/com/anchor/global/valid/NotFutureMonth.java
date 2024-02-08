package com.anchor.global.valid;

import com.anchor.global.valid.CustomValidatorRegistry.MonthValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.FIELD, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MonthValidator.class)
public @interface NotFutureMonth {

  String message() default "Invalid Future";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
