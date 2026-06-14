package com.nexaride.user.controller;

import com.nexaride.user.dto.LoginRequest;
import com.nexaride.user.dto.LoginResponse;
import com.nexaride.user.dto.RegisterRequest;
import com.nexaride.user.dto.UserResponse;
import com.nexaride.user.entity.User;
import com.nexaride.user.mapper.UserMapper;
import com.nexaride.user.service.JwtService;
import com.nexaride.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public AuthController(UserService userService, UserMapper userMapper, JwtService jwtService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
    }
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){
        User user = userService.registerUser(request);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        log.info("Calling loginUser for cred Validation");
        User user = userService.loginUser(request);
        log.info("calling generateToken method to safely generate token after validation");
        String token = jwtService.generateToken(user);
        LoginResponse loginResponse = LoginResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .build();
        return ResponseEntity.ok(loginResponse);
    }
}
