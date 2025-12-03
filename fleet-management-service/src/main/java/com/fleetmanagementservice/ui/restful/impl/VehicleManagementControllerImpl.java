package com.fleetmanagementservice.ui.restful.impl;

import com.fleetmanagementservice.core.dto.request.VehicleRequestDTO;
import com.fleetmanagementservice.core.dto.response.VehicleResponseDTO;
import com.fleetmanagementservice.core.constant.response.GeneralResponse;
import com.fleetmanagementservice.core.service.VehicleSearchService;
import com.fleetmanagementservice.core.service.VehicleService;
import com.fleetmanagementservice.kernel.mapper.VehicleMapper;
import com.fleetmanagementservice.ui.restful.VehicleManagementController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VehicleManagementControllerImpl implements VehicleManagementController {
    private final VehicleService vehicleService;
    private final VehicleSearchService vehicleSearchService;

    @Override
    public GeneralResponse<VehicleResponseDTO> addVehicle(VehicleRequestDTO addVehicleRequestDTO) {
        return GeneralResponse.ok(VehicleMapper.toResponse(vehicleService.addVehicle(addVehicleRequestDTO)));
    }

    @Override
    public GeneralResponse<VehicleResponseDTO> updateVehicle(VehicleRequestDTO updateVehicleRequestDTO) {
        return GeneralResponse.ok(VehicleMapper.toResponse(vehicleService.updateVehicle(updateVehicleRequestDTO)));
    }

    @Override
    public GeneralResponse<List<VehicleResponseDTO>> getVehicleDetail(VehicleRequestDTO vehicleDetailRequestDTO) {
        return GeneralResponse.ok(VehicleMapper.toResponseList(vehicleService.getVehicle(vehicleDetailRequestDTO)));
    }

    @Override
    public GeneralResponse<Void> removeVehicle(VehicleRequestDTO removeVehicleRequestDTO) {
        vehicleService.removeVehicle(removeVehicleRequestDTO);
        return GeneralResponse.ok(null);
    }

    @Override
    public GeneralResponse<String> reindexAll() {
        vehicleSearchService.reindex();
        return GeneralResponse.ok("Reindex completed");
    }

    @Override
    public GeneralResponse<String> sync() {
        vehicleService.sync();
        return GeneralResponse.ok("Sync completed");
    }
}
