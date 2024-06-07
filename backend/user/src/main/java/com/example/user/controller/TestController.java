package com.example.user.controller;

import com.example.user.config.jwt.JwtProvider;
import com.example.user.config.jwt.TokenInfo;
import com.example.user.config.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${jwt.refresh-token-expire-time}")
    private int REFRESH_TOKEN_EXPIRE_TIME;

    @GetMapping("/guest")
    ResponseEntity<?> guest() {
        return ResponseEntity.ok("안녕하세요.");
    }

    @GetMapping("/user")
    ResponseEntity<?> user() {
        return ResponseEntity.ok("회원님 안녕하세요.");
    }

    @GetMapping("/admin")
    ResponseEntity<?> admin() { return ResponseEntity.ok("관리자님 안녕하세요."); }

    // 테스트를 위해서만 쓰이는 로그인 api, 실제 서비스에서 쓰면 안 됨!
    // TODO : jwt key 제대로 된 걸로 바꾸기, h2DB에 있는 1번, 2번 유저로 accessToken 만들어서 body 담아주기
    @GetMapping("/login/user")
    ResponseEntity<?> loginUser(HttpServletResponse response) {
        TokenInfo tokenInfo = jwtProvider.generateToken("1", "admin@example.com", "박관리", "https://bit.ly/4e9bnA3", "ADMIN");
        CookieUtil.addCookie(response, "RT", tokenInfo.getRefreshToken(), REFRESH_TOKEN_EXPIRE_TIME);
//        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJhZG1pbkBleGFtcGxlLmNvbSIsIm5hbWUiOiLrsJXqtIDrpqwiLCJwcm9maWxlIjoiaHR0cHM6Ly9iaXQubHkvNGU5Ym5BMyIsImF1dGgiOiJBRE1JTiJ9.6irSq_zAUBLyHCA592k4ou15SrcmZ3QvYUOlMrZpyzk";
        return ResponseEntity.ok(tokenInfo.getAccessToken()); }

    @GetMapping("/login/admin")
    ResponseEntity<?> loginAdmin(HttpServletResponse response) {
        TokenInfo tokenInfo = jwtProvider.generateToken("2", "user@example.com", "김유저", "https://bit.ly/3RijqAK", "USER");
        CookieUtil.addCookie(response, "RT", tokenInfo.getRefreshToken(), REFRESH_TOKEN_EXPIRE_TIME);
//        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwiZW1haWwiOiJ1c2VyQGV4YW1wbGUuY29tIiwibmFtZSI6Iuq5gOycoOyggCIsInByb2ZpbGUiOiJodHRwczovL2JpdC5seS8zUmlqcUFLIiwiYXV0aCI6IlVTRVIifQ.YJDC-CIWA7c4AAhK2VuP9TGoKLdN3iH9og24pz44ygA";
        return ResponseEntity.ok(tokenInfo.getAccessToken()); }

    // 리프레시 토큰의 만료시간을 반환
    @GetMapping("/exp/rt")
    ResponseEntity<Long> getExpOfRefreshToken(HttpServletRequest request) {
        String accessToken = jwtProvider.getToken(request);
        String sub = jwtProvider.getSub(accessToken);

        Cookie cookie = CookieUtil.getCookie(request, "RT").get();
        String refreshToken = cookie.getValue();
        Long expiration = jwtProvider.getExpiration(refreshToken);
//        Long expire = redisTemplate.getExpire("RT:" + sub);

        return ResponseEntity.ok(expiration);
    }
}
