package com.rentalservice.infrastructure.repository.spec;

import com.rentalservice.core.dto.request.RentalRequestDTO;
import com.rentalservice.core.entity.Rental;
import com.rentalservice.kernel.utils.DataUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RentalSpecifications {
    private RentalSpecifications() {}

    public static Specification<Rental> fromRequest(RentalRequestDTO request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (DataUtils.nonNull(request.getUserId())) {
                predicates.add(cb.equal(root.get("userId"), request.getUserId()));
            }
            if (DataUtils.nonNull(request.getVehicleId())) {
                predicates.add(cb.equal(root.get("vehicleId"), request.getVehicleId()));
            }
            if (DataUtils.nonNull(request.getPaymentId())) {
                predicates.add(cb.equal(root.get("paymentId"), request.getPaymentId()));
            }
            if (DataUtils.nonNull(request.getPickupTime())) {
                predicates.add(cb.equal(root.get("pickupTime"), request.getPickupTime()));
            }
            if (DataUtils.nonNull(request.getReturnTime())) {
                predicates.add(cb.equal(root.get("returnTime"), request.getReturnTime()));
            }
            if (DataUtils.nonNull(request.getPickupBranchId())) {
                predicates.add(cb.equal(root.get("pickupBranchId"), request.getPickupBranchId()));
            }
            if (DataUtils.nonNull(request.getReturnBranchId())) {
                predicates.add(cb.equal(root.get("returnBranchId"), request.getReturnBranchId()));
            }
            if (DataUtils.nonNull(request.getDurationDays())) {
                predicates.add(cb.equal(root.get("durationDays"), request.getDurationDays()));
            }
            if (DataUtils.nonNull(request.getTotalAmount())) {
                predicates.add(cb.equal(root.get("totalAmount"), request.getTotalAmount()));
            }
            if (!DataUtils.isBlank(request.getCurrency())) {
                predicates.add(cb.equal(root.get("currency"), request.getCurrency()));
            }
            if (!DataUtils.isBlank(request.getStatus())) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
