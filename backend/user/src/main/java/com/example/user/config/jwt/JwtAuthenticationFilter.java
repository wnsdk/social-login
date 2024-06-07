package com.example.user.config.jwt;

import com.example.user.config.CookieUtil;
import com.example.user.domain.entity.User;
import com.example.user.exception.BaseException;
import com.example.user.exception.ErrorMessage;
import com.example.user.repository.UserRepository;
import jakarta.servlet.*;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    // 인증에서 제외할 url
    private static final List<String> EXCLUDE_URL =
            List.of(
//                    "/oauth2/authorization/google",
                    "/login/oauth2/code/google"
            );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return EXCLUDE_URL.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
    }

    /**
     * 인증이 필요한 요청마다, 유효한 AccessToken 을 갖고 있는지 검증하는 필터
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SecurityContext context = SecurityContextHolder.getContext();

        try {
            // Request Header 에서 Access Token 추출
            String accessToken = jwtProvider.getToken(request);

            // AccessToken 유효성 검사
            if (jwtProvider.validateToken(accessToken)) {
                // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
                // 이제 이 스레드는 authenticated 되었다.
                Authentication authentication = jwtProvider.getAuthentication(accessToken);
                context.setAuthentication(authentication);
            }

            // AccessToken 이 유효하지 않다면 -> RefreshToken 확인
            else {
                // Cookie 에서 RefreshToken 추출
                Cookie cookie = CookieUtil.getCookie(request, "RT").orElseThrow(() -> new BaseException(ErrorMessage.REFRESH_TOKEN_NOT_MATCH));
                String refreshTokenFromClient = cookie.getValue();

                // Cookie 의 RefreshToken 에 담긴 유저 정보로 Redis 에서 RefreshToken 탐색
                String userId = jwtProvider.getSub(refreshTokenFromClient);
                String refreshTokenFromServer = redisTemplate.opsForValue().get("RT:" + userId);

                // RefreshToken 이 유효하다면 -> accessToken 을 재발급
                if (refreshTokenFromClient.equals(refreshTokenFromServer)) {
                    Optional<User> optionalUser = userRepository.findById(Long.valueOf(userId));

                    // TODO : user 가 ACTIVE 상태인지 체크
                    if (optionalUser.isEmpty()) {
                        throw new BaseException(ErrorMessage.USER_NOT_FOUND);
                    }

                    User user = optionalUser.get();

                    TokenInfo tokenInfo = jwtProvider.generateToken(String.valueOf(user.getUserId()),
                            user.getEmail(), user.getName(), user.getProfile(), user.getRole().getValue());

                    response.addHeader("Access-Token", tokenInfo.getAccessToken());
                    Authentication authentication = jwtProvider.getAuthentication(tokenInfo.getAccessToken());
                    context.setAuthentication(authentication);
                }

                // AccessToken 과 RefreshToken 둘 다 유효하지 않다면
                else {
                    log.info("refresh 토큰이 이상해!");
                    context.setAuthentication(null);    // TODO : 아니 context 왜 처음부터 authentication 값 제대로 들어가있냐? 언제 들어감?? 원래 이 코드 없어도 됐는데..
                    throw new BaseException(ErrorMessage.REFRESH_TOKEN_EXPIRE);
                }
            }
        } catch (Exception e) {
            request.setAttribute("exception", e);
        }
        filterChain.doFilter(request, response);
    }
}
