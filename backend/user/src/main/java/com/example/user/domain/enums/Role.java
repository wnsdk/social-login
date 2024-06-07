package com.example.user.domain.enums;

public enum Role {
    USER("USER"),     // 일반 유저
    ADMIN("ADMIN");   // 관리자 유저

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
