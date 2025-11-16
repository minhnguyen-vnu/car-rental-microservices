package com.fleetmanagementservice.kernel.mapper;

import com.fleetmanagementservice.core.dto.request.VehicleBlockRequestDTO;
import com.fleetmanagementservice.core.dto.request.VehicleRequestDTO;
import com.fleetmanagementservice.core.dto.response.VehicleBlockResponseDTO;
import com.fleetmanagementservice.core.dto.response.VehicleResponseDTO;
import com.fleetmanagementservice.core.entity.Vehicle;
import com.fleetmanagementservice.core.entity.VehicleBlock;
import com.fleetmanagementservice.core.event.RentalEvent;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class VehicleBlockMapper {
    private VehicleBlockMapper() {}

    public static VehicleBlockResponseDTO toResponse(VehicleBlock e) {
        if (e == null) return null;
        return VehicleBlockResponseDTO.builder()
                .id(e.getId())
                .vehicleId(e.getVehicleId())
                .rentalId(e.getRentalId())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .note(e.getNote())
                .build();
    }

    public static List<VehicleBlockResponseDTO> toResponseList(Collection<VehicleBlock> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .filter(Objects::nonNull)
                .map(VehicleBlockMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static VehicleBlock toEntity(VehicleBlockRequestDTO dto) {
        if (dto == null) return null;
        return VehicleBlock.builder()
                .id(dto.getId())
                .vehicleId(dto.getVehicleId())
                .rentalId(dto.getRentalId())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .note(dto.getNote())
                .build();
    }

    public static VehicleBlock toEntity(RentalEvent event) {
        if (event == null) return null;
        return VehicleBlock.builder()
                .vehicleId(event.getVehicleId())
                .rentalId(event.getRentalId())
                .build();
    }
}
