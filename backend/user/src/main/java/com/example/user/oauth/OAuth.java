package com.example.user.oauth;

public enum OAuth {
    REDIRECT_URI_PARAM_COOKIE_NAME("redirect_uri"),
    OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME("oauth2_auth_request"),
    REFRESH_TOKEN_COOKIE_NAME("refresh_token"),

    REFRESH_TOKEN_REDIS_NAME("RT");

    private final String value;

    OAuth(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
