package com.example.user.user.service;

import com.example.user.global.exception.BaseException;
import com.example.user.global.model.entity.User;
import com.example.user.global.model.enums.Provider;
import com.example.user.global.model.enums.Role;
import com.example.user.global.model.enums.Status;
import com.example.user.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private String name;
    private String email;
    private String profile;
    private String role;
    private String provider;

    @BeforeEach
    private void beforeEach() {
        name = "홍길동";
        email = "gildong@example.com";
        profile = "https://bit.ly/3X85b5g";
        role = Role.USER.getValue();
        provider = Provider.GOOGLE.getValue();
    }

    @AfterEach
    private void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 가입 성공 테스트")
    void shouldRegisterUserSuccessfully() {
        //when
        userService.registerUser(name, email, profile, role, provider);

        //then
        User foundUser = userRepository.findByEmail(email).orElse(null);
        assertNotNull(foundUser);
        assertEquals(email, foundUser.getEmail());
    }

    @Test
    @DisplayName("이미 등록된 유저, 회원 가입 실패 테스트")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        //given
        userService.registerUser(name, email, profile, role, provider);

        //when
        assertThrows(BaseException.class, () -> {
            userService.registerUser(name, email, profile, role, provider);
        });
    }

    @Test
    @DisplayName("이미 탈퇴한 유저, 회원 가입 실패 테스트")
    void shouldFailRegistrationForPreviouslyDeletedUser() {
        //given
        userService.registerUser(name, email, profile, role, provider);
        userService.deleteUser(email);

        //when
        assertThrows(BaseException.class, () -> {
            userService.registerUser(name, email, profile, role, provider);
        });
    }

    @Test
    @DisplayName("회원 탈퇴 성공 테스트")
    void shouldDeleteUserSuccessfully() {
        //given
        userService.registerUser(name, email, profile, role, provider);

        //when
        userService.deleteUser(email);

        //then
        User foundUser = userRepository.findByEmail(email).orElse(null);
        assertNotNull(foundUser);

        assertEquals(Status.DELETED, foundUser.getStatus());
    }

    @Test
    @DisplayName("존재하지 않는 유저, 회원 탈퇴 실패 테스트")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        //when
        assertThrows(BaseException.class, () -> {
            userService.deleteUser(email);
        });
    }
}