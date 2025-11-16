package com.rentalservice.infrastructure.repository;

import com.rentalservice.core.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Integer>, JpaSpecificationExecutor<Rental> {
    Optional<Rental> findByTransactionCode(String transactionCode);

    boolean existsByVehicleIdAndStatusInAndPickupTimeLessThanAndReturnTimeGreaterThan(Integer vehicleId, Collection<String> statuses, LocalDateTime pickupTimeIsLessThan, LocalDateTime returnTimeIsGreaterThan);

    @Query("""
        select r from Rental r
            where r.status in :status
                and (r.pickupTime >= :now or r.returnTime <= :now)
    """)
    List<Rental> findAvailableRental(@Param("status") List<String> status,
                                     @Param("now") LocalDateTime now);

}
