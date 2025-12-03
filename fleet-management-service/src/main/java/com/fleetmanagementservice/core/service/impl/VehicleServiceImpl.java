package com.fleetmanagementservice.core.service.impl;

import com.fleetmanagementservice.core.constant.enums.ErrorCode;
import com.fleetmanagementservice.core.document.VehicleDocument;
import com.fleetmanagementservice.core.dto.request.VehicleBlockRequestDTO;
import com.fleetmanagementservice.core.dto.request.VehicleRequestDTO;
import com.fleetmanagementservice.core.entity.Vehicle;
import com.fleetmanagementservice.core.constant.exception.AppException;
import com.fleetmanagementservice.core.entity.VehicleBlock;
import com.fleetmanagementservice.core.entity.VehicleFeature;
import com.fleetmanagementservice.core.service.AIParserService;
import com.fleetmanagementservice.core.service.VehicleBlockService;
import com.fleetmanagementservice.core.service.VehicleSearchService;
import com.fleetmanagementservice.core.service.VehicleService;
import com.fleetmanagementservice.infrastructure.repository.VehicleFeatureRepository;
import com.fleetmanagementservice.infrastructure.repository.VehicleRepository;
import com.fleetmanagementservice.kernel.mapper.VehicleMapper;
import com.fleetmanagementservice.kernel.utils.DataUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final AIParserService aiParserService;
    private final VehicleBlockService vehicleBlockService;
    private final VehicleFeatureRepository vehicleFeatureRepository;

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
        if (DataUtils.isNull(vehicleRequest.getPickupTime())
            || DataUtils.isNull(vehicleRequest.getReturnTime())) {
            throw new AppException(ErrorCode.REQ_TIME_RANGE_REQUIRED);
        }

        if (vehicleRequest.getReturnTime().isBefore(vehicleRequest.getPickupTime())) {
            throw new AppException(ErrorCode.REQ_INVALID_TIME_RANGE);
        }

        if (!vehicleRequest.isMeaningful()) {
            throw new AppException(ErrorCode.REQ_IS_NOT_MEANINGFUL);
        }

        if (DataUtils.nonNull(vehicleRequest.getId())) {
            return vehicleRepository.findById(vehicleRequest.getId())
                    .filter(vehicle -> !isVehicleBlocked(vehicle, vehicleRequest))
                    .map(List::of)
                    .orElseGet(List::of);
        }

        VehicleRequestDTO extractedRequest = null;
        if (!DataUtils.isBlank(vehicleRequest.getFreeText())) {
            extractedRequest = aiParserService.parseVehicleQuery(vehicleRequest.getFreeText());
        }
        final VehicleRequestDTO finalRequest = mergeRequest(extractedRequest, vehicleRequest);
        Specification<Vehicle> spec = buildSpecification(finalRequest, null);
        List<Vehicle> vehicleList = vehicleRepository.findAll(spec);
        if (DataUtils.isNull(vehicleRequest.getFeatureMask())) {
            vehicleRequest.setFeatureMask(0L);
        }
        List<Vehicle> satisfiedVehicles = vehicleList.stream()
                .filter(vehicle -> hasRequiredFeatures(vehicle.getFeatureMask(), vehicleRequest.getFeatureMask()))
                .filter(vehicle -> !isVehicleBlocked(vehicle, finalRequest))
                .toList();
        satisfiedVehicles = evaluateAndSort(satisfiedVehicles, finalRequest);
        if (vehicleRequest.getOffset() != null) {
            satisfiedVehicles.stream().limit(vehicleRequest.getOffset()).toList();
        }
        return satisfiedVehicles;
    }

    private List<Vehicle> evaluateAndSort(List<Vehicle> vehicles, VehicleRequestDTO request) {
        Map<Integer, Integer> featureCntMap = vehicleFeatureRepository.findAll().stream()
                .collect(Collectors.toMap(VehicleFeature::getId,
                        feature -> feature.getCnt() != null ? feature.getCnt() : 0));

        List<VehicleBlock> blockedVehicles = getAllVehicleBlock(request);

        for (VehicleBlock vehicleBlock : blockedVehicles) {
            Integer vehicleId = vehicleBlock.getVehicleId();
            Vehicle blockedVehicle = vehicleRepository.findById(vehicleId).orElse(null);

            if (blockedVehicle != null && blockedVehicle.getFeatureMask() != null) {
                long featureMask = blockedVehicle.getFeatureMask();

                for (int featureId : featureCntMap.keySet()) {
                    int bitPosition = featureId - 1;
                    if ((featureMask & (1L << bitPosition)) != 0) {
                        featureCntMap.put(featureId, featureCntMap.get(featureId) - 1);
                    }
                }
            }
        }

        return vehicles.stream()
                .map(vehicle -> {
                    double cost = calculateCost(vehicle, featureCntMap);
                    return new VehicleWithCost(vehicle, cost);
                })
                .sorted(Comparator.comparingDouble(VehicleWithCost::getCost))
                .map(VehicleWithCost::getVehicle)
                .toList();
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

    @Override
    public void sync() {
        List<Vehicle> vehicles = vehicleRepository.findAll().stream()
                .filter(v -> v.getFeatureMask() != null)
                .toList();

        List<VehicleFeature> features = vehicleFeatureRepository.findAll();

        features.forEach(feature -> feature.setCnt(0));

        for (Vehicle vehicle : vehicles) {
            long featureMask = vehicle.getFeatureMask();

            for (VehicleFeature feature : features) {
                int featureId = feature.getId();
                int bitPosition = featureId - 1;

                boolean hasFeature = (featureMask & (1L << bitPosition)) != 0;

                if (hasFeature) {
                    feature.setCnt(feature.getCnt() + 1);
                }
            }
        }

        vehicleFeatureRepository.saveAll(features);

    }

    private VehicleRequestDTO mergeRequest(VehicleRequestDTO extractedRequest, VehicleRequestDTO userRequest) {
        if (extractedRequest == null) {
            return userRequest;
        }
        VehicleRequestDTO merged = new VehicleRequestDTO();

        merged.setId(DataUtils.nonNull(userRequest.getId()) ? userRequest.getId() : extractedRequest.getId());
        merged.setVehicleCode(DataUtils.nonNull(userRequest.getVehicleCode()) ? userRequest.getVehicleCode() : extractedRequest.getVehicleCode());
        merged.setLicensePlate(DataUtils.nonNull(userRequest.getLicensePlate()) ? userRequest.getLicensePlate() : extractedRequest.getLicensePlate());
        merged.setBrand(DataUtils.nonNull(userRequest.getBrand()) ? userRequest.getBrand() : extractedRequest.getBrand());
        merged.setModel(DataUtils.nonNull(userRequest.getModel()) ? userRequest.getModel() : extractedRequest.getModel());
        merged.setVehicleType(DataUtils.nonNull(userRequest.getVehicleType()) ? userRequest.getVehicleType() : extractedRequest.getVehicleType());
        merged.setSeats(DataUtils.nonNull(userRequest.getSeats()) ? userRequest.getSeats() : extractedRequest.getSeats());
        merged.setTransmission(DataUtils.nonNull(userRequest.getTransmission()) ? userRequest.getTransmission() : extractedRequest.getTransmission());
        merged.setFuelType(DataUtils.nonNull(userRequest.getFuelType()) ? userRequest.getFuelType() : extractedRequest.getFuelType());
        merged.setColor(DataUtils.nonNull(userRequest.getColor()) ? userRequest.getColor() : extractedRequest.getColor());
        merged.setYear(DataUtils.nonNull(userRequest.getYear()) ? userRequest.getYear() : extractedRequest.getYear());
        merged.setBasePrice(DataUtils.nonNull(userRequest.getBasePrice()) ? userRequest.getBasePrice() : extractedRequest.getBasePrice());
        merged.setStatus(DataUtils.nonNull(userRequest.getStatus()) ? userRequest.getStatus() : extractedRequest.getStatus());
        merged.setBranchId(DataUtils.nonNull(userRequest.getBranchId()) ? userRequest.getBranchId() : extractedRequest.getBranchId());
        merged.setTurnaroundMinutes(DataUtils.nonNull(userRequest.getTurnaroundMinutes()) ? userRequest.getTurnaroundMinutes() : extractedRequest.getTurnaroundMinutes());
        merged.setImageUrl(DataUtils.nonNull(userRequest.getImageUrl()) ? userRequest.getImageUrl() : extractedRequest.getImageUrl());
        merged.setFreeText(DataUtils.nonNull(userRequest.getFreeText()) ? userRequest.getFreeText() : extractedRequest.getFreeText());

        return merged;
    }

    private Specification<Vehicle> buildSpecification(VehicleRequestDTO req, List<Integer> ids) {
        return (root, cq, cb) -> {
            List<Predicate> ps = new ArrayList<>();

            // Nếu có IDs từ Elasticsearch
            if (!DataUtils.isNullOrEmpty(ids)) {
                ps.add(root.get("id").in(ids));
            }

            if (!DataUtils.isBlank(req.getVehicleCode())) {
                ps.add(cb.equal(root.get("vehicleCode"), req.getVehicleCode()));
            }
            if (!DataUtils.isBlank(req.getBrand())) {
                ps.add(cb.equal(root.get("brand"), req.getBrand()));
            }
            if (!DataUtils.isBlank(req.getModel())) {
                ps.add(cb.equal(root.get("model"), req.getModel()));
            }
            if (!DataUtils.isBlank(req.getVehicleType())) {
                ps.add(cb.equal(root.get("vehicleType"), req.getVehicleType()));
            }
            if (DataUtils.nonNull(req.getSeats())) {
                ps.add(cb.equal(root.get("seats"), req.getSeats()));
            }
            if (!DataUtils.isBlank(req.getTransmission())) {
                ps.add(cb.equal(root.get("transmission"), req.getTransmission()));
            }
            if (!DataUtils.isBlank(req.getFuelType())) {
                ps.add(cb.equal(root.get("fuelType"), req.getFuelType()));
            }
            if (!DataUtils.isBlank(req.getColor())) {
                ps.add(cb.equal(root.get("color"), req.getColor()));
            }
            if (DataUtils.nonNull(req.getYear())) {
                ps.add(cb.equal(root.get("year"), req.getYear()));
            }
            if (DataUtils.nonNull(req.getBasePrice())) {
                ps.add(cb.greaterThanOrEqualTo(root.get("basePrice"), req.getBasePrice()));
            }
            if (!DataUtils.isBlank(req.getStatus())) {
                ps.add(cb.equal(root.get("status"), req.getStatus()));
            }
            if (DataUtils.nonNull(req.getBranchId())) {
                ps.add(cb.equal(root.get("branchId"), req.getBranchId()));
            }

            return ps.isEmpty() ? cb.conjunction() : cb.and(ps.toArray(new Predicate[0]));
        };
    }

    private boolean isVehicleBlocked(Vehicle vehicle, VehicleRequestDTO request) {
        VehicleBlockRequestDTO blockCheck = VehicleBlockRequestDTO.builder()
                .id(vehicle.getId())
                .startTime(request.getPickupTime())
                .endTime(request.getReturnTime())
                .build();
        return vehicleBlockService.checkVehicleBlockOverlap(blockCheck);
    }

    private List<VehicleBlock> getAllVehicleBlock(VehicleRequestDTO request) {
        VehicleBlockRequestDTO blockCheck = VehicleBlockRequestDTO.builder()
                .startTime(request.getPickupTime())
                .endTime(request.getReturnTime())
                .build();
        return vehicleBlockService.getAllVehicleBlock(blockCheck);
    }

    private boolean hasRequiredFeatures(Long vehicleMask, Long requiredMask) {
        if (requiredMask == null) return true;
        if (vehicleMask == null) return false;
        return (vehicleMask & requiredMask) == requiredMask;
    }

    private double calculateCost(Vehicle vehicle, Map<Integer, Integer> featureCntMap) {
        if (vehicle.getFeatureMask() == null) {
            return 0.0;
        }

        double totalCost = 0.0;
        long featureMask = vehicle.getFeatureMask();

        // Duyệt qua từng feature của xe
        for (int featureId : featureCntMap.keySet()) {
            int bitPosition = featureId - 1;

            // Check xe có feature này không
            if ((featureMask & (1L << bitPosition)) != 0) {
                int cnt = featureCntMap.get(featureId);
                int denominator = cnt - 1;

                if (denominator == 0) {
                    totalCost += 1e9; // INF
                } else {
                    totalCost += 1.0 / denominator;
                }
            }
        }

        return totalCost;
    }

    // Inner class để giữ vehicle và cost
    @AllArgsConstructor
    @Getter
    private static class VehicleWithCost {
        private Vehicle vehicle;
        private double cost;
    }
}
