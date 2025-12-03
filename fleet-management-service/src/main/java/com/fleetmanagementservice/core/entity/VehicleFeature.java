package com.fleetmanagementservice.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@DynamicInsert
@Entity
@Table(name = "vehicle_features")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "feature_code")
    private String featureCode;

    @Column(name = "feature_name")
    private String featureName ;

    @Column(name = "feature_category")
    private String featureCategory;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "cnt")
    private Integer cnt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
