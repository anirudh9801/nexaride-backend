package com.nexaride.booking_service.repository;

import com.nexaride.booking_service.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<Ride,Long> {
}
