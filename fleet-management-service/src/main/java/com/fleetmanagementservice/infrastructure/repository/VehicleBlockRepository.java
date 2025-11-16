package com.fleetmanagementservice.infrastructure.repository;

import com.fleetmanagementservice.core.entity.VehicleBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VehicleBlockRepository extends JpaRepository<VehicleBlock, Integer> {
    Optional<List<VehicleBlock>> findByVehicleIdAndStartTimeLessThanAndEndTimeGreaterThan(Integer vehicleId, LocalDateTime startTimeIsLessThan, LocalDateTime endTimeIsGreaterThan);
    Optional<VehicleBlock> findByRentalId(Integer rentalId);
}
