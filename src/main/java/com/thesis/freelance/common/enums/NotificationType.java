package com.thesis.freelance.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationType {

    NEW_OFFER("new_offer"),
    OFFER_ACCEPTED("offer_accepted"),
    OFFER_REJECTED("offer_rejected"),
    OFFER_CANCELLED("offer_cancelled"),
    OFFER_DELIVERED("offer_delivered"),
    OFFER_COMPLETED("offer_completed"),
    OFFER_UNSATISFACTORY("offer_unsatisfactory"),
    EXPIRED_CERTIFICATE("expired_certificate"),
    EXPIRED_POSTING("expired_posting"),
    EXPIRED_OFFER("expired_offer"),
    NEW_REVIEW("new_review"),

    //admin da prashta
    CUSTOM("custom"),
    REGISTRATION_CLOSED("registration_closed"),
    CONTENT_VIOLATION("content_violation"),

    //admin da poluchava
    NEW_FLAG("new_flag"),
    NEW_USER("new_user"),
    NEW_POSTING("new_posting");


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
