package com.example.user.jwt;

import com.example.user.global.exception.BaseException;
import com.example.user.global.model.entity.User;
import com.example.user.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.example.user.global.exception.ErrorMessage.INVALID_ACCESS_TOKEN;
import static com.example.user.global.exception.ErrorMessage.NOT_EXIST_USER;
import static com.example.user.global.model.enums.Status.DELETED;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
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
            else {
                throw new BaseException(INVALID_ACCESS_TOKEN);
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
}
