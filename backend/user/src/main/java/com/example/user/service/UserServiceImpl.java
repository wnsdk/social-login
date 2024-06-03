package com.example.user.service;

import com.example.user.domain.entity.User;
import com.example.user.domain.enums.Role;
import com.example.user.domain.enums.Status;
import com.example.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User registerUser(String name, String email, String profile) {

        User user = User.builder()
                .name(name)
                .email(email)
                .profile(profile)
                .role(Role.ROLE_USER)
                .status(Status.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        return savedUser;
    }
}
