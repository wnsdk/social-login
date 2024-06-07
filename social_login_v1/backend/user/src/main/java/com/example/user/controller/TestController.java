package com.example.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    
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
    ResponseEntity<?> loginUser() {
        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZW1haWwiOiJhZG1pbkBleGFtcGxlLmNvbSIsIm5hbWUiOiLrsJXqtIDrpqwiLCJwcm9maWxlIjoiaHR0cHM6Ly9iaXQubHkvNGU5Ym5BMyIsImF1dGgiOiJBRE1JTiJ9.6irSq_zAUBLyHCA592k4ou15SrcmZ3QvYUOlMrZpyzk";
        return ResponseEntity.ok(accessToken); }

    @GetMapping("/login/admin")
    ResponseEntity<?> loginAdmin() {
        String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwiZW1haWwiOiJ1c2VyQGV4YW1wbGUuY29tIiwibmFtZSI6Iuq5gOycoOyggCIsInByb2ZpbGUiOiJodHRwczovL2JpdC5seS8zUmlqcUFLIiwiYXV0aCI6IlVTRVIifQ.YJDC-CIWA7c4AAhK2VuP9TGoKLdN3iH9og24pz44ygA";
        return ResponseEntity.ok(accessToken); }
}
