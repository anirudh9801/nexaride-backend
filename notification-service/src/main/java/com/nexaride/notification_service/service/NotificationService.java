package com.nexaride.notification_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    private final EmailService emailService;
    private final SmsService smsService;

    public NotificationService(EmailService emailService, SmsService smsService) {
        this.emailService = emailService;
        this.smsService = smsService;
    }

    public void sendNotification(String email){
log.info("Inside sendNotification method.");
        String message = "Your ride has been booked successfully";
        log.info("Calling email service");
        emailService.send(email,message);
        log.info("Calling sms service");
        smsService.send(email,message);
    }
}
