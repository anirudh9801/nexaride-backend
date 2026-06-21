package com.nexaride.audit_service.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaErrorConfig {
        // DLQ specific KafkaTemplate
        @Bean
        public KafkaTemplate<String, Object> dlqKafkaTemplate(
                ProducerFactory<String, Object> producerFactory) {

            Map<String, Object> props = new HashMap<>(producerFactory.getConfigurationProperties());

            //  override serializer to JSON
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                    org.springframework.kafka.support.serializer.JsonSerializer.class);

            ProducerFactory<String, Object> factory =
                    new DefaultKafkaProducerFactory<>(props);

            return new KafkaTemplate<>(factory);
        }

        //  Error handler using DLQ template
        @Bean
        public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> dlqKafkaTemplate) {

            log.info("Inside DefaultErrorHandler method...");

            DeadLetterPublishingRecoverer recoverer =
                    new DeadLetterPublishingRecoverer(
                            dlqKafkaTemplate,
                            (record, ex) ->
                                    new TopicPartition("booking-created-dlq", record.partition())
                    );

            return new DefaultErrorHandler(
                    recoverer,
                    new FixedBackOff(2000, 3) //  retry config
            );
        }
    }
