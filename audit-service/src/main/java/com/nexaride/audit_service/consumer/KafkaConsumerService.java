package com.nexaride.audit_service.consumer;

import com.nexaride.audit_service.event.BookingEvent;
import com.nexaride.audit_service.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {
    private final AuditService auditService;

    public KafkaConsumerService(AuditService auditService) {
        this.auditService = auditService;
    }

    @KafkaListener(topics = "booking-created", groupId = "audit-log")
    public void consume(BookingEvent event, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition){
        //@Header added here to check the number of thread generated and partition assigning to each thread in logs
        log.info("Audit event received: {}", event);


        if(event.getRideId() % 2 == 0){
            throw new RuntimeException("Simulated DB error ❗");
        }
        log.error("Sending to DLQ: {}", event);

        log.info("Thread: {} | Partition: {} | Event: {}",
                Thread.currentThread().getName(),
                partition,
                event);

        log.debug("Calling service to save into db");
        auditService.saveEvent(event.getRideId(), event.getEmail(), "BOOKING-CREATED");
    }
}
