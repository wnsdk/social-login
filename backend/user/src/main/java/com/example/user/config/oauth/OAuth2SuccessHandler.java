package com.example.user.config.oauth;

import com.example.user.config.jwt.JwtTokenProvider;
import com.example.user.config.jwt.TokenInfo;
import com.example.user.domain.entity.User;
import com.example.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-token-expire-time}")
    private int REFRESH_TOKEN_EXPIRE_TIME_COOKIE;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        String targetUrl = determineTargetUrl(request, response, authentication);

        // 서버가 이미 클라이언트에게 응답을 생성해 보내기 시작했다면??
        if (response.isCommitted()) {
            return;
        }

        // 보안 강화를 위해 인증 관련 속성을 쿠키에서 제거하기
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        // token 발급
        TokenInfo tokenInfo = issueToken(request, response, authentication);

        // Redis 에 RT 저장 (서버용)
        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(),
                        tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        // Cookie 에 RT 갱신 (클라이언트용)
        CookieUtil.deleteCookie(request, response, "RT"); //TODO : 이 부분 고쳐야됨
        CookieUtil.addCookie(response, "RT", tokenInfo.getRefreshToken(), REFRESH_TOKEN_EXPIRE_TIME_COOKIE);

        // response 의 헤더에 AT 담아주기
        response.setHeader("Access-Token", tokenInfo.getAccessToken());
        System.out.println(tokenInfo.getAccessToken());

        // targetUrl로 사용자를 리다이렉션
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtil.getCookie(request, OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        return UriComponentsBuilder.fromUriString(targetUrl)
//                .queryParam("accessToken", tokenInfo.getAccessToken()) //쿼리 파라미터가 아니라 헤더에 담아 보내줘야되는데...
//                .queryParam("refreshToken", tokenInfo.getRefreshToken())
                .build().toUriString();
    }

    private TokenInfo issueToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // registrationId 추출
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = authToken.getAuthorizedClientRegistrationId();

        // 유저 정보 추출
        OidcUser user = ((OidcUser) authentication.getPrincipal());
        OAuth2UserInfo userInfo = OAuth2UserInfo.of(registrationId, user.getAttributes());
        User savedUser = userRepository.findByEmail(userInfo.email()).get();

        // JWT 발급
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(savedUser.getUserId().toString(), savedUser.getEmail(),
                savedUser.getName(), savedUser.getProfile(), savedUser.getRole().getValue());

        return tokenInfo;
    }
}
