package com.thesis.freelance.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FlagTarget {

    FLAG_PROFILE("flag_profile"),
    FLAG_OFFER("flag_offer"),
    FLAG_POSTING("flag_posting"),
    FLAG_REVIEW("flag_review");

    private final String value;

    FlagTarget(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static FlagTarget fromValue(String value) {
        for (FlagTarget target : FlagTarget.values()) {
            if (target.value.equalsIgnoreCase(value)) {
                return target;
            }
        }
        throw new IllegalArgumentException("Unknown flag target: " + value);
    }
}
