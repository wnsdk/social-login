package com.example.user.user.service;

import com.example.user.global.exception.BaseException;
import com.example.user.global.exception.ErrorMessage;
import com.example.user.global.model.entity.User;
import com.example.user.global.model.enums.Provider;
import com.example.user.global.model.enums.Role;
import com.example.user.global.model.enums.Status;
import com.example.user.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.user.global.exception.ErrorMessage.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        registerUser("박관리", "admin@gmail.com", "https://bit.ly/4e9bnA3", Role.ADMIN.getValue(), Provider.GOOGLE.getValue());
        registerUser("김유저", "user@gmail.com", "https://bit.ly/3RijqAK", Role.USER.getValue(), Provider.GOOGLE.getValue());
    }

    //회원 가입
    @Override
    public User registerUser(String name, String email, String profile, String role, String provider) {

        userRepository.findByEmail(email).ifPresent(user -> {
            //이미 탈퇴했던 유저
            if (user.getStatus().equals(Status.DELETED.getValue())) {
                throw new BaseException(DELETED_USER);
            }
            //이미 가입한 유저
            else {
                throw new BaseException(EXIST_USER);
            }
        });

        User user = User.builder()
                .name(name)
                .email(email)
                .profile(profile)
                .role(Role.valueOf(role))
                .status(Status.ACTIVE)
                .provider(Provider.valueOf(provider))
                .build();

        return userRepository.save(user);
    }

    //회원 탈퇴
    @Override
    public User deleteUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BaseException(NOT_EXIST_USER));
        user.setStatus(Status.DELETED);
        return user;
    }

    //회원 수정
    @Override
    public User updateUser(String email, String name, String profile) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BaseException(NOT_EXIST_USER));
        user.updateUser(name, profile);
        return user;
    }
}