package com.fleetmanagementservice.core.service;

import com.fleetmanagementservice.core.dto.request.VehicleBlockRequestDTO;
import com.fleetmanagementservice.core.dto.response.VehicleBlockResponseDTO;
import com.fleetmanagementservice.core.entity.VehicleBlock;

import java.util.List;

public interface VehicleBlockService {
    public List<VehicleBlock> getVehicleBlock(VehicleBlockRequestDTO request);
    public void saveVehicleBlock(VehicleBlock vehicleBlock);
    public void deleteVehicleBlock(VehicleBlock vehicleBlock);
}
