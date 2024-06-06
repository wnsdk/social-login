package com.example.user.service;

import com.example.user.domain.entity.User;
import com.example.user.domain.enums.Role;
import com.example.user.domain.enums.Status;
import com.example.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
//        registerUser("관리자", "admin@example.com", "image url", "ADMIN");
//        registerUser("김유저", "user@example.com", "image url", "USER");
    }

    @Override
    public User registerUser(String name, String email, String profile, String role) {

        User user = User.builder()
                .name(name)
                .email(email)
                .profile(profile)
                .role(Role.valueOf(role))
                .status(Status.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        return savedUser;
    }

}
