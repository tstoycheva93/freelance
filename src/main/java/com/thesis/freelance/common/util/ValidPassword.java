package com.thesis.freelance.common.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Password must be at least 8 characters and include uppercase, lowercase and a number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int min() default 8;
    int max() default 100;

    boolean requireUppercase() default true;
    boolean requireLowercase() default true;
    boolean requireDigit() default true;

    // symbols allowed but not required; no rule needed unless you want to restrict allowed chars
    boolean allowWhitespace() default false;
}