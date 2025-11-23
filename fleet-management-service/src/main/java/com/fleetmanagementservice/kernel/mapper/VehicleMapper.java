package com.fleetmanagementservice.kernel.mapper;

import com.fleetmanagementservice.core.dto.request.VehicleRequestDTO;
import com.fleetmanagementservice.core.dto.response.VehicleResponseDTO;
import com.fleetmanagementservice.core.entity.Vehicle;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class VehicleMapper {
    private VehicleMapper() {}

    public static VehicleResponseDTO toResponse(Vehicle e) {
        if (e == null) return null;
        return VehicleResponseDTO.builder()
                .id(e.getId())
                .vehicleCode(e.getVehicleCode())
                .licensePlate(e.getLicensePlate())
                .brand(e.getBrand())
                .model(e.getModel())
                .vehicleType(e.getVehicleType())
                .seats(e.getSeats())
                .transmission(e.getTransmission())
                .fuelType(e.getFuelType())
                .color(e.getColor())
                .year(e.getYear())
                .basePrice(e.getBasePrice())
                .status(e.getStatus())
                .branchId(e.getBranchId())
                .turnaroundMinutes(e.getTurnaroundMinutes())
                .imageUrl(e.getImageUrl())
                .build();
    }

    public static List<VehicleResponseDTO> toResponseList(Collection<Vehicle> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .filter(Objects::nonNull)
                .map(VehicleMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static Vehicle toEntity(VehicleRequestDTO dto) {
        if (dto == null) return null;
        return Vehicle.builder()
                .id(dto.getId())
                .vehicleCode(dto.getVehicleCode())
                .licensePlate(dto.getLicensePlate())
                .brand(dto.getBrand())
                .model(dto.getModel())
                .vehicleType(dto.getVehicleType())
                .seats(dto.getSeats())
                .transmission(dto.getTransmission())
                .fuelType(dto.getFuelType())
                .color(dto.getColor())
                .year(dto.getYear())
                .basePrice(dto.getBasePrice())
                .status(dto.getStatus())
                .branchId(dto.getBranchId())
                .turnaroundMinutes(dto.getTurnaroundMinutes())
                .imageUrl(dto.getImageUrl())
                .build();
    }

    public static List<Vehicle> toEntityList(Collection<VehicleRequestDTO> dtos) {
        if (dtos == null) return List.of();
        return dtos.stream().filter(Objects::nonNull)
                .map(VehicleMapper::toEntity)
                .collect(Collectors.toList());
    }
}
