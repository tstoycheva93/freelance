package com.thesis.freelance.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FlagStatus {

    PENDING ("pending"),
    APPROVED ("approved"),
    DECLINED ("declined");


    private final String value;

    FlagStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static FlagStatus fromValue(String value) {
        for (FlagStatus status : FlagStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown flag status: " + value);
    }
}
