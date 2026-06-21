package com.nexaride.audit_service.consumer;

import com.nexaride.audit_service.event.BookingEvent;
import com.nexaride.audit_service.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {
    private final AuditService auditService;

    public KafkaConsumerService(AuditService auditService) {
        this.auditService = auditService;
    }

    @KafkaListener(topics = "booking-created", groupId = "audit-log")
    public void consume(BookingEvent event){
        log.info("Audit event received: {}", event);

        log.debug("Calling service to save into db");
        auditService.saveEvent(event.getRideId(), event.getEmail(), "BOOKING-CREATED");
    }
}
