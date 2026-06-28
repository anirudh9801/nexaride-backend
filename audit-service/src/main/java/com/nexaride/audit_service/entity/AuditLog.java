package com.nexaride.audit_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "audit_log",uniqueConstraints = @UniqueConstraint(columnNames = {"rideId","eventType"}))
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rideId;

    private String email;

    private String eventType;

    private LocalDateTime createdAt;
}
