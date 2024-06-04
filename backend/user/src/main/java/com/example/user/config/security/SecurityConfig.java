package com.example.user.config.security;

import com.example.user.config.jwt.JwtAuthenticationFilter;
import com.example.user.config.jwt.JwtTokenProvider;
import com.example.user.config.oauth.OAuth2SuccessHandler;
import com.example.user.domain.enums.Role;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> oAuth2AuthorizationRequestBasedOnCookieRepository;
//    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/test/1").permitAll()
                        .anyRequest().authenticated() // JwtFilter 에서 authenticate 해줌
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
                                                //Client 가 이 URI 로 요청을 보내면 OAuth 공급자에게 리다이렉션 됨
                                                .baseUri("/oauth2/authorization")
                                                //request 가 자동으로 cookie 에 저장됨
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

        //UsernamePasswordFilter에서 클라이언트가 요청한 리소스의 접근 권한이 없을 때 막는 역할을 하기 때문에 이 필터 전에 jwtAuthenticationFilter실행
//        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
