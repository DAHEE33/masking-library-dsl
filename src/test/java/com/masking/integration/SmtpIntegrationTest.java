package com.masking.integration;

import com.masking.audit.EmailAuditEventHandler;
import com.masking.config.TemplateConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 운영 SMTP 환경 통합 테스트
 * 
 * 실제 SMTP 서버와의 연동을 테스트합니다.
 * 환경변수 EMAIL_USER, EMAIL_PASSWORD가 설정되어 있어야 합니다.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmtpIntegrationTest {

    @BeforeAll
    void setUp() throws IOException {
        TemplateConfig.init();
    }

    @Test
    void testProductionSmtpConnection() {
        // 환경변수 확인
        String emailUser = System.getenv("EMAIL_USER");
        String emailPassword = System.getenv("EMAIL_PASSWORD");
        
        if (emailUser == null || emailPassword == null) {
            System.out.println("⚠️  환경변수 EMAIL_USER, EMAIL_PASSWORD가 설정되지 않아 테스트를 건너뜁니다.");
            System.out.println("실제 SMTP 테스트를 원한다면 환경변수를 설정하세요.");
            return;
        }

        try {
            EmailAuditEventHandler emailHandler = new EmailAuditEventHandler();
            
            // 실제 이메일 전송 테스트
            emailHandler.handle("test_field", "original_value", "masked_value");
            
            System.out.println("✅ 실제 SMTP 서버로 이메일이 전송되었습니다.");
            System.out.println("📧 받은 편지함을 확인해보세요: " + System.getenv("EMAIL_TO"));
            
        } catch (Exception e) {
            fail("SMTP 연결 실패: " + e.getMessage());
        }
    }

    @Test
    void testEmailConfigurationValidation() {
        EmailAuditEventHandler emailHandler = new EmailAuditEventHandler();
        
        // 설정 유효성 검증
        assertNotNull(emailHandler, "EmailAuditEventHandler가 생성되어야 합니다");
        
        // 빈 값으로 테스트 (예외 발생 확인)
        assertThrows(Exception.class, () -> {
            emailHandler.handle("", "", "");
        });
    }
} 