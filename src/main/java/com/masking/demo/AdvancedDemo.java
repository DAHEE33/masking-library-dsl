package com.masking.demo;

import com.masking.action.*;
import com.masking.audit.ConsoleAuditEventHandler;
import com.masking.audit.SlackAuditEventHandler;
import com.masking.pipeline.MaskPipelineBuilder;
import com.masking.strategy.mask.RegexMaskStrategy;
import com.masking.strategy.tokenize.UUIDTokenizationStrategy;
import com.masking.strategy.encrypt.AesEncryptionStrategy;
import com.masking.util.CryptoUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 고급 데모: 다양한 감사 추적 방식 보여주기
 */
public class AdvancedDemo {
    public static void main(String[] args) throws IOException {
        System.out.println("=== 마스킹 라이브러리 고급 데모 ===\n");

        // 샘플 데이터
        Map<String, String> record = new HashMap<>();
        record.put("email", "user@example.com");
        record.put("phone", "010-1234-5678");
        record.put("ssn", "123-45-6789");
        record.put("id", "original123");

        // AES 키 생성
        String aesKeyBase64 = CryptoUtil.generateAesKeyBase64();
        byte[] aesKey = CryptoUtil.decodeBase64(aesKeyBase64);

        // 핸들러들
        ConsoleAuditEventHandler consoleHandler = new ConsoleAuditEventHandler();
        SlackAuditEventHandler slackHandler = new SlackAuditEventHandler();

        System.out.println("1. 기본 감사 (before만 추적)");
        System.out.println("원본 데이터: " + record);
        
        // 기본 감사 + 마스킹
        Action basicPipeline = Actions.of(
            AuditAction.of("email", consoleHandler),
            MaskAction.of("email", RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'))
        );
        basicPipeline.apply(record);
        System.out.println("결과: " + record.get("email") + "\n");

        // 레코드 초기화
        record.put("email", "user@example.com");

        System.out.println("2. 복합 감사 (before/after 모두 추적)");
        System.out.println("원본 데이터: " + record);
        
        // 복합 감사 (before/after 모두 추적)
        Action compositePipeline = Actions.of(
            CompositeAuditAction.of("email", consoleHandler, 
                MaskAction.of("email", RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'))),
            CompositeAuditAction.of("phone", slackHandler,
                MaskAction.of("phone", RegexMaskStrategy.of("(?<=.{3}).(?=.{4})", '*'))),
            CompositeAuditAction.of("ssn", consoleHandler,
                EncryptAction.of("ssn", AesEncryptionStrategy.of(aesKey))),
            CompositeAuditAction.of("id", slackHandler,
                TokenizeAction.of("id", UUIDTokenizationStrategy.of()))
        );
        compositePipeline.apply(record);
        
        System.out.println("최종 결과:");
        record.forEach((k, v) -> System.out.printf("  %s: %s%n", k, v));
        System.out.println();

        System.out.println("3. 빌더 패턴으로 복합 감사");
        // 레코드 초기화
        record.clear();
        record.put("email", "test@example.com");
        record.put("phone", "010-9876-5432");
        
        MaskPipelineBuilder.newBuilder()
            .maskWithAudit("email", RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'), consoleHandler)
            .maskWithAudit("phone", RegexMaskStrategy.of("(?<=.{3}).(?=.{4})", '*'), slackHandler)
            .build()
            .apply(record);
            
        System.out.println("빌더 결과:");
        record.forEach((k, v) -> System.out.printf("  %s: %s%n", k, v));
    }
} 