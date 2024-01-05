package com.anchor.global.util.valid;

import com.anchor.global.util.valid.CustomValidatorRegistry.RangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RangeValidator.class)
public @interface ValidRange {

  String message() default "Invalid Range";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}