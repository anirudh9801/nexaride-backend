package com.nexaride.otp_service.controller;

import com.nexaride.otp_service.dto.GenerateOtpRequest;
import com.nexaride.otp_service.dto.OtpResponse;
import com.nexaride.otp_service.dto.ValidateOtpRequest;
import com.nexaride.otp_service.entity.Otp;
import com.nexaride.otp_service.mapper.OtpMapper;
import com.nexaride.otp_service.service.OtpService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp")
public class OtpController {
    private final OtpService otpService;
    private final OtpMapper otpMapper;

    public OtpController(OtpService otpService, OtpMapper otpMapper) {
        this.otpService = otpService;
        this.otpMapper = otpMapper;
    }
    @PostMapping("/generate")
    public ResponseEntity<OtpResponse> generateOtp(@Valid @RequestBody GenerateOtpRequest request){
        Otp otp = otpService.generateOtp(request.getEmail());
        OtpResponse response = otpMapper.toResponse(otp);
        response.setMessage("OTP generated/fetched successfully");
        return ResponseEntity.ok(response);
    }
    @PostMapping("/validate")
    public  ResponseEntity<OtpResponse> validateOtp(@Valid @RequestBody ValidateOtpRequest request){
        otpService.validateOtp(
                request.getEmail(),request.getOtp()
        );

        OtpResponse otpResponse = OtpResponse.builder()
                .email(request.getEmail())
                .message("OTP verified, Let's Go")
                .build();

        return ResponseEntity.ok(otpResponse);
    }
}
