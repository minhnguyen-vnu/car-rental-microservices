package com.fleetmanagementservice.core.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleRequestDTO {
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
    private Integer turnaroundMinutes;
    private String imageUrl;
    private Long featureMask;
    private Integer offset;
    private Boolean isMeaningful;

    private LocalDateTime pickupTime;
    private LocalDateTime returnTime;

    private Integer page;
    private Integer size;

    private String freeText;
}
