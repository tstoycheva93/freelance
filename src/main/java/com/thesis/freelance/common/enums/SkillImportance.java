package com.thesis.freelance.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SkillImportance {

    REQUIRED("required"),
    PREFERRED("preferred"),
    NICE_TO_HAVE("nice_to_have");

    private final String value;

    SkillImportance(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SkillImportance fromValue(String value) {
        for (SkillImportance importance : SkillImportance.values()) {
            if (importance.value.equalsIgnoreCase(value)) {
                return importance;
            }
        }
        throw new IllegalArgumentException("Unknown skill importance: " + value);
    }
}
