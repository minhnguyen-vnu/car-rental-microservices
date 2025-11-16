package com.rentalservice.core.dto.request;

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
public class RentalCreateRequestDTO {
    private Integer vehicleId;
    private LocalDateTime pickupTime;
    private LocalDateTime returnTime;
    private Integer pickupBranchId;
    private Integer returnBranchId;
    private Double durationDays;
    private Double totalAmount;
}
