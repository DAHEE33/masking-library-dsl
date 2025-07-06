package com.masking.integration;

import com.masking.audit.EmailAuditEventHandler;
import com.masking.config.EmailConfig;
import com.masking.config.TemplateConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * 실제 SMTP 서버를 사용한 이메일 발송 테스트
 * 
 * 환경변수 설정 필요:
 * - EMAIL_SMTP_HOST: SMTP 서버 주소 (예: smtp.gmail.com)
 * - EMAIL_SMTP_PORT: SMTP 포트 (예: 587)
 * - EMAIL_FROM: 발신자 이메일
 * - EMAIL_TO: 수신자 이메일
 * - EMAIL_USERNAME: SMTP 인증 사용자명
 * - EMAIL_PASSWORD: SMTP 인증 비밀번호
 */
@EnabledIfEnvironmentVariable(named = "RUN_INTEGRATION_TESTS", matches = "true")
class SmtpIntegrationTest {

    @BeforeAll
    static void beforeAll() throws Exception {
        // YAML 설정 로드
        TemplateConfig.init();
    }

    @Test
    void shouldSendEmailViaRealSmtp() {
        // 테스트용 레코드
        Map<String, String> record = new HashMap<>();
        record.put("email", "test@example.com");
        record.put("ssn", "123-45-6789");

        // 이메일 핸들러 생성
        EmailAuditEventHandler emailHandler = new EmailAuditEventHandler();

        // 실제 SMTP로 이메일 발송 테스트
        assertDoesNotThrow(() -> {
            emailHandler.handle("email", "test@example.com", "t***@example.com");
            System.out.println("[TEST] 이메일 발송 완료");
        });
    }

    @Test
    void shouldSendEmailWithCustomConfig() {
        // 커스텀 이메일 설정
        EmailConfig customConfig = new EmailConfig();
        customConfig.smtpHost = System.getenv("EMAIL_SMTP_HOST");
        customConfig.smtpPort = Integer.parseInt(System.getenv("EMAIL_SMTP_PORT"));
        customConfig.from = System.getenv("EMAIL_FROM");
        customConfig.to = System.getenv("EMAIL_TO");
        customConfig.username = System.getenv("EMAIL_USERNAME");
        customConfig.password = System.getenv("EMAIL_PASSWORD");
        customConfig.starttls = true;

        // 커스텀 설정으로 이메일 핸들러 생성
        EmailAuditEventHandler emailHandler = new EmailAuditEventHandler(customConfig);

        // 실제 SMTP로 이메일 발송 테스트
        assertDoesNotThrow(() -> {
            emailHandler.handle("ssn", "123-45-6789", "***-**-6789");
            System.out.println("[TEST] 커스텀 설정으로 이메일 발송 완료");
        });
    }
} 