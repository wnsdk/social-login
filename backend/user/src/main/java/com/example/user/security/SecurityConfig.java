package com.example.user.security;

import com.example.user.jwt.JwtAuthenticationFilter;
import com.example.user.oauth.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepositoryWithCookie;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/test/all", "/login/**").permitAll()
                        .requestMatchers("/test/admin").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                );

        http
                .oauth2Login(oauth ->
                        oauth
                                .authorizationEndpoint(authorizationEndpointConfig ->
                                        authorizationEndpointConfig
                                                //Client 가 이 URI 로 요청을 보내면 OAuth 공급자에게 리디렉션 됨
                                                .baseUri("/oauth2/authorization")
                                                //OAuth2 승인 요청이 자동으로 cookie 에 저장됨
                                                .authorizationRequestRepository(authorizationRequestRepositoryWithCookie))

                                //OAuth 공급자에게 인증을 받은 후 리다이렉트되는 엔드포인트 구성
                                .redirectionEndpoint(redirectionEndpointConfig ->
                                        redirectionEndpointConfig
                                                .baseUri("/*/oauth2/code/*"))

                                //인증 후 사용자 정보를 처리하는 서비스 설정
                                .userInfoEndpoint(userInfoEndpoint ->
                                        userInfoEndpoint
                                                .userService(oAuth2UserService))

                                // 로그인 성공 시 핸들러
                                .successHandler(oAuth2SuccessHandler)
                );

        //UsernamePasswordFilter 에서 클라이언트가 요청한 리소스의 접근 권한이 없을 때 막는 역할을 하기 때문에 이 필터 전에 jwtAuthenticationFilter실행
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 인증 실패할 경우
        http.exceptionHandling(handler -> handler.authenticationEntryPoint(authenticationEntryPoint));

        http
                //localhost:5173 에 대한 CORS 허용
                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();

                    config.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(List.of("Content-Type", "Authorization"));
                    config.addExposedHeader("Access-Token");
                    config.setMaxAge(3600L);

                    return config;
                }));

        return http.build();
    }
}
