package com.example.user.user.controller;

import com.example.user.global.exception.BaseException;
import com.example.user.global.exception.ErrorMessage;
import com.example.user.global.util.CookieUtil;
import com.example.user.jwt.JwtProvider;
import com.example.user.oauth.OAuth;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.user.global.exception.ErrorMessage.NOT_EXIST_REFRESH_TOKEN;
import static com.example.user.oauth.OAuth.REFRESH_TOKEN_COOKIE_NAME;

@RestController
@RequestMapping("/rt")
@RequiredArgsConstructor
public class TestRTController {

    private final JwtProvider jwtProvider;

    // 리프레시 토큰의 만료시간을 반환
    @GetMapping("/exp")
    ResponseEntity<Long> getExpOfRefreshToken(HttpServletRequest request) {
        Cookie cookie = CookieUtil.getCookie(request, REFRESH_TOKEN_COOKIE_NAME.getValue()).orElseThrow(() ->
                new BaseException(NOT_EXIST_REFRESH_TOKEN));
        String refreshToken = cookie.getValue();
        Long expiration = jwtProvider.getExpiration(refreshToken);

        return ResponseEntity.ok(expiration);
    }
}
