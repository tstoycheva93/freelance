package com.thesis.freelance.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Availability {

    AVAILABLE("available"),
    BUSY("busy"),
    NOT_AVAILABLE("not_available");

    private final String value;

    Availability(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Availability fromValue(String value) {
        for (Availability status : Availability.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown availability: " + value);
    }
}
