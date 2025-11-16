package com.fleetmanagementservice.core.service.impl;

import com.fleetmanagementservice.core.constant.enums.ErrorCode;
import com.fleetmanagementservice.core.dto.request.BranchRequestDTO;
import com.fleetmanagementservice.core.entity.Branch;
import com.fleetmanagementservice.core.constant.exception.AppException;
import com.fleetmanagementservice.core.service.BranchService;
import com.fleetmanagementservice.infrastructure.repository.BranchRepository;
import com.fleetmanagementservice.kernel.mapper.BranchMapper;
import com.fleetmanagementservice.kernel.utils.DataUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {
    private final BranchRepository branchRepository;

    @Override
    public Branch addBranch(BranchRequestDTO branchRequestDTO) {
        if (DataUtils.isNull(branchRequestDTO)) {
            throw new AppException(ErrorCode.REQ_BODY_NULL);
        } else if (DataUtils.isNull(branchRequestDTO.getCode())
                    || DataUtils.isNull(branchRequestDTO.getName())
                    || DataUtils.isNull(branchRequestDTO.getAddress())) {
            throw new AppException(ErrorCode.REQ_MISSING_FIELD);
        }
        Branch e = BranchMapper.toEntity(branchRequestDTO);
        branchRepository.findByCode(branchRequestDTO.getCode()).ifPresent(v -> {
            throw new AppException(ErrorCode.BRANCH_EXISTED);
        });
        return branchRepository.save(e);
    }

    @Override
    public Branch updateBranch(BranchRequestDTO branchRequestDTO) {
        if (DataUtils.isNull(branchRequestDTO) || DataUtils.isNull(branchRequestDTO.getId())) {
            throw new AppException(ErrorCode.REQ_ID_REQUIRED);
        }
        Branch existed = branchRepository.findById(branchRequestDTO.getId())
                .orElseThrow(() -> new AppException(ErrorCode.BRANCH_NOT_FOUND));

        if (!DataUtils.isBlank(branchRequestDTO.getCode()))    existed.setCode(branchRequestDTO.getCode());
        if (!DataUtils.isBlank(branchRequestDTO.getName()))    existed.setName(branchRequestDTO.getName());
        if (!DataUtils.isBlank(branchRequestDTO.getAddress())) existed.setAddress(branchRequestDTO.getAddress());
        if (DataUtils.nonNull(branchRequestDTO.getLat()))      existed.setLat(branchRequestDTO.getLat());
        if (DataUtils.nonNull(branchRequestDTO.getLng()))      existed.setLng(branchRequestDTO.getLng());

        return branchRepository.save(existed);
    }

    @Override
    public List<Branch> getBranch(BranchRequestDTO branchRequestDTO) {
        if (DataUtils.isNull(branchRequestDTO)) {
            return branchRepository.findAll();
        }

        Specification<Branch> spec = (root, cq, cb) -> {
            List<Predicate> ps = new ArrayList<>();

            if (DataUtils.nonNull(branchRequestDTO.getId())) {
                ps.add(cb.equal(root.get("id"), branchRequestDTO.getId()));
            }
            if (!DataUtils.isBlank(branchRequestDTO.getCode())) {
                ps.add(cb.equal(root.get("code"), branchRequestDTO.getCode()));
            }
            if (!DataUtils.isBlank(branchRequestDTO.getName())) {
                ps.add(cb.equal(root.get("name"), branchRequestDTO.getName()));
            }
            if (!DataUtils.isBlank(branchRequestDTO.getAddress())) {
                ps.add(cb.equal(root.get("address"), branchRequestDTO.getAddress()));
            }

            return ps.isEmpty() ? cb.conjunction() : cb.and(ps.toArray(new Predicate[0]));
        };

        return branchRepository.findAll(spec);
    }

    @Override
    public void removeBranch(BranchRequestDTO branchRequestDTO) {
        if (DataUtils.isNull(branchRequestDTO) || DataUtils.isNull(branchRequestDTO.getId())) {
            throw new AppException(ErrorCode.REQ_ID_REQUIRED);
        }
        if (!branchRepository.existsById(branchRequestDTO.getId())) {
            throw new AppException(ErrorCode.BRANCH_NOT_FOUND);
        }
        branchRepository.deleteById(branchRequestDTO.getId());
    }
}
