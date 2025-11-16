package com.fleetmanagementservice.infrastructure.repository;

import com.fleetmanagementservice.core.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Integer>, JpaSpecificationExecutor<Branch> {
    Optional<Branch> findByCode(String code);
}
