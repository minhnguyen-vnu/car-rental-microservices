package com.rentalservice.core.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleResponseDTO {
    private Integer id;
    private String vehicleCode;
    private String licensePlate;
    private String brand;
    private String model;
    private String vehicleType;
    private Integer seats;
    private String transmission;
    private String fuelType;
    private String color;
    private Integer year;
    private Double basePrice;
    private String status;
    private Integer branchId;
}