package com.thesis.freelance.common.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.LinkedHashMap;
import java.util.Map;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    // Map of country code -> total expected digits (including country code, excluding +)
    private static final Map<String, Integer> COUNTRY_DIGIT_COUNT = new LinkedHashMap<>();

    static {
        COUNTRY_DIGIT_COUNT.put("359", 11);  // Bulgaria: +359 + 8 digits = 11 digits
        COUNTRY_DIGIT_COUNT.put("30", 11);   // Greece: +30 + 10 digits = 11 digits
        COUNTRY_DIGIT_COUNT.put("382", 11);  // Montenegro: +382 + 8 digits = 11 digits
        COUNTRY_DIGIT_COUNT.put("40", 11);   // Romania: +40 + 9 digits = 11 digits
        COUNTRY_DIGIT_COUNT.put("389", 11);  // North Macedonia: +389 + 8 digits = 11 digits
        COUNTRY_DIGIT_COUNT.put("385", 11);   // Croatia: +385 + 9 digits = 11 digits
        COUNTRY_DIGIT_COUNT.put("386", 11);  // Slovenia: +386 + 8 digits = 11 digits
        COUNTRY_DIGIT_COUNT.put("387", 11);  // Bosnia: +387 + 8 digits = 11 digits
        COUNTRY_DIGIT_COUNT.put("355", 11);  // Albania: +355 + 9 digits = 11 digits
        COUNTRY_DIGIT_COUNT.put("381", 11);  // Serbia: +381 + 9 digits = 11 digits
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null || phone.isBlank()) {
            return false;
        }

        // Remove any spaces or dashes
        String cleaned = phone.replaceAll("[\\s-]", "");

        // Must start with +
        if (!cleaned.startsWith("+")) {
            return false;
        }

        // Extract digits only (after +)
        String digits = cleaned.substring(1);

        // Try each supported country code
        for (Map.Entry<String, Integer> entry : COUNTRY_DIGIT_COUNT.entrySet()) {
            String countryCode = entry.getKey();
            int expectedLength = entry.getValue();

            if (digits.startsWith(countryCode)) {
                // Check if the remaining digits match the expected count
                String numberPart = digits.substring(countryCode.length());
                return numberPart.length() >= 8 && numberPart.length() <= 10;
            }
        }

        // Unknown country code
        return false;
    }
}
