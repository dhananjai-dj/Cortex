package com.project.kafka;

import com.project.dto.InjectRequest;
import com.project.util.Constants;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class Producer {

    private final KafkaTemplate<String, InjectRequest> kafkaTemplate;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public Producer(KafkaTemplate<String, InjectRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void pushToRetry(String key, InjectRequest injectRequest) {
        executorService.submit(() -> kafkaTemplate.send(Constants.KAFKA_RETRY_INJECTION_TOPIC, key, injectRequest));
    }
}
