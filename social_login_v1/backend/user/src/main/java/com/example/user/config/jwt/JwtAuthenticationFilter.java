package com.example.user.config.jwt;

import com.example.user.config.oauth.CookieUtil;
import com.example.user.exception.BaseException;
import com.example.user.exception.ErrorMessage;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
//@Order(Integer.MIN_VALUE)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    // 인증에서 제외할 url
    private static final List<String> EXCLUDE_URL =
            List.of(
//                    "/oauth2/authorization/google",
                    "/login/oauth2/code/google",
                    "/login"
            );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return EXCLUDE_URL.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Request Header 에서 Access Token 추출
            String accessToken = jwtTokenProvider.getToken(request);
            System.out.println(accessToken);
            System.out.println("!!");
            // AccessToken 유효성 검사
            if (jwtTokenProvider.validateToken(accessToken)) {
                // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
                // 이제 이 스레드는 authenticated 되었다.
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(authentication);
            }
            // AccessToken 이 유효하지 않다면 -> RefreshToken 확인
            else {
                // Cookie 에서 Refresh Token 추출
                Optional<Cookie> cookie = CookieUtil.getCookie(request, "RT");
                if (cookie.isEmpty()) {
                    throw new BaseException(ErrorMessage.REFRESH_TOKEN_NOT_MATCH);
                }
                String refreshTokenFromClient = cookie.get().getValue();

                // Refresh Token 에서 유저 정보 추출
                Authentication authentication = jwtTokenProvider.getAuthentication(refreshTokenFromClient);

                // Redis 에서 Refresh Token 추출
                String refreshTokenFromServer = redisTemplate.opsForValue().get("RT:" + authentication.getName());

                if (!refreshTokenFromClient.equals(refreshTokenFromServer)) {
                    throw new BaseException(ErrorMessage.REFRESH_TOKEN_NOT_MATCH);
                }
            }

        } catch (Exception e) {
//            request.setAttribute("exception", e);
        } finally {
            filterChain.doFilter(request, response);
        }


    }
}
