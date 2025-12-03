package com.fleetmanagementservice.core.service.impl;

import com.fleetmanagementservice.core.constant.enums.ErrorCode;
import com.fleetmanagementservice.core.constant.exception.AppException;
import com.fleetmanagementservice.core.dto.request.VehicleBlockRequestDTO;
import com.fleetmanagementservice.core.entity.VehicleBlock;
import com.fleetmanagementservice.core.service.VehicleBlockService;
import com.fleetmanagementservice.infrastructure.repository.VehicleBlockRepository;
import com.fleetmanagementservice.kernel.utils.DataUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleBlockServiceImpl implements VehicleBlockService {
    private final VehicleBlockRepository vehicleBlockRepository;

    @Override
    public List<VehicleBlock> getVehicleBlock(VehicleBlockRequestDTO request) {
        if (DataUtils.isNull(request.getVehicleId())) {
            throw new AppException(ErrorCode.REQ_MISSING_FIELD);
        }

        if (DataUtils.isNull(request.getStartTime())
                || DataUtils.isNull(request.getEndTime())) {
            throw new AppException(ErrorCode.REQ_TIME_RANGE_REQUIRED);
        }

        return vehicleBlockRepository.findByVehicleIdAndStartTimeLessThanAndEndTimeGreaterThan(
                request.getVehicleId(), request.getEndTime(), request.getStartTime()
        ).orElse(List.of());
    }

    @Override
    public void saveVehicleBlock(VehicleBlock vehicleBlock) {
        vehicleBlockRepository.save(vehicleBlock);
    }

    @Override
    public void deleteVehicleBlock(VehicleBlock vehicleBlock) {
        vehicleBlockRepository.delete(vehicleBlock);
    }

    @Override
    public boolean checkVehicleBlockOverlap(VehicleBlockRequestDTO request) {
        if (DataUtils.isNull(request.getVehicleId())
                || DataUtils.isNull(request.getStartTime())
                || DataUtils.isNull(request.getEndTime())) {
            throw new AppException(ErrorCode.REQ_MISSING_FIELD);
        }

        return vehicleBlockRepository.existsByVehicleIdAndStartTimeLessThanAndEndTimeGreaterThan(
                request.getVehicleId(), request.getEndTime(), request.getStartTime()
        );
    }

    @Override
    public List<VehicleBlock> getAllVehicleBlock(VehicleBlockRequestDTO request) {
        if (DataUtils.isNull(request.getStartTime())
                || DataUtils.isNull(request.getEndTime())) {
            throw new AppException(ErrorCode.REQ_TIME_RANGE_REQUIRED);
        }

        return vehicleBlockRepository.findByStartTimeLessThanAndEndTimeGreaterThan(
                request.getEndTime(), request.getStartTime()
        ).orElse(List.of());
    }
}
