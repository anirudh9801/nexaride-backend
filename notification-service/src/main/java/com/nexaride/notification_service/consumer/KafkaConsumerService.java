package com.nexaride.notification_service.consumer;

import com.nexaride.notification_service.event.BookingEvent;
import com.nexaride.notification_service.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class KafkaConsumerService {
    private final NotificationService notificationService;
    private final StringRedisTemplate redisTemplate;

    public KafkaConsumerService(NotificationService notificationService, StringRedisTemplate redisTemplate) {
        this.notificationService = notificationService;
        this.redisTemplate = redisTemplate;
    }
    @KafkaListener(topics = "ride-events")
    public void consume(BookingEvent event){
        //IDEMPOTENCY KEY
        String key = "event:"+event.getRideId()+"-"+event.getStatus();
        log.info("Idempotency key: "+key);

        //Redis check
        if(redisTemplate.hasKey(key)){
            log.warn("Duplicate notification skipped: {}", key);
            return;
        }
        log.info("Received booking event: "+event);
        switch (event.getStatus()){
            case "BOOKED":
                notificationService.sendNotification(event.getEmail(),"Ride Booked");
                break;
            case "STARTED":
                notificationService.sendNotification(event.getEmail(),"Ride started");
                break;
            case "COMPLETED":
                notificationService.sendNotification(event.getEmail(),"Ride completed");
                break;

            case "CANCELLED":
                notificationService.sendNotification(event.getEmail(), "Ride cancelled");
        }
        redisTemplate.opsForValue().set(key,"sent", Duration.ofHours(24));
    }
}
