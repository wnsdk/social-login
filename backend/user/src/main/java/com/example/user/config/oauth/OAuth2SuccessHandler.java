package com.example.user.config.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        String targetUrl = determineTargetUrl(request, response, authentication);

        // 서버가 이미 클라이언트에게 응답을 생성해 보내기 시작했다면
        if (response.isCommitted()) {
            return;
        }

        // 보안 강화를 위해 인증 관련 속성을 쿠키에서 제거하기
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        // targetUrl로 사용자를 리다이렉션
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtil.getCookie(request, OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        // TODO : JWT Provider 만들기. AccessToken은 레디스에, RefreshToken은 쿠키에 넣던가? 그리고 그 이후에 오는 요청들 검증하는 코드도 짜야됨

        return UriComponentsBuilder.fromUriString(targetUrl)
//                .queryParam("accessToken", tokenInfo.getAccessToken())
//                .queryParam("refreshToken", tokenInfo.getRefreshToken())
                .build().toUriString();
    }
}
