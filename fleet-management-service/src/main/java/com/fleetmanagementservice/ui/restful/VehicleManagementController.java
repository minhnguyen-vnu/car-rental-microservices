package com.fleetmanagementservice.ui.restful;

import com.fleetmanagementservice.core.dto.request.VehicleRequestDTO;
import com.fleetmanagementservice.core.dto.response.VehicleResponseDTO;
import com.fleetmanagementservice.core.constant.response.GeneralResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/vehicle")
public interface VehicleManagementController {
    @PostMapping("/add")
    public GeneralResponse<VehicleResponseDTO> addVehicle(@RequestBody VehicleRequestDTO addVehicleRequestDTO);
    @PutMapping("/update")
    public GeneralResponse<VehicleResponseDTO> updateVehicle(@RequestBody VehicleRequestDTO updateVehicleRequestDTO);
    @PostMapping("/get")
    public GeneralResponse<List<VehicleResponseDTO>> getVehicleDetail(@RequestBody VehicleRequestDTO vehicleDetailRequestDTO);
    @DeleteMapping("/remove")
    public GeneralResponse<Void> removeVehicle(@RequestBody VehicleRequestDTO removeVehicleRequestDTO);
    @PostMapping("/reindex")
    public GeneralResponse<String> reindexAll();
    @PostMapping("/sync")
    public GeneralResponse<String> sync();
}
