package com.nexaride.booking_service.client;

import com.nexaride.booking_service.dto.GenerateOtpRequest;
import com.nexaride.booking_service.dto.OtpResponse;
import com.nexaride.booking_service.dto.ValidateOtpRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "otp-service",url = "${otp.service.url}")
public interface OtpClient {
    @PostMapping("/otp/generate")
    OtpResponse generateOtp(@RequestHeader("X-API-KEY") String apiKey,
                            @RequestBody GenerateOtpRequest request);
    @PostMapping("/otp/validate")
    OtpResponse validateOtp(@RequestHeader("X-API-KEY") String apiKey,
                            @RequestBody ValidateOtpRequest request);
}
