package com.example.user.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorMessage {
    EXIST_USER(201, "이미 가입된 유저입니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_USER(202, "존재하지 않는 유저입니다.", HttpStatus.BAD_REQUEST),
    DELETED_USER(203, "탈퇴한 유저입니다.", HttpStatus.BAD_REQUEST);

    private final Integer code;
    private final String errMsg;
    private final HttpStatus httpStatus;

    ErrorMessage(int code, String errMsg, HttpStatus httpStatus) {
        this.code = code;
        this.errMsg = errMsg;
        this.httpStatus = httpStatus;
    }

    public int getErrorCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorMessage() {
        return errMsg;
    }
}