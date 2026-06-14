package com.nexaride.user.controller;

import com.nexaride.user.dto.ChangePasswordRequest;
import com.nexaride.user.dto.UpdateProfileRequest;
import com.nexaride.user.dto.UserResponse;
import com.nexaride.user.entity.User;
import com.nexaride.user.exception.UserNotFoundException;
import com.nexaride.user.mapper.UserMapper;
import com.nexaride.user.repository.UserRepository;
import com.nexaride.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserMapper userMapper, UserService userService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userService = userService;
    }

    //Here->we are injecting Authentication as we already set the auth object in JWTAuthFilter
    //We store pricipal-> email, cred-> null, authorites/role -> null here right now

/*→ Spring automatically inject Authentication in controller
→ Authentication comes from SecurityContextHolder */
    @PreAuthorize("hasRole('USER')")
@GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(Authentication authentication){
        String email = authentication.getName();
        log.info("Email extracted from JWT token");
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User Not Found"));
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponse> updateProfile(@RequestBody @Valid UpdateProfileRequest
                                                      request, Authentication authentication){
    String email = authentication.getName();
    User updatedUser = userService.updateProfile(email,request);
    return ResponseEntity.ok(userMapper.toResponse( updatedUser));
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody @Valid
                                                     ChangePasswordRequest request, Authentication authentication){
    String email = authentication.getName();
    userService.changePassword(email,request);
    return ResponseEntity.ok("Password updated successfully");
    }

}
