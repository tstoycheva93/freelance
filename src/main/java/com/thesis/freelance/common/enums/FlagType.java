package com.thesis.freelance.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FlagType {

    SPAM ("spam"),
    SCAM("scam"),
    INAPPROPRIATE("inappropriate"),
    FAKE("fake"),
    OTHER("other");

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
        for (FlagType type : FlagType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown flag type: " + value);
    }
}
