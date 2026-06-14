package com.nexaride.user.service;

import com.nexaride.user.dto.ChangePasswordRequest;
import com.nexaride.user.dto.LoginRequest;
import com.nexaride.user.dto.RegisterRequest;
import com.nexaride.user.dto.UpdateProfileRequest;
import com.nexaride.user.entity.User;
import org.springframework.stereotype.Service;

public interface UserService {
    User registerUser(RegisterRequest request);
    User loginUser(LoginRequest request);
    User updateProfile(String email, UpdateProfileRequest request);
    void changePassword(String email, ChangePasswordRequest request);
}
