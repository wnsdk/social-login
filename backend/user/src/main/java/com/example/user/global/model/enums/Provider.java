package com.example.user.global.model.enums;

public enum Provider {
    GOOGLE("GOOGLE");

    private final String value;

    Provider(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

