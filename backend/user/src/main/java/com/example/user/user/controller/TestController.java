package com.example.user.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/all")
    ResponseEntity<?> all() {
        return ResponseEntity.ok("안녕하세요.");
    }

    @GetMapping("/user")
    ResponseEntity<?> user() {
        return ResponseEntity.ok("회원님 안녕하세요.");
    }

    @GetMapping("/admin")
    ResponseEntity<?> admin() { return ResponseEntity.ok("관리자님 안녕하세요."); }

}
