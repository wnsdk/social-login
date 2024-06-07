package com.example.user.service;

import com.example.user.config.jwt.JwtProvider;
import com.example.user.domain.entity.User;
import com.example.user.domain.enums.Role;
import com.example.user.domain.enums.Status;
import com.example.user.exception.BaseException;
import com.example.user.exception.ErrorMessage;
import com.example.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
//        registerUser("박관리", "admin@example.com", "https://bit.ly/4e9bnA3", "ADMIN");
//        registerUser("김유저", "user@example.com", "https://bit.ly/3RijqAK", "USER");
    }

    @Override
    public User registerUser(String name, String email, String profile, String role) throws Exception {

        if (userRepository.findByEmail(email).isPresent()) {
            throw new BaseException(ErrorMessage.EXIST_EMAIL);
        }

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
