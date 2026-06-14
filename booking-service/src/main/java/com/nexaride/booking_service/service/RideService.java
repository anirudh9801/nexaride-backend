package com.nexaride.booking_service.service;

import com.nexaride.booking_service.dto.CreateRideRequest;
import com.nexaride.booking_service.dto.RideResponse;

public interface RideService {
    RideResponse createRide(String email, CreateRideRequest request);
    RideResponse startRide(Long rideId,String otpInput, String email);
    RideResponse completeRide(Long rideId, String email);
    RideResponse cancelRide(Long rideId, String email);
}
