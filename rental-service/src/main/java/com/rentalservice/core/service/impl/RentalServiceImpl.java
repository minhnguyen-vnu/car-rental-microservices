package com.rentalservice.core.service.impl;

import com.rentalservice.core.constant.enums.ErrorCode;
import com.rentalservice.core.constant.State;
import com.rentalservice.core.constant.enums.RentalEventType;
import com.rentalservice.core.constant.enums.RentalStatus;
import com.rentalservice.core.constant.exception.AppException;
import com.rentalservice.core.context.LocalContextHolder;
import com.rentalservice.core.dto.request.RentalCreateRequestDTO;
import com.rentalservice.core.dto.request.RentalRequestDTO;
import com.rentalservice.core.dto.response.VehicleBlockResponseDTO;
import com.rentalservice.core.dto.response.VehicleResponseDTO;
import com.rentalservice.core.entity.Rental;
import com.rentalservice.core.event.RentalEvent;
import com.rentalservice.core.service.RentalService;
import com.rentalservice.infrastructure.adapter.FleetServiceAdapter;
import com.rentalservice.infrastructure.messaging.producer.RentalEventProducer;
import com.rentalservice.infrastructure.repository.RentalRepository;
import com.rentalservice.infrastructure.repository.spec.RentalSpecifications;
import com.rentalservice.kernel.mapper.FleetMapper;
import com.rentalservice.kernel.mapper.RentalMapper;
import com.rentalservice.kernel.mapper.VehicleBlockMapper;
import com.rentalservice.kernel.utils.DataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final FleetServiceAdapter fleetServiceAdapter;
    private final RentalEventProducer rentalEventProducer;
    private final RedissonClient redisson;


    @Override
    public Rental createRental(RentalCreateRequestDTO request) {
        log.info("start processing a rental request {}", request);

        Integer userId = LocalContextHolder.get().getUserId();

        if (DataUtils.isNull(request)) {
            throw new AppException(ErrorCode.REQ_BODY_NULL);
        }

        if (DataUtils.isNull(request.getVehicleId())
                || DataUtils.isNull(request.getPickupTime())
                || DataUtils.isNull(request.getReturnTime())
                || DataUtils.isNull(request.getPickupBranchId())
                || DataUtils.isNull(request.getReturnBranchId())
                || DataUtils.isNull(request.getDurationDays())
                || DataUtils.isNull(request.getTotalAmount())) {
            throw new AppException(ErrorCode.REQ_MISSING_FIELD);
        }

        if (request.getReturnTime().isBefore(request.getPickupTime())) {
            throw new AppException(ErrorCode.REQ_INVALID_TIME_RANGE);
        }

        if (!DataUtils.isValidTimeRange(request.getPickupTime(), request.getReturnTime(), request.getDurationDays())
                || Double.compare(request.getDurationDays(), 0.0) <= 0) {
            throw new AppException(ErrorCode.REQ_INVALID_DURATION);
        }
        if (Double.compare(request.getTotalAmount(), 0.0) < 0) {
            throw new AppException(ErrorCode.REQ_INVALID_AMOUNT);
        }

        log.info("Sending request to fleet service");
        List<VehicleResponseDTO> vehicleResponseList = fleetServiceAdapter.getVehicleDetails(FleetMapper.toRequest(request.getVehicleId()));
        log.info("Fleet Service response {}", vehicleResponseList);
        List<VehicleBlockResponseDTO> vehicleBlockResponseList = fleetServiceAdapter.getVehicleBlocks(VehicleBlockMapper.toRequest(request.getVehicleId(), request.getPickupTime(), request.getReturnTime()));
        boolean overlap = rentalRepository.existsByVehicleIdAndStatusInAndPickupTimeLessThanAndReturnTimeGreaterThan(
                request.getVehicleId(), List.of(RentalStatus.HOLD.name(), RentalStatus.RESERVED.name(), RentalStatus.RENTING.name()), request.getReturnTime(), request.getPickupTime());
        if (DataUtils.isNullOrEmpty(vehicleResponseList)) {
            throw new AppException(ErrorCode.VEHICLE_NOT_FOUND);
        } else if (Double.compare(vehicleResponseList.getFirst().getBasePrice() * request.getDurationDays(),
                request.getTotalAmount()) != 0) {
            throw new AppException(ErrorCode.REQ_INVALID_AMOUNT);
        }else if (!vehicleResponseList.getFirst().getStatus().equals(State.Fleet.AVAILABLE)) {
            throw new AppException(ErrorCode.VEHICLE_NOT_AVAILABLE);
        } else if (overlap) {
            throw new AppException(ErrorCode.VEHICLE_OVERLAP_WITH_EXISTED_RENTAL);
        } else if (!DataUtils.isNullOrEmpty(vehicleBlockResponseList)) {
            throw new AppException(ErrorCode.VEHICLE_OVERLAP_WITH_VEHICLE_BLOCKS);
        }

        String lockKey = "lock:rental:vehicle:" + request.getVehicleId();
        RLock lock = redisson.getLock(lockKey);
        boolean locked = false;

        try {
            locked = lock.tryLock(0, 15, TimeUnit.SECONDS);
            if (!locked) {
                throw new AppException(ErrorCode.REDIS_CONFLICT);
            }

            Rental entity = RentalMapper.toEntity(request, userId);
            entity = rentalRepository.save(entity);

            rentalEventProducer.sendRentalEvent(
                    RentalEvent.builder()
                            .rentalType(RentalEventType.RENTAL_CREATED.name())
                            .rentalId(entity.getId())
                            .userId(entity.getUserId())
                            .vehicleId(entity.getVehicleId())
                            .paymentId(entity.getPaymentId())
                            .status(RentalStatus.HOLD.name())
                            .startTime(request.getPickupTime())
                            .endTime(request.getReturnTime())
                            .build()
            );
            return entity;
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new AppException(ErrorCode.SYS_UNEXPECTED);
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public Rental updateRental(RentalRequestDTO request) {
        if (DataUtils.isNull(request)) {
            throw new AppException(ErrorCode.REQ_BODY_NULL);
        }

        Rental target;
        if (DataUtils.nonNull(request.getId())) {
            target = rentalRepository.findById(request.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.RENTAL_NOT_FOUND));
        } else if (!DataUtils.isBlank(request.getTransactionCode())) {
            target = rentalRepository.findByTransactionCode(request.getTransactionCode())
                    .orElseThrow(() -> new AppException(ErrorCode.RENTAL_NOT_FOUND));
        } else {
            throw new AppException(ErrorCode.RENTAL_IDENTIFIER_REQUIRED);
        }

        if (DataUtils.nonNull(request.getUserId())) target.setUserId(request.getUserId());
        if (DataUtils.nonNull(request.getVehicleId())) target.setVehicleId(request.getVehicleId());
        if (DataUtils.nonNull(request.getPickupTime())) target.setPickupTime(request.getPickupTime());
        if (DataUtils.nonNull(request.getReturnTime())) target.setReturnTime(request.getReturnTime());
        if (DataUtils.nonNull(request.getPickupBranchId())) target.setPickupBranchId(request.getPickupBranchId());
        if (DataUtils.nonNull(request.getReturnBranchId())) target.setReturnBranchId(request.getReturnBranchId());
        if (DataUtils.nonNull(request.getDurationDays())) target.setDurationDays(request.getDurationDays());
        if (DataUtils.nonNull(request.getTotalAmount())) target.setTotalAmount(request.getTotalAmount());
        if (!DataUtils.isBlank(request.getCurrency())) target.setCurrency(request.getCurrency());
        if (!DataUtils.isBlank(request.getStatus())) target.setStatus(request.getStatus());

        LocalDateTime p = target.getPickupTime();
        LocalDateTime r = target.getReturnTime();
        if (r.isBefore(p)) {
            throw new AppException(ErrorCode.REQ_INVALID_TIME_RANGE);
        }
        if (Double.compare(target.getDurationDays(), 0.0) <= 0
            || !DataUtils.isValidTimeRange(p, r, target.getDurationDays())) {
            throw new AppException(ErrorCode.REQ_INVALID_DURATION);
        }
        if (Double.compare(target.getTotalAmount(), 0.0) < 0) {
            throw new AppException(ErrorCode.REQ_INVALID_AMOUNT);
        }

        if (RentalStatus.COMPLETED.name().equals(target.getStatus())) {
            rentalEventProducer.sendRentalEvent(
                    RentalEvent.builder()
                            .rentalType(RentalEventType.RENTAL_COMPLETED.name())
                            .userId(target.getUserId())
                            .vehicleId(target.getVehicleId())
                            .paymentId(target.getPaymentId())
                            .status(target.getStatus())
                            .startTime(target.getPickupTime())
                            .endTime(target.getReturnTime())
                            .build()
            );
        } else if (RentalStatus.CANCELLED.name().equals(target.getStatus())) {
            rentalEventProducer.sendRentalEvent(
                    RentalEvent.builder()
                            .rentalType(RentalEventType.RENTAL_CANCELLED.name())
                            .userId(target.getUserId())
                            .vehicleId(target.getVehicleId())
                            .paymentId(target.getPaymentId())
                            .status(target.getStatus())
                            .startTime(target.getPickupTime())
                            .endTime(target.getReturnTime())
                            .build()
            );
        }
        return rentalRepository.save(target);
    }

    @Override
    public List<Rental> getRentals(RentalRequestDTO request) {
        if (DataUtils.isNull(request)) {
            return rentalRepository.findAll();
        }

        if (DataUtils.nonNull(request.getId())) {
            return rentalRepository.findById(request.getId())
                    .map(List::of)
                    .orElseGet(List::of);
        }
        if (!DataUtils.isBlank(request.getTransactionCode())) {
            return rentalRepository.findByTransactionCode(request.getTransactionCode())
                    .map(List::of)
                    .orElseGet(List::of);
        }

        Specification<Rental> spec = RentalSpecifications.fromRequest(request);
        return rentalRepository.findAll(spec);
    }

    @Override
    public void sync() {
        List<Rental> rentalList = rentalRepository.findAvailableRental(List.of(RentalStatus.RENTING.name(),
                RentalStatus.RESERVED.name()), LocalDateTime.now());

        for (Rental rental : rentalList) {
            RentalRequestDTO request;
            if (rental.getPickupTime().isAfter(LocalDateTime.now())) {
                request = RentalRequestDTO.builder()
                        .id(rental.getId())
                        .status(RentalStatus.RENTING.name())
                        .build();
            } else {
                request = RentalRequestDTO.builder()
                        .id(rental.getId())
                        .status(RentalStatus.COMPLETED.name())
                        .build();
            }
            Rental updatedRental = updateRental(request);
            log.info("Change Rental id {} from status {} to status {}", rental.getId(),
                    rental.getStatus(),
                    updatedRental.getStatus());
        }
    }
}
