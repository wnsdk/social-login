package com.example.user.jwt;

import com.example.user.global.exception.BaseException;
import com.example.user.global.exception.ErrorMessage;
import com.example.user.global.model.entity.User;
import com.example.user.global.model.enums.Status;
import com.example.user.global.util.CookieUtil;
import com.example.user.oauth.OAuth;
import com.example.user.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.example.user.global.exception.ErrorMessage.INVALID_REFRESH_TOKEN;
import static com.example.user.global.model.enums.Status.DELETED;
import static com.example.user.oauth.OAuth.REFRESH_TOKEN_COOKIE_NAME;
import static com.example.user.oauth.OAuth.REFRESH_TOKEN_REDIS_NAME;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    private static final List<String> EXCLUDE_URL =
            List.of("/login/oauth2/code/google");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return EXCLUDE_URL.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
    }

    // 인증이 필요한 요청마다, 유효한 AccessToken 을 갖고 있는지 검증하는 필터
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken = jwtProvider.getToken(request);

            // AccessToken 유효하다면
            if (jwtProvider.validateToken(accessToken)) {
                authenticate(accessToken);
            }
            // AccessToken 유효하지 않다면 (RefreshToken 확인)
            else {
                // Cookie 에서 RefreshToken 추출
                Cookie cookie = CookieUtil.getCookie(request, REFRESH_TOKEN_COOKIE_NAME.getValue()).orElseThrow(() ->
                        new BaseException(INVALID_REFRESH_TOKEN));
                String refreshTokenFromClient = cookie.getValue();
                log.info("쿠키의 Refresh Token : {}", refreshTokenFromClient);

                // Redis 에서 RefreshToken 추출
                String id = jwtProvider.getSub(refreshTokenFromClient);
                String refreshTokenFromServer = redisTemplate.opsForValue().get(REFRESH_TOKEN_REDIS_NAME.getValue() + id);
                log.info("레디스의 Refresh Token : {}", refreshTokenFromServer);

                // RefreshToken 유효하다면 (AccessToken 재발급)
                if (refreshTokenFromClient.equals(refreshTokenFromServer)) {
                    User user = getUser(id);

                    TokenInfo tokenInfo = jwtProvider.generateToken(String.valueOf(user.getId()),
                            user.getEmail(), user.getName(), user.getProfile(), user.getRole().getValue());
                    response.addHeader("Access-Token", tokenInfo.getAccessToken());

                    authenticate(tokenInfo.getAccessToken());
                }

                // AccessToken RefreshToken 둘 다 유효하지 않다면
                else {
                    SecurityContext context = SecurityContextHolder.getContext();
                    context.setAuthentication(null);    // context 에 처음부터 authentication 값이 제대로 들어가있는 경우가 있음
                    throw new BaseException(INVALID_REFRESH_TOKEN);
                }
            }
        } catch (Exception e) {
            request.setAttribute("exception", e);

        }
        filterChain.doFilter(request, response);
    }

    // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
    // 이제 이 스레드는 authenticated 되었다.
    private void authenticate(String accessToken) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = jwtProvider.getAuthentication(accessToken);
        context.setAuthentication(authentication);
    }

    private User getUser(String id) {
        User user = userRepository.findById(Long.valueOf(id)).orElseThrow(() -> {
            throw new BaseException(ErrorMessage.NOT_EXIST_USER);
        });

        if (user.getStatus() == DELETED) {
            throw new BaseException(ErrorMessage.NOT_EXIST_USER);
        }

        return user;
    }
}
