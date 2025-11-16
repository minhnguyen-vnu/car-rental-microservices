package com.rentalservice.kernel.mapper;

import com.rentalservice.core.dto.request.VehicleRequestDTO;

public class FleetMapper {
    private FleetMapper() {}

    public static VehicleRequestDTO toRequest(Integer vehicleId) {
        return VehicleRequestDTO.builder()
                .id(vehicleId)
                .build();
    }
}
