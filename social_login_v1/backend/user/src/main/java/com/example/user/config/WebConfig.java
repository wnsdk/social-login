//package com.example.user.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:5173") // 클라이언트 출처 명시
//                .allowedMethods("GET", "POST", "PUT", "DELETE")
//                .allowCredentials(true)
//                .allowedHeaders("*")
//                .maxAge(3600);
//    }
//}
