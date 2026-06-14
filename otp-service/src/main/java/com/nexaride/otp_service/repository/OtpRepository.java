package com.nexaride.otp_service.repository;

import com.nexaride.otp_service.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp,Long> {
    //Fetch otp by email
    Optional<Otp> findByEmail(String email);
}
