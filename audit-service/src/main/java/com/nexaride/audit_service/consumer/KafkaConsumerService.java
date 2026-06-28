package com.nexaride.audit_service.consumer;

import com.nexaride.audit_service.event.BookingEvent;
import com.nexaride.audit_service.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class KafkaConsumerService {
    private final AuditService auditService;
    private final StringRedisTemplate redisTemplate;

    public KafkaConsumerService(AuditService auditService, StringRedisTemplate redisTemplate) {
        this.auditService = auditService;
        this.redisTemplate = redisTemplate;
    }

    @KafkaListener(topics = "ride-events", groupId = "audit-log")
    public void consume(BookingEvent event, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        //@Header added here to check the number of thread generated and partition assigning to each thread in logs
        log.info("Audit event received: {}", event);


        //IDEMPOTENCY KEY
        String key = "event:" + event.getRideId() + "-" + event.getStatus();

        //Redis check
        try{
            if (redisTemplate.hasKey(key)) {
                log.warn("Duplicate event detected, skipping: {}", key);
                return;
            }
        }catch (Exception e){
                log.warn("Redis down, proceeding without idempotency");
        }


        log.info("Thread: {} | Partition: {} | Event: {}",
                Thread.currentThread().getName(),
                partition,
                event);

        log.debug("Calling service to save into db");
        try {
            //SWITCH Based event handelling
            switch (event.getStatus()) {
                case "BOOKED":
                    auditService.saveEvent(event.getRideId(), event.getEmail(), "BOOKED");
                    break;
                case "STARTED":
                    auditService.saveEvent(event.getRideId(), event.getEmail(), "STARTED");
                    break;
                case "COMPLETED":
                    auditService.saveEvent(event.getRideId(), event.getEmail(), "COMPLETED");
                    break;
                case "CANCELLED":
                    auditService.saveEvent(event.getRideId(), event.getEmail(), "CANCELLED");
                    break;


                default:
                    log.warn("Unknown event status: {}", event.getStatus());


            }
            //mark as proccesed event
            try{
                redisTemplate.opsForValue().set(key, "processed", Duration.ofHours(24));
            }catch (Exception e){
                log.warn("Redis down, unable to mark processed");
            }


        } catch (Exception ex) {
            log.error("Error processing event: {}", event, ex);
            throw ex;
        }
    }
}
