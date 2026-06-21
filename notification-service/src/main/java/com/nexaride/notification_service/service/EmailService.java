package com.nexaride.notification_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    public void send(String email, String message){

        log.info("[EMAIL] Sent to {} with message: {}", email, message);
    }
}
