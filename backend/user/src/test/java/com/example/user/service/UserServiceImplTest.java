package com.example.user.service;

import com.example.user.domain.entity.User;
import com.example.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void registerUser() {
        // given
        String name = "gildong";
        String email = "gildong@example.com";
        String profile = "Profile of Gildong";

        // when
        User savedUser = userService.registerUser(name, email, profile);

        // then
        assertNotNull(savedUser);

        User findUser = userRepository.findById(savedUser.getUserId()).orElse(null);
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(findUser);
    }
}