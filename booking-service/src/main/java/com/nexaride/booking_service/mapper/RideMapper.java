package com.nexaride.booking_service.mapper;

import com.nexaride.booking_service.dto.CreateRideRequest;
import com.nexaride.booking_service.dto.RideResponse;
import com.nexaride.booking_service.entity.Ride;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RideMapper {
    Ride toEntity(CreateRideRequest request);
    @Mapping(source = "id", target = "rideId")
    RideResponse toResponse(Ride ride);
}
