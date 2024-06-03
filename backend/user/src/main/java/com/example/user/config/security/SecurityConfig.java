package com.example.user.config.security;

import com.example.user.config.oauth.OAuth2SuccessHandler;
import com.example.user.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> oAuth2AuthorizationRequestBasedOnCookieRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().permitAll()
                );

        http
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                );

        http
                .oauth2Login(oauth ->
                        oauth
                                .authorizationEndpoint(authorizationEndpointConfig ->
                                        authorizationEndpointConfig
                                                //Client가 이 URI로 요청을 보내면 OAuth 공급자에게 리다이렉션 됨
                                                .baseUri("/oauth2/authorization")
                                                //request가 자동으로 cookie에 저장됨
                                                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository))

                                //OAuth 공급자가 인증 후 OAuth 클라이언트(Spring Server)에게 리다이렉션할 URI 패턴 설정
//                                .redirectionEndpoint(redirectionEndpointConfig ->
//                                        redirectionEndpointConfig
//                                                .baseUri("/*/oauth2/code/*"))

                                //인증 후 사용자 정보를 처리하는 서비스 설정
                                .userInfoEndpoint(userInfoEndpoint ->
                                        userInfoEndpoint
                                                .userService(oAuth2UserService))

                                // 로그인 성공 시 핸들러
                                .successHandler(oAuth2SuccessHandler)
                );

        return http.build();
    }
}
