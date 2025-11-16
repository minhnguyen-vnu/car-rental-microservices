package com.fleetmanagementservice.ui.restful.impl;

import com.fleetmanagementservice.core.constant.response.GeneralResponse;
import com.fleetmanagementservice.core.dto.request.VehicleBlockRequestDTO;
import com.fleetmanagementservice.core.dto.response.VehicleBlockResponseDTO;
import com.fleetmanagementservice.core.service.VehicleBlockService;
import com.fleetmanagementservice.kernel.mapper.VehicleBlockMapper;
import com.fleetmanagementservice.ui.restful.VehicleBlockManagementController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VehicleBlockManagementControllerImpl implements VehicleBlockManagementController {
    private final VehicleBlockService vehicleBlockService;

    @Override
    public GeneralResponse<List<VehicleBlockResponseDTO>> getVehicleBlock(VehicleBlockRequestDTO request) {
        return GeneralResponse.ok(VehicleBlockMapper.toResponseList(vehicleBlockService.getVehicleBlock(request)));
    }
}
