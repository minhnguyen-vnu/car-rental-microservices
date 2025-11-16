package com.rentalservice.infrastructure.messaging.producer;

import com.rentalservice.core.constant.KafkaTopic;
import com.rentalservice.core.event.RentalEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalEventProducer {
    private final KafkaTemplate<String, Object> kafka;

    public void sendRentalEvent(RentalEvent evt) {
        String key = evt.getVehicleId().toString();
        kafka.send(KafkaTopic.RENTAL_EVENT_TOPIC, key, evt);
    }

}
