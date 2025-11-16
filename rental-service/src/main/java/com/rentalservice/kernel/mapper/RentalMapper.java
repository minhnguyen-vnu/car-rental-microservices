package com.rentalservice.kernel.mapper;

import com.rentalservice.core.dto.request.RentalCreateRequestDTO;
import com.rentalservice.core.dto.request.RentalRequestDTO;
import com.rentalservice.core.dto.response.RentalResponseDTO;
import com.rentalservice.core.entity.Rental;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class RentalMapper {
    private RentalMapper() {}

    public static Rental toEntity(RentalCreateRequestDTO req, Integer userId) {
        return Rental.builder()
                .transactionCode(UUID.randomUUID().toString())
                .userId(userId)
                .vehicleId(req.getVehicleId())
                .paymentId(1)
                .pickupTime(req.getPickupTime())
                .returnTime(req.getReturnTime())
                .pickupBranchId(req.getPickupBranchId())
                .returnBranchId(req.getReturnBranchId())
                .durationDays(req.getDurationDays())
                .totalAmount(req.getTotalAmount())
                .build();
    }

    public static Rental toEntity(RentalRequestDTO req) {
        return Rental.builder()
                .id(req.getId())
                .transactionCode(req.getTransactionCode())
                .userId(req.getUserId())
                .vehicleId(req.getVehicleId())
                .paymentId(req.getPaymentId())
                .pickupTime(req.getPickupTime())
                .returnTime(req.getReturnTime())
                .pickupBranchId(req.getPickupBranchId())
                .returnBranchId(req.getReturnBranchId())
                .durationDays(req.getDurationDays())
                .totalAmount(req.getTotalAmount())
                .currency(req.getCurrency())
                .status(req.getStatus())
                .build();
    }


    public static RentalResponseDTO toResponse(Rental e) {
        if (e == null) return null;
        return RentalResponseDTO.builder()
                .id(e.getId())
                .transactionCode(e.getTransactionCode())
                .userId(e.getUserId())
                .vehicleId(e.getVehicleId())
                .paymentId(e.getPaymentId())
                .pickupTime(e.getPickupTime())
                .returnTime(e.getReturnTime())
                .pickupBranchId(e.getPickupBranchId())
                .returnBranchId(e.getReturnBranchId())
                .durationDays(e.getDurationDays())
                .totalAmount(e.getTotalAmount())
                .currency(e.getCurrency())
                .status(e.getStatus())
                .build();
    }

    public static List<RentalResponseDTO> toResponseList(Collection<Rental> rentals) {
        if (rentals == null) return null;
        return rentals.stream()
                .filter(Objects::nonNull)
                .map(RentalMapper::toResponse)
                .collect(Collectors.toList());
    }

}
