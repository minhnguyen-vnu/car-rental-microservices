package com.rentalservice.infrastructure.messaging.consumer;

import com.rentalservice.core.constant.enums.ErrorCode;
import com.rentalservice.core.constant.enums.PaymentResult;
import com.rentalservice.core.constant.enums.RentalStatus;
import com.rentalservice.core.constant.exception.AppException;
import com.rentalservice.core.dto.request.RentalRequestDTO;
import com.rentalservice.core.entity.Rental;
import com.rentalservice.core.event.PaymentResultEvent;
import com.rentalservice.core.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentResultConsumer {
    private final RentalService rentalService;

    @KafkaListener(topics = "payment-result-topic", groupId = "rental-service-0")
    private void onMessage(ConsumerRecord<String, PaymentResultEvent> rec, Acknowledgment ack) {
        try {
            PaymentResultEvent event = rec.value();
            System.out.println(event);
            RentalRequestDTO rental = RentalRequestDTO
                    .builder()
                    .id(event.getRentalId())
                    .build();
            if (event.getResult().equals(PaymentResult.SUCCEEDED.name())) {
                rental.setStatus(RentalStatus.RESERVED.name());
            } else {
                rental.setStatus(RentalStatus.CANCELLED.name());
            }
            rentalService.updateRental(rental);
            ack.acknowledge();
        } catch (Exception e) {
            throw new AppException(ErrorCode.KAFKA_UNEXPECTED);
        }
    }
}
