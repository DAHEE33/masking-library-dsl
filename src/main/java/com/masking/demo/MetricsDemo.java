package com.masking.demo;

import com.masking.action.*;
import com.masking.audit.ConsoleAuditEventHandler;
import com.masking.strategy.encrypt.AesEncryptionStrategy;
import com.masking.strategy.mask.RegexMaskStrategy;
import com.masking.strategy.tokenize.UUIDTokenizationStrategy;
import com.masking.util.CryptoUtil;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * 메트릭 사용법 데모
 * 
 * 라이브러리 사용자가 성능을 모니터링하는 방법을 보여줍니다.
 */
public class MetricsDemo {
    
    public static void main(String[] args) {
        // 1. 메트릭 레지스트리 설정
        // 개발환경: SimpleMeterRegistry
        MeterRegistry devRegistry = new SimpleMeterRegistry();
        
        // 운영환경: Prometheus (Grafana 연동 가능)
        // MeterRegistry prodRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        
        // 2. 샘플 데이터
        Map<String, String> record = new HashMap<>();
        record.put("email", "user@example.com");
        record.put("ssn", "123-45-6789");
        record.put("username", "testuser");
        
        // 3. AES 키 생성
        String aesKeyBase64 = CryptoUtil.generateAesKeyBase64();
        byte[] aesKey = CryptoUtil.decodeBase64(aesKeyBase64);
        
        // 4. 메트릭이 포함된 Action 실행
        System.out.println("=== 메트릭이 포함된 Action 실행 ===");
        
        // 단일 Action with 메트릭
        Action maskAction = MaskAction.of("email", RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'));
        maskAction.applyWithMetrics(record, devRegistry);
        
        // 복합 Action with 메트릭
        Actions actions = Actions.of(
            AuditAction.of("email", new ConsoleAuditEventHandler()),
            TokenizeAction.of("username", UUIDTokenizationStrategy.of()),
            EncryptAction.of("ssn", AesEncryptionStrategy.of(aesKey))
        );
        actions.applyWithMetrics(record, devRegistry);
        
        // 5. 메트릭 결과 출력
        System.out.println("\n=== 메트릭 결과 ===");
        devRegistry.getMeters().forEach(meter -> {
            System.out.printf("%s: %s%n", meter.getId().getName(), meter.measure());
        });
        
        // 6. 결과 출력
        System.out.println("\n=== 최종 레코드 ===");
        record.forEach((k, v) -> System.out.printf("  %s -> %s%n", k, v));
    }
} 