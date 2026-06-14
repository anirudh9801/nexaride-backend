package com.nexaride.otp_service.service;

import com.nexaride.otp_service.entity.Otp;

public interface OtpService {

    Otp generateOtp(String email);
    boolean validateOtp(String email,String otp);
}
