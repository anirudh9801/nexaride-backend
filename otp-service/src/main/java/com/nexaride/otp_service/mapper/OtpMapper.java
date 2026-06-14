package com.nexaride.otp_service.mapper;

import com.nexaride.otp_service.dto.GenerateOtpRequest;
import com.nexaride.otp_service.dto.OtpResponse;
import com.nexaride.otp_service.entity.Otp;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OtpMapper {

    OtpResponse toResponse(Otp otp);
}
