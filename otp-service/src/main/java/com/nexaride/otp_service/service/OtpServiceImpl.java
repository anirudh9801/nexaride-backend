package com.nexaride.otp_service.service;

import com.nexaride.otp_service.config.RedisConfig;
import com.nexaride.otp_service.entity.Otp;
import com.nexaride.otp_service.exception.OtpException;
import com.nexaride.otp_service.repository.OtpRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class OtpServiceImpl implements OtpService{
    private final OtpRepository otpRepository;
    private final RedisTemplate<String,String> redisTemplate;

    public OtpServiceImpl(OtpRepository otpRepository, RedisTemplate<String, String> redisTemplate) {
        this.otpRepository = otpRepository;
        this.redisTemplate = redisTemplate;
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
                log.info("Reusing OTP from DB for email: {}", email);
                //Making copy of the otp in redis, "Cache Warmup"
                redisTemplate.opsForValue()
                        .set(email,otp.getOtp(),Duration.ofMinutes(OTP_VALIDITY_MINUTES));
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
        otpRepository.save(otp);
        //Redis Save
        log.info("Saving inside redis after DB save");
        redisTemplate.opsForValue()
                .set(email,newOtp,Duration.ofMinutes(OTP_VALIDITY_MINUTES));
        return otp;

    }

    //Validating OTP Driver side business logic
    // Implementing Cache Aside pattern of redis
    @Override
    public boolean validateOtp(String email, String otpInput) {
        //First redis Hit
        String cachedOtp = redisTemplate.opsForValue().get(email);
        if(cachedOtp!=null){
            log.info("Redis hit for email:{}",email);
            return otpInput.equals(cachedOtp);
        }
        log.info("Redis miss -> DB lookup for email:{}",email);
        Otp otp = otpRepository.findByEmail(email)
                .orElseThrow(()->new OtpException("OTP not found for user"));
        if(isExpired(otp)){
            throw new OtpException("OTP Expired");
        }

        if (otp.getOtp().equals(otpInput)) {
            //Cache Warm-up
            redisTemplate.opsForValue().set(
                    email,otp.getOtp(),Duration.ofMinutes(OTP_VALIDITY_MINUTES)
            );
            return true;
        }

        throw new OtpException("Invalid Otp");
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
