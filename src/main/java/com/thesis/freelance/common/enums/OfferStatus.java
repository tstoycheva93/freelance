package com.thesis.freelance.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OfferStatus {

    PENDING("pending"),
    SHORTLISTED("shortlisted"),
    ACCEPTED("accepted"),
    REJECTED("rejected"),
    DELIVERED("delivered"),
    UNSATISFACTORY("unsatisfactory"),
    COMPLETED("completed"),
    CANCELED("canceled");

    private final String value;

    OfferStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static OfferStatus fromValue(String value) {
        for (OfferStatus status : OfferStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown offer status: " + value);
    }
}
