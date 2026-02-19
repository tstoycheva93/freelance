package com.thesis.freelance.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationType {

    NEW_OFFER("new_offer"),
    NEW_MESSAGE("new_message"),
    OFFER_ACCEPTED("offer_accepted"),
    OFFER_REJECTED("offer_rejected"),
    PROJECT_COMPLETED("project_completed"),
    NEW_REVIEW("new_review");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static NotificationType fromValue(String value) {
        for (NotificationType type : NotificationType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown notification type: " + value);
    }
}
