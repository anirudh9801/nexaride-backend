package com.nexaride.otp_service.service;

import com.nexaride.otp_service.entity.Otp;
import com.nexaride.otp_service.exception.OtpException;
import com.nexaride.otp_service.repository.OtpRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService{
    private final OtpRepository otpRepository;

    public OtpServiceImpl(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }


    private static final int OTP_VALIDITY_MINUTES = 10;

    //Generate or reuse otp
    @Override
    public Otp generateOtp(String email) {
        Optional<Otp> existingOtp = otpRepository.findByEmail(email);

        //case 1: existing & not expired → reuse
        if(existingOtp.isPresent()){
            Otp otp = existingOtp.get();
            if(!isExpired(otp)){
                return otp;
            }
        }
        //case 2: expired OR not present → generate new
        String newOtp = generateRandomOtp();
        Otp otp = existingOtp.orElse(new Otp());
        otp.setEmail(email);
        otp.setOtp(newOtp);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
        return otpRepository.save(otp);
    }

    //Validating OTP Driver side business logic
    @Override
    public boolean validateOtp(String email, String otpInput) {
        Otp otp = otpRepository.findByEmail(email)
                .orElseThrow(()->new OtpException("OTP not found for user"));
        if(isExpired(otp)){
            throw new OtpException("OTP Expired");
        }

        if (!otp.getOtp().equals(otpInput)) {
            throw new OtpException("Invalid OTP");
        }

        return true;
    }

    //Expiry Check
    private boolean isExpired(Otp otp){
        return otp.getExpiresAt().isBefore(LocalDateTime.now());
    }

    //Generate 6 digit random otp
    public String generateRandomOtp(){
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
