package com.thesis.freelance.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PostingStatus {

    ACTIVE("active"),
    CLOSED("closed");

    private final String value;

    PostingStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static PostingStatus fromValue(String value) {
        for (PostingStatus status : PostingStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown posting status: " + value);
    }
}
