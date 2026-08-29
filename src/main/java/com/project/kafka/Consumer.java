package com.project.kafka;

import com.project.dto.InjectRequest;
import com.project.service.InjectorService;
import com.project.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
    private final InjectorService injectorService;

    public Consumer(InjectorService injectorService) {
        this.injectorService = injectorService;
    }

    @KafkaListener(topics = Constants.KAFKA_RETRY_INJECTION_TOPIC, groupId = "cortex-kb-group")
    public void consume(
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Payload InjectRequest injectRequest
    ) {
        try {
            UUID documentId = UUID.fromString(key);
            injectorService.process(documentId, injectRequest.summary(), injectRequest.metaData(), true);
        } catch (Exception e) {
            logger.error("Error in consuming the payload for the key {} error {}", key, e.getMessage());
        }
    }
}
