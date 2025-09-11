package com.spingboot_study.spingboot_service.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = {DobValidator.class}
)
public @interface DobConstraint {
    String message() default "Invalid date of birth format. Expected format is YYYY-MM-DD";

    int min() default 0;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
