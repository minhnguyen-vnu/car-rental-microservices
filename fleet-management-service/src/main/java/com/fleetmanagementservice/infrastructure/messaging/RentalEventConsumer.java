package com.fleetmanagementservice.infrastructure.messaging;

import com.fleetmanagementservice.core.constant.enums.ErrorCode;
import com.fleetmanagementservice.core.constant.enums.RentalEventType;
import com.fleetmanagementservice.core.constant.enums.VehicleBlockType;
import com.fleetmanagementservice.core.constant.exception.AppException;
import com.fleetmanagementservice.core.dto.request.VehicleBlockRequestDTO;
import com.fleetmanagementservice.core.entity.VehicleBlock;
import com.fleetmanagementservice.core.event.RentalEvent;
import com.fleetmanagementservice.core.service.VehicleBlockService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentalEventConsumer {
    private final VehicleBlockService vehicleBlockService;

    @KafkaListener(topics = "rental-event", groupId = "fleet-service")
    public void onMessage(ConsumerRecord<String, RentalEvent> rec, Acknowledgment ack) {
        try {
            RentalEvent evt = rec.value();
            VehicleBlock vehicleBlock = VehicleBlock.builder()
                    .vehicleId(evt.getVehicleId())
                    .rentalId(evt.getRentalId())
                    .type(VehicleBlockType.TURNAROUND.name())
                    .startTime(evt.getEndTime())
                    .endTime(evt.getEndTime().plusHours(2))
                    .build();

            if (evt.getRentalType().equals(RentalEventType.RENTAL_COMPLETED.name())
                    || evt.getRentalType().equals(RentalEventType.RENTAL_CANCELLED.name())) {
                VehicleBlock currentVehicleBlock = vehicleBlockService.getVehicleBlock(VehicleBlockRequestDTO.builder()
                                .rentalId(evt.getRentalId())
                                .build()).getFirst();

                vehicleBlockService.deleteVehicleBlock(currentVehicleBlock);
            }

            if (!evt.getRentalType().equals(RentalEventType.RENTAL_CANCELLED.name())) {
                vehicleBlockService.saveVehicleBlock(vehicleBlock);
            }

            ack.acknowledge();
        } catch (Exception ex) {
            throw new AppException(ErrorCode.KAFKA_UNEXPECTED);
        }
    }
}
