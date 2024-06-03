package com.example.user.domain.enums;

public enum Role {
    ROLE_USER("ROLE_USER"),     // 일반 유저
    ROLE_ADMIN("ROLE_ADMIN");   // 관리자 유저

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
