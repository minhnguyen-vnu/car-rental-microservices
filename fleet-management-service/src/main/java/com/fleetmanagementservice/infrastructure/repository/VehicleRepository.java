package com.fleetmanagementservice.infrastructure.repository;

import com.fleetmanagementservice.core.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer>, JpaSpecificationExecutor<Vehicle> {
    Optional<Vehicle> findByVehicleCode(String vehicleCode);
}
