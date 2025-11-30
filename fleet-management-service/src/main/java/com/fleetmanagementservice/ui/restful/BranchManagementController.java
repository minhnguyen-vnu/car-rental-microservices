package com.fleetmanagementservice.ui.restful;

import com.fleetmanagementservice.core.dto.request.BranchRequestDTO;
import com.fleetmanagementservice.core.dto.response.BranchResponseDTO;
import com.fleetmanagementservice.core.constant.response.GeneralResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/branch")
public interface BranchManagementController {
    @PostMapping("/add")
    public GeneralResponse<BranchResponseDTO> addBranch(@RequestBody BranchRequestDTO branchRequestDTO);
    @PutMapping("/update")
    public GeneralResponse<BranchResponseDTO> updateBranch(@RequestBody BranchRequestDTO branchRequestDTO);
    @PostMapping("/get")
    public GeneralResponse<List<BranchResponseDTO>> getBranchDetail(@RequestBody BranchRequestDTO branchRequestDTO);
    @DeleteMapping("/remove")
    public GeneralResponse<Void> removeBranch(@RequestBody BranchRequestDTO branchRequestDTO);
}
