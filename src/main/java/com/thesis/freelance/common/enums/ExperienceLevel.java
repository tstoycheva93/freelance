package com.thesis.freelance.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum ExperienceLevel {

    //TODO decide - map e sus slojnost O1 a ostanalite s On
    ENTRY("entry"),
    MID("mid"),
    SENIOR("senior"),
    EXPERT("expert");

    private static final Map<String, ExperienceLevel> MAP = new HashMap<>();
    static {
        for (ExperienceLevel l : values()) MAP.put(l.value, l);
    }

    private final String value;

    ExperienceLevel(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static ExperienceLevel fromValue(String value) {
        ExperienceLevel result = MAP.get(value.toLowerCase());
        if (result == null) throw new IllegalArgumentException("Unknown experience level: " + value);
        return result;
    }
}