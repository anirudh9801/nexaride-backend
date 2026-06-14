package com.nexaride.booking_service.controller;

import com.nexaride.booking_service.dto.CreateRideRequest;
import com.nexaride.booking_service.dto.RideResponse;
import com.nexaride.booking_service.service.RideService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rides")
public class RideController {
    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping("/create")
    public ResponseEntity<RideResponse> createRide(@RequestAttribute("email") String email,
                                                   @Valid @RequestBody CreateRideRequest request){
        RideResponse response = rideService.createRide(email,request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/start")
    public ResponseEntity<RideResponse> startRide(
            @RequestAttribute("email") String email,
            @RequestParam Long rideId,
            @RequestParam String otp) {

        RideResponse response = rideService.startRide(rideId, otp, email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete")
    public ResponseEntity<RideResponse> completeRide(
            @RequestAttribute("email") String email,
            @RequestParam Long rideId) {

        RideResponse response = rideService.completeRide(rideId, email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel")
    public ResponseEntity<RideResponse> cancelRide(
            @RequestAttribute("email") String email,
            @RequestParam Long rideId) {

        RideResponse response = rideService.cancelRide(rideId, email);
        return ResponseEntity.ok(response);
    }


}
