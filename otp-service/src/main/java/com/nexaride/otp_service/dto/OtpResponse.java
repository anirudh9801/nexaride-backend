package com.nexaride.otp_service.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpResponse {
    private String email;
    private String otp;
    private String message;
}
