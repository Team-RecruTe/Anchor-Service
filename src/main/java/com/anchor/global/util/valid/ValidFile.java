package com.anchor.global.util.valid;

import com.anchor.global.util.valid.CustomValidatorRegistry.FileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = {ElementType.FIELD, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileValidator.class)
public @interface ValidFile {

  String message() default "Invalid Range";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
