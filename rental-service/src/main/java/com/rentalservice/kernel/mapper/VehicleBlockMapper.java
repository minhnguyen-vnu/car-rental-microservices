package com.rentalservice.kernel.mapper;

import com.rentalservice.core.dto.request.VehicleBlockRequestDTO;
import com.rentalservice.core.dto.request.VehicleRequestDTO;

import java.time.LocalDateTime;

public class VehicleBlockMapper {
    private VehicleBlockMapper() {}

    public static VehicleBlockRequestDTO toRequest(Integer vehicleId, LocalDateTime startTime, LocalDateTime endTime) {
        return VehicleBlockRequestDTO.builder()
                .vehicleId(vehicleId)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
