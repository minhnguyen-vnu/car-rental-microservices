package com.fleetmanagementservice.kernel.mapper;

import com.fleetmanagementservice.core.dto.request.BranchRequestDTO;
import com.fleetmanagementservice.core.dto.response.BranchResponseDTO;
import com.fleetmanagementservice.core.entity.Branch;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class BranchMapper {
    private BranchMapper() {}

    public static BranchResponseDTO toResponse(Branch e) {
        if (e == null) {
            return null;
        }

        return BranchResponseDTO.builder()
                .id(e.getId())
                .code(e.getCode())
                .name(e.getName())
                .address(e.getAddress())
                .lat(e.getLat())
                .lng(e.getLng())
                .build();
    }

    public static List<BranchResponseDTO> toResponseList(Collection<Branch> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .map(BranchMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static Branch toEntity(BranchRequestDTO dto) {
        if (dto == null) return null;
        return Branch.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .address(dto.getAddress())
                .lat(dto.getLat())
                .lng(dto.getLng())
                .build();
    }

    public static List<Branch> toEntityList(Collection<BranchRequestDTO> dtos) {
        if (dtos == null) return List.of();
        return dtos.stream().filter(Objects::nonNull)
                .map(BranchMapper::toEntity)
                .collect(Collectors.toList());
    }

}
