package com.example.user.oauth;

import com.example.user.global.model.entity.User;
import com.example.user.global.model.enums.Provider;
import com.example.user.global.util.CookieUtil;
import com.example.user.jwt.JwtProvider;
import com.example.user.jwt.TokenInfo;
import com.example.user.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.example.user.oauth.OAuth.*;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-token-expire-time}")
    private int REFRESH_TOKEN_EXPIRE_TIME;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        String targetUrl = determineTargetUrl(request, response, authentication);

        // 인증 관련 속성 제거
        super.clearAuthenticationAttributes(request);
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME.getValue());
        CookieUtil.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME.getValue());

        // targetUrl 로 사용자를 리디렉션
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME.getValue())
                .map(Cookie::getValue);
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        // token 발급
        TokenInfo tokenInfo = issueToken(request, response, authentication);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", tokenInfo.getAccessToken())  // 리디렉션 url 쿼리파라미터에 AT 전달
                .build().toUriString();
    }

        private TokenInfo issueToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // registrationId 추출
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = authToken.getAuthorizedClientRegistrationId();
        Provider provider = Provider.valueOf(registrationId.toUpperCase());

        // 유저 정보 추출
        OidcUser oidcUser = ((OidcUser) authentication.getPrincipal());
        OAuth2UserInfo userInfo = OAuth2UserInfo.of(provider, oidcUser.getAttributes());
        User user = userRepository.findByEmail(userInfo.email()).get();

        // JWT 발급
        TokenInfo tokenInfo = jwtProvider.generateToken(user.getId().toString(), user.getEmail(), user.getName(),
                user.getProfile(), user.getRole().getValue());

        // Redis 에 RT 저장 (서버용)
        redisTemplate.opsForValue()
                .set(REFRESH_TOKEN_REDIS_NAME.getValue() + authentication.getName(), tokenInfo.getRefreshToken(),
                        REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        // Cookie 에 RT 갱신 (클라이언트용)
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME.getValue());
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME.getValue(), tokenInfo.getRefreshToken(), REFRESH_TOKEN_EXPIRE_TIME);

        return tokenInfo;
    }

}
