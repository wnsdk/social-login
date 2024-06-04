package com.example.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    
    @GetMapping("/1")
    ResponseEntity<?> test1() {
        return ResponseEntity.ok("인증되지 않은 유저도 사용 가능합니다.");
    }

    @GetMapping("/2")
    ResponseEntity<?> test2() {
        return ResponseEntity.ok("인증된 유저만 사용 가능합니다.");
    }
}
