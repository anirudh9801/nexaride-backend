package com.nexaride.audit_service.service;

import com.nexaride.audit_service.entity.AuditLog;
import com.nexaride.audit_service.repository.AuditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AuditService {
    private final AuditRepository auditRepository;


    public AuditService(AuditRepository auditRepository ) {
        this.auditRepository = auditRepository;
    }

    public void saveEvent(Long rideId, String email, String eventType){
        AuditLog auditLog = new AuditLog();
        log.debug("inside saveEvent method");
        log.debug("Ride id: "+rideId);
        auditLog.setRideId(rideId);
        log.debug("Email: "+email);
        auditLog.setEmail(email);
        log.debug("Evenet type: "+eventType);
        auditLog.setEventType(eventType);
        log.debug("Created At: "+auditLog.getCreatedAt());
        auditLog.setCreatedAt(LocalDateTime.now());

        log.debug("Saving inside DB");
        auditRepository.save(auditLog);
    }
}
