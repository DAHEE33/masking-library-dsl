package com.masking.demo;

import com.masking.audit.SlackAuditEventHandler;
import com.masking.pipeline.MaskPipelineBuilder;
import com.masking.strategy.mask.RegexMaskStrategy;
import com.masking.strategy.tokenize.UUIDTokenizationStrategy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 데이터 레코드에 대해 감사 로그 전송, 마스킹, 토큰화 파이프라인을 실행하는 데모 클래스
 */
public class Demo {
    public static void main(String[] args) throws IOException {
        // 1) 레코드 준비
        Map<String, String> record = new HashMap<>();
        record.put("email", "dahee@example.com");
        record.put("username", "maskingUser");

        // 2) Slack 핸들러 생성 (템플릿 파일에서 설정된 값 자동 로드)
        SlackAuditEventHandler slackHandler = new SlackAuditEventHandler();

        // 3) 파이프라인 구성 및 실행
        MaskPipelineBuilder.newBuilder()
                .mask("email", RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'))
                .tokenize("username", UUIDTokenizationStrategy.of())
                .build()
                .apply(record);

        // 4) 슬랙으로 원본 vs 변환 후 값 전송
        String originalEmail = "dahee@example.com";
        String maskedEmail = record.get("email");
        slackHandler.handle("email", originalEmail, maskedEmail);

        String originalUsername = "maskingUser";
        String tokenizedUsername = record.get("username");
        slackHandler.handle("username", originalUsername, tokenizedUsername);
    }
}
