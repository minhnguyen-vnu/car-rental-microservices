package com.fleetmanagementservice.ui.restful;

import com.fleetmanagementservice.core.constant.response.GeneralResponse;
import com.fleetmanagementservice.core.dto.request.VehicleBlockRequestDTO;
import com.fleetmanagementservice.core.dto.response.VehicleBlockResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/vehicle-blocks")
public interface VehicleBlockManagementController {
    @PostMapping("/get")
    public GeneralResponse<List<VehicleBlockResponseDTO>> getVehicleBlock(@RequestBody VehicleBlockRequestDTO request);
}
