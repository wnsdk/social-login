package com.example.user.user.service;

import com.example.user.global.model.entity.User;

public interface UserService {

    User registerUser(String name, String email, String profile, String role, String provider);

    User deleteUser(String email);

    User updateUser(String email, String name, String profile);
}