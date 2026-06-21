package com.nexaride.otp_service.service;

import com.nexaride.otp_service.event.BookingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerService {
    @KafkaListener(topics = "booking-created",groupId = "otp-group")
    public void consume(BookingEvent event){
        log.info("Message received form kafka: "+event);
        String email = event.getEmail();
        log.info("OTP generated for user: {}", email);

    }
}
