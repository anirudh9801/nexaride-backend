package com.nexaride.user.service;

import com.nexaride.user.dto.ChangePasswordRequest;
import com.nexaride.user.dto.LoginRequest;
import com.nexaride.user.dto.RegisterRequest;
import com.nexaride.user.dto.UpdateProfileRequest;
import com.nexaride.user.entity.Role;
import com.nexaride.user.entity.User;
import com.nexaride.user.exception.InvalidCredentialsException;
import com.nexaride.user.exception.UserAlreadyExistsException;
import com.nexaride.user.exception.UserNotFoundException;
import com.nexaride.user.mapper.UserMapper;
import com.nexaride.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User registerUser(RegisterRequest request) {
        log.info("Inside registerUser method");
        log.info("Register request received for email: "+request.getEmail());
        //Duplicate check
        if(userRepository.existsByEmail(request.getEmail())){
            log.error("Email already exists: "+request.getEmail());
            throw new UserAlreadyExistsException("Email already registered");
        }
        //DTO -> Entity(Mapper)
        User user = userMapper.toEntity(request);
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("User Created with id: "+user.getId());
        return user;
    }

    @Override
    public User loginUser(LoginRequest request) {
        log.info("Inside loginUser method.");
        log.info("Login request received for email: "+request.getEmail());

        //Find User
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new UserNotFoundException("User not found"));
        //Password Match
        if(!bCryptPasswordEncoder.matches(request.getPassword(),user.getPassword())){
            log.error("Invalid password for email: "+request.getEmail());
            throw new InvalidCredentialsException("Invalid Credential");
        }
        log.info("User login successful for email: "+request.getEmail());
        return user;
    }

    @Override
    public User updateProfile(String email, UpdateProfileRequest request) {
        log.info("Updating profile for the email: "+email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new UserNotFoundException("User Not Found"));
        userMapper.updateUserFromDto(request,user);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        log.info("Profile updated successfully for email: "+email);
        return user;
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {
        log.info("Changing password for email: "+ email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("User Not Found"));
        //Verifying old password
        if(!bCryptPasswordEncoder.matches(request.getOldPassword(), user.getPassword())){
            log.error("Invalid old password for email: "+email);
            throw new InvalidCredentialsException("Old password is incorrect");
        }
        //Same Password Error
        if (bCryptPasswordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("New password cannot be same as old password");
        }

        //Encode new password
        user.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        log.info("Password changed successfully for email: "+email);
    }
}
