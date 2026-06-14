package com.nexaride.booking_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rides", indexes = {
        @Index(name = "idx_user_email", columnList = "userEmail")
})
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String userEmail;
    @Column(nullable = false)
    private String sourceAddress;
    @Column(nullable = false)
    private String destinationAddress;
    private Double sourceLatitude;
    private Double sourceLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = RideStatus.BOOKED;
    }
}
