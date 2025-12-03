package com.fleetmanagementservice.core.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "vehicles")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleDocument {
    @Id
    private Integer id;

    @Field(type = FieldType.Keyword)
    private String vehicleCode;

    @Field(type = FieldType.Keyword)
    private String licensePlate;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String brand;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String model;

    @Field(type = FieldType.Keyword)
    private String vehicleType;

    @Field(type = FieldType.Integer)
    private Integer seats;

    @Field(type = FieldType.Keyword)
    private String transmission;

    @Field(type = FieldType.Keyword)
    private String fuelType;

    @Field(type = FieldType.Keyword)
    private String color;

    @Field(type = FieldType.Integer)
    private Integer year;

    @Field(type = FieldType.Double)
    private Double basePrice;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Integer)
    private Integer branchId;

    @Field(type = FieldType.Integer)
    private Integer turnaroundMinutes;

    @Field(type = FieldType.Keyword)
    private String imageUrl;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime updatedAt;
}