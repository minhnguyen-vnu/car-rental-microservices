package com.fleetmanagementservice.infrastructure.repository;

import com.fleetmanagementservice.core.document.VehicleDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface VehicleSearchRepository extends ElasticsearchRepository<VehicleDocument, Integer> {
    List<VehicleDocument> findByBrandContainingOrModelContainingOrVehicleTypeContaining(
            String brand, String model, String vehicleType
    );
}
