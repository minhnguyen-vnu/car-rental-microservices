package com.fleetmanagementservice.ui.restful.impl;

import com.fleetmanagementservice.core.dto.request.BranchRequestDTO;
import com.fleetmanagementservice.core.dto.response.BranchResponseDTO;
import com.fleetmanagementservice.core.constant.response.GeneralResponse;
import com.fleetmanagementservice.core.service.BranchService;
import com.fleetmanagementservice.kernel.mapper.BranchMapper;
import com.fleetmanagementservice.ui.restful.BranchManagementController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BranchManagementControllerImpl implements BranchManagementController {
    private final BranchService branchService;

    @Override
    public GeneralResponse<BranchResponseDTO> addBranch(BranchRequestDTO branchRequestDTO) {
        return GeneralResponse.ok(BranchMapper.toResponse(branchService.addBranch(branchRequestDTO)));
    }

    @Override
    public GeneralResponse<BranchResponseDTO> updateBranch(BranchRequestDTO branchRequestDTO) {
        return GeneralResponse.ok(BranchMapper.toResponse(branchService.updateBranch(branchRequestDTO)));
    }

    @Override
    public GeneralResponse<List<BranchResponseDTO>> getBranchDetail(BranchRequestDTO branchRequestDTO) {
        return GeneralResponse.ok(BranchMapper.toResponseList(branchService.getBranch(branchRequestDTO)));
    }

    @Override
    public GeneralResponse<Void> removeBranch(BranchRequestDTO branchRequestDTO) {
        branchService.removeBranch(branchRequestDTO);
        return GeneralResponse.ok(null);
    }
}
