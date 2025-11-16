package com.fleetmanagementservice.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@DynamicInsert
@Entity
@Table(name = "vehicles")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "vehicle_code")
    private String vehicleCode;
    @Column(name = "license_plate")
    private String licensePlate;
    private String brand;
    private String model;
    @Column(name = "vehicle_type")
    private String vehicleType;
    private Integer seats;
    private String transmission;
    @Column(name = "fuel_type")
    private String fuelType;
    private String color;
    private Integer year;
    @Column(name = "base_price")
    private Double basePrice;
    private String status;
    @Column(name = "branch_id")
    private Integer branchId;
    @Column(name = "turnaround_minutes")
    private Integer turnaroundMinutes;
}
