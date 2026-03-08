package com.thesis.freelance.common.enums;

public enum ActivityStatus {

    ACTIVE("active"),

    INACTIVE("inactive"),

    DELETED("deleted");

    private final String value;

    ActivityStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ActivityStatus fromValue(String value) {
        for (ActivityStatus status : ActivityStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown record status: " + value);
    }
}