package com.fleetmanagementservice.core.service;

import com.fleetmanagementservice.core.dto.request.VehicleRequestDTO;
import com.fleetmanagementservice.core.entity.Vehicle;

import java.util.List;

public interface VehicleService {
    public Vehicle addVehicle(VehicleRequestDTO vehicleRequest);
    public Vehicle updateVehicle(VehicleRequestDTO vehicleRequest);
    public List<Vehicle> getVehicle(VehicleRequestDTO vehicleRequest);
    public void removeVehicle(VehicleRequestDTO vehicleRequest);
    void sync();
}
