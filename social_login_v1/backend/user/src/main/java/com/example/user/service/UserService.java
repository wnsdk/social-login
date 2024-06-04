package com.example.user.service;

import com.example.user.domain.entity.User;

import java.util.List;

public interface UserService {

    User registerUser(String name, String email, String profile, String role);

}
