package com.nexaride.booking_service.service;

import com.nexaride.booking_service.event.BookingEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducerService {
    private  final KafkaTemplate<String,Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBookingEvent(BookingEvent event){
        kafkaTemplate.send("booking-created",event.getRideId().toString(),event);
        log.info("Message Sent: "+event);
    }
}
