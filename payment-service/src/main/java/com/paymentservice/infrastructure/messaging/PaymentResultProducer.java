package com.paymentservice.infrastructure.messaging;

import com.paymentservice.core.KafkaTopic;
import com.paymentservice.core.event.PaymentResultEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentResultProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentResult(PaymentResultEvent paymentResultEvent) {
        kafkaTemplate.send(KafkaTopic.PAYMENT_RESULT_TOPIC,
                paymentResultEvent.getRentalId().toString(),
                paymentResultEvent);
    }
}
