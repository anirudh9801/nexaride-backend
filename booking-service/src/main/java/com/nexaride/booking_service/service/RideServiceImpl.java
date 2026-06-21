package com.nexaride.booking_service.service;

import com.nexaride.booking_service.client.OtpClient;
import com.nexaride.booking_service.dto.*;
import com.nexaride.booking_service.entity.Ride;
import com.nexaride.booking_service.entity.RideStatus;
import com.nexaride.booking_service.event.BookingEvent;
import com.nexaride.booking_service.mapper.RideMapper;
import com.nexaride.booking_service.repository.RideRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@Service
public class RideServiceImpl implements RideService{
    private final RideRepository rideRepository;
    private final GeoService geoService;
    private final RideMapper rideMapper;
    private final OtpClient otpClient;
    private final KafkaProducerService kafkaProducerService;



    public RideServiceImpl(RideRepository rideRepository, GeoService geoService,
                           RideMapper rideMapper, OtpClient otpClient,
                           KafkaProducerService kafkaProducerService
                           ) {
        this.rideRepository = rideRepository;
        this.geoService = geoService;
        this.rideMapper = rideMapper;
        this.otpClient = otpClient;
        this.kafkaProducerService = kafkaProducerService;
    }
    @Value("${internal.api.key}")
    private String internalApiKey;

    @Override
    public RideResponse createRide(String email, CreateRideRequest request) {
        log.info("inside createRide method");
        //Dto -> Entity
        Ride ride = rideMapper.toEntity(request);
        //User set
        ride.setUserEmail(email);
        log.info("after setting userEmail: "+ride.getUserEmail());

        //Geo Conversion
        double[] source = geoService.getCoordinates(request.getSourceAddress());
        double[] destination = geoService.getCoordinates(request.getDestinationAddress());
        log.info("Source: "+ Arrays.toString(source));
        log.info("Destination: "+ Arrays.toString(destination));


        ride.setSourceLatitude(source[0]);
        ride.setSourceLongitude(source[1]);
        log.info("After Setting Source latitude: "+ride.getSourceLatitude());
        log.info("After Setting Source longitude: "+ride.getSourceLongitude());

        ride.setDestinationLatitude(destination[0]);
        ride.setDestinationLongitude(destination[1]);
        log.info("After Setting Destination latitude: "+ride.getDestinationLatitude());
        log.info("After Setting Destination Longitude: "+ride.getDestinationLongitude());


        ride.setStatus(RideStatus.OTP_GENERATED);
        Ride saved = rideRepository.save(ride);


       //Feign call -> Otp Service
        log.info("Feign call initiating to call otp service");
        OtpResponse otpResponse = otpClient.generateOtp(
                internalApiKey, new GenerateOtpRequest(email)
        );
        //Async Event
        BookingEvent event = new BookingEvent(saved.getId(), email, "BOOKED");
        kafkaProducerService.sendBookingEvent(event);

        log.info("OTP generated: {}", otpResponse.getOtp());
        RideResponse response = rideMapper.toResponse(saved);
        response.setOtp(otpResponse.getOtp());
        return response;

    }

    @Override
    public RideResponse startRide(Long rideId, String otpInput, String email) {
        log.info("inside startRide method");
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(()->new RuntimeException("Ride not found"));

        log.info("UserEmail: "+ride.getUserEmail());
        if (!ride.getUserEmail().equals(email)) {
            throw new RuntimeException("Unauthorized user for this ride");
        }


        log.info("Feign call for otpValidation from Otp Service");
        //Validate otp by feign
        otpClient.validateOtp(
                internalApiKey,new ValidateOtpRequest(email,otpInput)
        );

        ride.setStatus(RideStatus.STARTED);
        ride.setStartedAt(LocalDateTime.now());

        Ride updated = rideRepository.save(ride);
        return rideMapper.toResponse(updated);
    }

    @Override
    public RideResponse completeRide(Long rideId, String email) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(()-> new RuntimeException("Ride not found"));

        if(!ride.getUserEmail().equals(email)){
            throw new RuntimeException("Unauthorized user");
        }
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());
        Ride updated = rideRepository.save(ride);
        log.info("Ride completed successfully for rideId: {}", rideId);
        return rideMapper.toResponse(updated);
    }

    @Override
    public RideResponse cancelRide(Long rideId, String email) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(()-> new RuntimeException("Ride not found"));


        if (!ride.getUserEmail().equals(email)) {
            throw new RuntimeException("Unauthorized user");
        }

        ride.setStatus(RideStatus.CANCELLED);
        Ride updated = rideRepository.save(ride);
        return rideMapper.toResponse(updated);


    }


}
