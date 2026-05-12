package com.thesis.freelance.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FlagType {

    SPAM("spam"),
    INAPPROPRIATE("inappropriate"),
    OTHER("other"),
    ILLEGAL_ACTIVITY("illegal_activity"),
    ABUSE("abuse"),
    FRAUD("fraud");

    private final String value;

    FlagType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static FlagType fromValue(String value) {
        if (value == null) return null;
        String normalized = value.toLowerCase().replace(' ', '_').replace('-', '_');
        for (FlagType type : FlagType.values()) {
            if (type.value.equals(normalized)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown flag type: " + value);
    }
}
