package com.thesis.freelance.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SkillType {

    TECHNICAL("technical"),
    LANGUAGE("language"),
    SOFT_SKILL("soft_skill"),
    TOOL("tool");

    private final String value;

    SkillType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SkillType fromValue(String value) {
        for (SkillType type : SkillType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown skill type: " + value);
    }
}
