package com.masking.demo;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.masking.audit.EmailAuditEventHandler;
import com.masking.audit.SlackAuditEventHandler;
import com.masking.config.TemplateConfig;
import com.masking.pipeline.MaskPipelineBuilder;
import com.masking.strategy.mask.RegexMaskStrategy;
import com.masking.strategy.tokenize.UUIDTokenizationStrategy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Demo:
 * - 로컬 데모를 위해 GreenMail SMTP 서버를 내장합니다.
 * - 파이프라인(마스킹+토큰화) 실행 후, Email 및 Slack으로 감사 전송합니다.
 */
public class Demo {
    public static void main(String[] args) throws IOException {
//        // 1) 레코드 준비
//        Map<String, String> record = new HashMap<>();
//        record.put("email", "dahee@example.com");
//        record.put("username", "maskingUser");
//
//        // 2) Slack 핸들러 생성 (템플릿 파일에서 설정된 값 자동 로드)
//        SlackAuditEventHandler slackHandler = new SlackAuditEventHandler();
//
//        // 3) 파이프라인 구성 및 실행
//        MaskPipelineBuilder.newBuilder()
//                .mask("email", RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'))
//                .tokenize("username", UUIDTokenizationStrategy.of())
//                .build()
//                .apply(record);
//
//        // 4) 슬랙으로 원본 vs 변환 후 값 전송
//        String originalEmail = "dahee@example.com";
//        String maskedEmail = record.get("email");
//        slackHandler.handle("email", originalEmail, maskedEmail);
//
//        String originalUsername = "maskingUser";
//        String tokenizedUsername = record.get("username");
//        slackHandler.handle("username", originalUsername, tokenizedUsername);

        // 0) GreenMail 서버 시작 (local SMTP 테스트)
        GreenMail greenMail = new GreenMail(new ServerSetup(3025, null, "smtp"));
        greenMail.start();
        try {
            // 1) 설정 파일 로드 (slack + email)
            TemplateConfig.init();

            // 2) 핸들러 생성
            EmailAuditEventHandler emailHandler = new EmailAuditEventHandler();
            SlackAuditEventHandler slackHandler = new SlackAuditEventHandler();

            // 3) 원본 레코드 준비
            String origEmail = "dahee@example.com";
            String origUser  = "maskingUser";
            Map<String, String> record = new HashMap<>();
            record.put("email", origEmail);
            record.put("username", origUser);

            // 4) 파이프라인 실행 (mask + tokenize)
            MaskPipelineBuilder.newBuilder()
                    .mask("email", RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'))
                    .tokenize("username", UUIDTokenizationStrategy.of())
                    .build()
                    .apply(record);

            // 5) 변환 결과
            String maskedEmail       = record.get("email");
            String tokenizedUsername = record.get("username");

            // 6) 감사 전송
            emailHandler.handle("email", origEmail, maskedEmail);
            emailHandler.handle("username", origUser, tokenizedUsername);
            slackHandler.handle("email", origEmail, maskedEmail);
            slackHandler.handle("username", origUser, tokenizedUsername);
        } finally {
            // 7) GreenMail 서버 중지
            greenMail.stop();
        }
    }
}