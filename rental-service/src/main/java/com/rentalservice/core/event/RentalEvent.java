package com.rentalservice.core.event;

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
public class RentalEvent {
    private String rentalType;
    private Integer rentalId;
    private Integer userId;
    private Integer vehicleId;
    private Integer paymentId;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
