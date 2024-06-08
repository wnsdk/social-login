package com.example.user.global.model.enums;

public enum Status {
    ACTIVE("ACTIVE"),        // 활성 상태: 로그인 및 서비스 이용 가능 상태
    INACTIVE("INACTIVE"),    // 비활성 상태: 일시적으로 비활성화된 상태
    DELETED("DELETED");      // 삭제 상태: 회원 정보가 영구적으로 삭제된 상태

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
