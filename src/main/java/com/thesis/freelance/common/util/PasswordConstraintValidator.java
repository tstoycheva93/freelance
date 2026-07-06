package com.thesis.freelance.common.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private int min;
    private int max;
    private boolean requireUppercase;
    private boolean requireLowercase;
    private boolean requireDigit;
    private boolean allowWhitespace;

    @Override
    public void initialize(ValidPassword constraint) {
        this.min = constraint.min();
        this.max = constraint.max();
        this.requireUppercase = constraint.requireUppercase();
        this.requireLowercase = constraint.requireLowercase();
        this.requireDigit = constraint.requireDigit();
        this.allowWhitespace = constraint.allowWhitespace();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext ctx) {
        if (password == null || password.isBlank()) return true;

        if (password.length() < min || password.length() > max) {
            return build(ctx, "Password must be between " + min + " and " + max + " characters");
        }

        if (!allowWhitespace && password.chars().anyMatch(Character::isWhitespace)) {
            return build(ctx, "Password must not contain spaces");
        }

        boolean allPrintable = password.chars().allMatch(ch -> ch >= 33 && ch <= 126);
        if (!allPrintable) {
            return build(ctx, "Password contains invalid characters");
        }

        if (requireLowercase && password.chars().noneMatch(Character::isLowerCase)) {
            return build(ctx, "Password must contain at least one lowercase letter");
        }

        if (requireUppercase && password.chars().noneMatch(Character::isUpperCase)) {
            return build(ctx, "Password must contain at least one uppercase letter");
        }

        if (requireDigit && password.chars().noneMatch(Character::isDigit)) {
            return build(ctx, "Password must contain at least one number");
        }

        return true;
    }

    private boolean build(ConstraintValidatorContext ctx, String message) {
        ctx.disableDefaultConstraintViolation();
        ctx.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}