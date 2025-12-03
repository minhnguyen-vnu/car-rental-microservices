package com.fleetmanagementservice.infrastructure.repository;

import com.fleetmanagementservice.core.entity.VehicleFeature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleFeatureRepository extends JpaRepository<VehicleFeature, Integer> {
}
