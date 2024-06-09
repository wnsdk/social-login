package com.example.user.user.controller;

import com.example.user.global.exception.BaseException;
import com.example.user.global.exception.ErrorMessage;
import com.example.user.global.model.entity.User;
import com.example.user.global.util.CookieUtil;
import com.example.user.jwt.JwtProvider;
import com.example.user.jwt.TokenInfo;
import com.example.user.oauth.OAuth;
import com.example.user.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

import static com.example.user.global.exception.ErrorMessage.NOT_EXIST_USER;
import static com.example.user.oauth.OAuth.REFRESH_TOKEN_COOKIE_NAME;
import static com.example.user.oauth.OAuth.REFRESH_TOKEN_REDIS_NAME;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class TestLoginController {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-token-expire-time}")
    private int REFRESH_TOKEN_EXPIRE_TIME;


    // 테스트를 위해서만 쓰이는 로그인 api, 실제 서비스에서 쓰면 안 됨!
    @GetMapping("/user")
    ResponseEntity<?> loginUser(HttpServletResponse response) {
        User user = userRepository.findById(2L).orElseThrow(() -> {
            throw new BaseException(NOT_EXIST_USER);
        });
        TokenInfo tokenInfo = jwtProvider.generateToken(user.getId().toString(), user.getEmail(), user.getName(), user.getProfile(), user.getRole().getValue());

        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME.getValue(), tokenInfo.getRefreshToken(), REFRESH_TOKEN_EXPIRE_TIME);
        redisTemplate.opsForValue()
                .set(REFRESH_TOKEN_REDIS_NAME.getValue() + user.getId(), tokenInfo.getRefreshToken(),
                        REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        return ResponseEntity.ok(tokenInfo.getAccessToken());
    }

    @GetMapping("/admin")
    ResponseEntity<?> loginAdmin(HttpServletResponse response) {
        User user = userRepository.findById(1L).orElseThrow(() -> {
            throw new BaseException(NOT_EXIST_USER);
        });
        TokenInfo tokenInfo = jwtProvider.generateToken(user.getId().toString(), user.getEmail(), user.getName(), user.getProfile(), user.getRole().getValue());
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME.getValue(), tokenInfo.getRefreshToken(), REFRESH_TOKEN_EXPIRE_TIME);
        redisTemplate.opsForValue()
                .set(REFRESH_TOKEN_REDIS_NAME.getValue() + user.getId(), tokenInfo.getRefreshToken(),
                        REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        return ResponseEntity.ok(tokenInfo.getAccessToken());
    }

}
