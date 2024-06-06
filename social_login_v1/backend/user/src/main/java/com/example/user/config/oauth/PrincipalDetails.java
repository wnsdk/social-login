package com.example.user.config.oauth;

import com.example.user.domain.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public record PrincipalDetails(
        User user,
        Map<String, Object> attributes) implements OAuth2User, OidcUser {

    /**
     * @return 사용자 이름
     */
    @Override
    public String getName() {
        return String.valueOf(user.getUserId());
    }

    /**
     * @return 사용자 속성 맵
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * @return 사용자 권한
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().getValue()));
    }

    /**
     * @return OpenID Connect 인증에서 사용되는 클레임(사용자에 대한 추가적인 정보)
     */
    @Override
    public Map<String, Object> getClaims() {
        return null;
    }

    /**
     * @return OpenID Connect 인증에서 사용되는 사용자 정보 엔드포인트의 사용자 정보
     */
    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    /**
     * @return OpenId Connect 인증에서 사용되는 ID 토큰
     */
    @Override
    public OidcIdToken getIdToken() {
        return null;
    }
}