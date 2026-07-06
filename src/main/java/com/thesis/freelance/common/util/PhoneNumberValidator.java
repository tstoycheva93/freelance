package com.thesis.freelance.common.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.regex.Pattern;

public class PhoneNumberValidator
        implements ConstraintValidator<ValidPhoneNumber, String> {

    private static final PhoneNumberUtil PHONE_NUMBER_UTIL =
            PhoneNumberUtil.getInstance();

    private static final Set<String> ALLOWED_REGIONS = Set.of(
            "BG",
            "GR",
            "ME",
            "RO",
            "MK",
            "HR",
            "SI",
            "BA",
            "AL",
            "RS"
    );

    private static final Pattern VALID_INPUT_PATTERN =
            Pattern.compile("^\\+[0-9\\s()-]+$");

    private static final Pattern SEPARATOR_PATTERN =
            Pattern.compile("[\\s()-]");

    @Override
    public boolean isValid(
            String phone,
            ConstraintValidatorContext context
    ) {
        if (phone == null || phone.isBlank()) {
            return true;
        }

        String trimmedPhone = phone.trim();

        if (!VALID_INPUT_PATTERN.matcher(trimmedPhone).matches()) {
            return false;
        }

        String normalizedPhone = SEPARATOR_PATTERN
                .matcher(trimmedPhone)
                .replaceAll("");

        try {
            PhoneNumber parsedPhone = PHONE_NUMBER_UTIL.parse(
                    normalizedPhone,
                    null
            );

            if (!PHONE_NUMBER_UTIL.isValidNumber(parsedPhone)) {
                return false;
            }

            String regionCode =
                    PHONE_NUMBER_UTIL.getRegionCodeForNumber(parsedPhone);

            return regionCode != null
                    && ALLOWED_REGIONS.contains(regionCode);

        } catch (NumberParseException exception) {
            return false;
        }
    }
}