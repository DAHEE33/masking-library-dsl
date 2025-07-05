package com.masking.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masking.audit.AuditEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka를 통한 감사 이벤트 핸들러
 * 
 * 감사 이벤트를 Kafka 토픽으로 전송합니다.
 * 실제 Kafka 클라이언트는 별도 의존성으로 추가해야 합니다.
 */
public class KafkaAuditEventHandler implements AuditEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaAuditEventHandler.class);
    
    private final String topic;
    private final ObjectMapper objectMapper;
    private final KafkaProducerWrapper producer;
    
    /**
     * Kafka 감사 핸들러를 생성합니다.
     * 
     * @param topic Kafka 토픽명
     * @param bootstrapServers Kafka 서버 주소 (예: "localhost:9092")
     */
    public KafkaAuditEventHandler(String topic, String bootstrapServers) {
        this.topic = topic;
        this.objectMapper = new ObjectMapper();
        this.producer = new KafkaProducerWrapper(bootstrapServers);
    }
    
    /**
     * 감사 이벤트를 Kafka로 전송합니다.
     * 
     * @param field 변경된 필드명
     * @param before 변경 전 값
     * @param after 변경 후 값
     */
    @Override
    public void handle(String field, String before, String after) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("timestamp", System.currentTimeMillis());
            event.put("field", field);
            event.put("before", before);
            event.put("after", after);
            event.put("source", "masking-library");
            
            String message = objectMapper.writeValueAsString(event);
            
            producer.send(topic, message);
            
            logger.debug("Kafka로 감사 이벤트 전송: {}", message);
            
        } catch (Exception e) {
            logger.error("Kafka 감사 이벤트 전송 실패: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Kafka Producer 래퍼 클래스
     * 실제 Kafka 클라이언트 의존성이 없을 때를 대비한 시뮬레이션 구현
     */
    private static class KafkaProducerWrapper {
        
        private final String bootstrapServers;
        
        public KafkaProducerWrapper(String bootstrapServers) {
            this.bootstrapServers = bootstrapServers;
            logger.info("Kafka Producer 초기화: {}", bootstrapServers);
        }
        
        public void send(String topic, String message) {
            // 실제 구현에서는 Kafka Producer를 사용
            logger.info("Kafka 메시지 전송 시뮬레이션 - Topic: {}, Message: {}", topic, message);
            
            // 실제 Kafka 클라이언트가 있다면:
            // producer.send(new ProducerRecord<>(topic, message));
        }
    }
} 