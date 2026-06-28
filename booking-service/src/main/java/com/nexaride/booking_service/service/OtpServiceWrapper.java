package com.nexaride.booking_service.service;

import com.nexaride.booking_service.client.OtpClient;
import com.nexaride.booking_service.dto.GenerateOtpRequest;
import com.nexaride.booking_service.dto.OtpResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OtpServiceWrapper {

    @Value("${internal.api.key}")
    private  String internalApiKey;

    private final OtpClient otpClient;

    public OtpServiceWrapper(OtpClient otpClient) {
        this.otpClient = otpClient;
    }
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name  ="otpService",fallbackMethod = "otpFallback")
    public OtpResponse callOtpService(String email){
        log.info("Calling OTP service via Circuit Breaker");
        return otpClient.generateOtp(
                internalApiKey,new GenerateOtpRequest(email)
        );
    }

    public OtpResponse otpFallback(String email, Exception ex){
        log.error("OTP Service is DOWN, Using fallback", ex);
        throw  new RuntimeException("There was an error generating OTP, Please try again.");
    }
}
