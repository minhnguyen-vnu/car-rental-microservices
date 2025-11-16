package com.fleetmanagementservice.core.service;

import com.fleetmanagementservice.core.dto.request.BranchRequestDTO;
import com.fleetmanagementservice.core.entity.Branch;

import java.util.List;

public interface BranchService {
    public Branch addBranch(BranchRequestDTO branchRequestDTO);
    public Branch updateBranch(BranchRequestDTO branchRequestDTO);
    public List<Branch> getBranch(BranchRequestDTO branchRequestDTO);
    public void removeBranch(BranchRequestDTO branchRequestDTO);
}
