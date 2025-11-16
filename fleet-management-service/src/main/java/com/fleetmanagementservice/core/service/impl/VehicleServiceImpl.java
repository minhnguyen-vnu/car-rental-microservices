package com.fleetmanagementservice.core.service.impl;

import com.fleetmanagementservice.core.constant.enums.ErrorCode;
import com.fleetmanagementservice.core.dto.request.VehicleRequestDTO;
import com.fleetmanagementservice.core.entity.Vehicle;
import com.fleetmanagementservice.core.constant.exception.AppException;
import com.fleetmanagementservice.core.service.VehicleService;
import com.fleetmanagementservice.infrastructure.repository.VehicleRepository;
import com.fleetmanagementservice.kernel.mapper.VehicleMapper;
import com.fleetmanagementservice.kernel.utils.DataUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;

    @Override
    public Vehicle addVehicle(VehicleRequestDTO vehicleRequest) {
        if (DataUtils.isNull(vehicleRequest)) {
            throw new AppException(ErrorCode.REQ_BODY_NULL);
        } else if (DataUtils.isNull(vehicleRequest.getVehicleCode())
                    || DataUtils.isNull(vehicleRequest.getLicensePlate())
                    || DataUtils.isNull(vehicleRequest.getBrand())
                    || DataUtils.isNull(vehicleRequest.getModel())
                    || DataUtils.isNull(vehicleRequest.getSeats())
                    || DataUtils.isNull(vehicleRequest.getBasePrice())) {
            throw new AppException(ErrorCode.REQ_MISSING_FIELD);
        }
        Vehicle entity = VehicleMapper.toEntity(vehicleRequest);

        Optional<Vehicle> existed = vehicleRepository.findByVehicleCode(vehicleRequest.getVehicleCode());
        if (existed.isPresent()) {
            throw new AppException(ErrorCode.VEHICLE_EXISTED);
        }
        return vehicleRepository.save(entity);
    }

    @Override
    public Vehicle updateVehicle(VehicleRequestDTO vehicleRequest) {
        if (vehicleRequest == null || vehicleRequest.getId() == null) {
            throw new AppException(ErrorCode.REQ_ID_REQUIRED);
        }
        Vehicle existed = vehicleRepository.findById(vehicleRequest.getId())
                .orElseThrow(() -> new AppException(ErrorCode.VEHICLE_NOT_FOUND));

        if (!DataUtils.isBlank(vehicleRequest.getVehicleCode())) existed.setVehicleCode(vehicleRequest.getVehicleCode());
        if (!DataUtils.isBlank(vehicleRequest.getLicensePlate())) existed.setLicensePlate(vehicleRequest.getLicensePlate());
        if (!DataUtils.isBlank(vehicleRequest.getBrand())) existed.setBrand(vehicleRequest.getBrand());
        if (!DataUtils.isBlank(vehicleRequest.getModel())) existed.setModel(vehicleRequest.getModel());
        if (!DataUtils.isBlank(vehicleRequest.getVehicleType())) existed.setVehicleType(vehicleRequest.getVehicleType());
        if (DataUtils.nonNull(vehicleRequest.getSeats())) existed.setSeats(vehicleRequest.getSeats());
        if (!DataUtils.isBlank(vehicleRequest.getTransmission())) existed.setTransmission(vehicleRequest.getTransmission());
        if (!DataUtils.isBlank(vehicleRequest.getFuelType())) existed.setFuelType(vehicleRequest.getFuelType());
        if (!DataUtils.isBlank(vehicleRequest.getColor())) existed.setColor(vehicleRequest.getColor());
        if (DataUtils.nonNull(vehicleRequest.getYear())) existed.setYear(vehicleRequest.getYear());
        if (DataUtils.nonNull(vehicleRequest.getBasePrice())) existed.setBasePrice(vehicleRequest.getBasePrice());
        if (!DataUtils.isBlank(vehicleRequest.getStatus())) existed.setStatus(vehicleRequest.getStatus());
        if (DataUtils.nonNull(vehicleRequest.getBranchId())) existed.setBranchId(vehicleRequest.getBranchId());
        if (DataUtils.nonNull(vehicleRequest.getTurnaroundMinutes())) existed.setTurnaroundMinutes(vehicleRequest.getTurnaroundMinutes());

        return vehicleRepository.save(existed);
    }

    @Override
    public List<Vehicle> getVehicle(VehicleRequestDTO vehicleRequest) {
        if (DataUtils.isNull(vehicleRequest)) {
            return vehicleRepository.findAll();
        }

        if (DataUtils.nonNull(vehicleRequest.getId())) {
            return vehicleRepository.findById(vehicleRequest.getId())
                    .map(List::of)
                    .orElseGet(List::of);
        }

        Specification<Vehicle> spec = (root, cq, cb) -> {
            List<Predicate> ps = new ArrayList<>();

            if (!DataUtils.isBlank(vehicleRequest.getVehicleCode())) {
                ps.add(cb.equal(root.get("vehicleCode"), vehicleRequest.getVehicleCode()));
            }
            if (!DataUtils.isBlank(vehicleRequest.getBrand())) {
                ps.add(cb.equal(root.get("brand"), vehicleRequest.getBrand()));
            }
            if (!DataUtils.isBlank(vehicleRequest.getModel())) {
                ps.add(cb.equal(root.get("model"), vehicleRequest.getModel()));
            }
            if (!DataUtils.isBlank(vehicleRequest.getVehicleType())) {
                ps.add(cb.equal(root.get("vehicleType"), vehicleRequest.getVehicleType()));
            }
            if (DataUtils.nonNull(vehicleRequest.getSeats())) {
                ps.add(cb.equal(root.get("seats"), vehicleRequest.getSeats()));
            }
            if (!DataUtils.isBlank(vehicleRequest.getTransmission())) {
                ps.add(cb.equal(root.get("transmission"), vehicleRequest.getTransmission()));
            }
            if (!DataUtils.isBlank(vehicleRequest.getFuelType())) {
                ps.add(cb.equal(root.get("fuelType"), vehicleRequest.getFuelType()));
            }
            if (!DataUtils.isBlank(vehicleRequest.getColor())) {
                ps.add(cb.equal(root.get("color"), vehicleRequest.getColor()));
            }
            if (DataUtils.nonNull(vehicleRequest.getYear())) {
                ps.add(cb.equal(root.get("year"), vehicleRequest.getYear()));
            }
            if (DataUtils.nonNull(vehicleRequest.getBasePrice())) {
                ps.add(cb.greaterThanOrEqualTo(root.get("basePrice"), vehicleRequest.getBasePrice()));
            }
            if (!DataUtils.isBlank(vehicleRequest.getStatus())) {
                ps.add(cb.equal(root.get("status"), vehicleRequest.getStatus()));
            }
            if (DataUtils.nonNull(vehicleRequest.getBranchId())) {
                ps.add(cb.equal(root.get("branchId"), vehicleRequest.getBranchId()));
            }

            return ps.isEmpty() ? cb.conjunction() : cb.and(ps.toArray(new Predicate[0]));
        };

        return vehicleRepository.findAll(spec);
    }

    @Override
    public void removeVehicle(VehicleRequestDTO vehicleRequest) {
        if (DataUtils.isNull(vehicleRequest) || DataUtils.isNull(vehicleRequest.getId())) {
            throw new AppException(ErrorCode.REQ_ID_REQUIRED);
        }
        if (!vehicleRepository.existsById(vehicleRequest.getId())) {
            throw new AppException(ErrorCode.VEHICLE_NOT_FOUND);
        }
        vehicleRepository.deleteById(vehicleRequest.getId());
    }
}
