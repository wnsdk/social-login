package com.example.user.oauth;

import com.example.user.global.model.enums.Provider;
import lombok.Builder;

import java.util.Map;

import static com.example.user.global.model.enums.Provider.GOOGLE;

@Builder
public record OAuth2UserInfo(
        String email,
        String name,
        String profile,
        Provider provider
) {

    public static OAuth2UserInfo of(Provider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case GOOGLE -> ofGoogle(attributes);
            default -> throw new IllegalArgumentException("Invalid Provider Type.");
        };
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profile((String) attributes.get("picture"))
                .provider(GOOGLE)
                .build();
    }
}
