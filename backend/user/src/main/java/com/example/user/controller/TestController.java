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
        System.out.println("테스트1");
        return ResponseEntity.ok("안녕ㅋㅋ");
    }

    @GetMapping("/2")
    ResponseEntity<?> test2() {
        System.out.println("테스트2");
        return ResponseEntity.ok("메롱ㅎㅎ");
    }
}
