package com.example.user.oauth;

import com.example.user.global.model.entity.User;
import com.example.user.global.model.enums.Provider;
import com.example.user.global.model.enums.Role;
import com.example.user.global.model.enums.Status;
import com.example.user.user.repository.UserRepository;
import com.example.user.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 유저 정보(OAuth2UserInfo) 생성
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();    // third-party id (ex. google)
        Provider provider = Provider.valueOf(registrationId.toUpperCase());
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(provider, oAuth2UserAttributes);

        // 존재하지 않는 유저라면 회원가입, 존재하는 유저라면 업데이트
        User user = userRepository.findByEmail(oAuth2UserInfo.email()).orElse(null);
        if (user == null) {
            user = userService.registerUser(oAuth2UserInfo.name(), oAuth2UserInfo.email(), oAuth2UserInfo.profile(),
                    Role.USER.getValue(), provider.getValue());
        }
        else {
            user = userService.updateUser(oAuth2UserInfo.email(), oAuth2UserInfo.name(), oAuth2UserInfo.profile());
        }

        return new PrincipalDetails(user, oAuth2UserAttributes);
    }
}