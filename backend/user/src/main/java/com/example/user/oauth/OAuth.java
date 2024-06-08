package com.example.user.oauth;

public enum OAuth {
    REDIRECT_URI_PARAM_COOKIE_NAME("redirect_uri"),
    OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME("oauth2_auth_request");

    private final String value;

    OAuth(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
