package com.nexaride.notification_service.consumer;

import com.nexaride.notification_service.event.BookingEvent;
import com.nexaride.notification_service.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {
    private final NotificationService notificationService;

    public KafkaConsumerService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    @KafkaListener(topics = "booking-created")
    public void consume(BookingEvent event){
        log.info("Received booking event: "+event);
        notificationService.sendNotification(event.getEmail());
    }
}
