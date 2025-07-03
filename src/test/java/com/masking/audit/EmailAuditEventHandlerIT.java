package com.masking.audit;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.masking.config.TemplateConfig;
import org.junit.jupiter.api.*;

import javax.mail.internet.MimeMessage;
import static org.junit.jupiter.api.Assertions.*;

/**
 * EmailAuditEventHandlerIT:
 * - GreenMail 서버를 사용하여 이메일 전송 기능을 통합 테스트, 가상의 SMTP 서버
 * - 인증 없이도 로컬 SMTP (3025 포트)로 메일 송신이 성공하는지 검증
 */
class EmailAuditEventHandlerIT {
    private static GreenMail greenMail;

    @BeforeAll
    static void startGreenMail() {
        greenMail = new GreenMail(new ServerSetup(3025, null, "smtp"));
        greenMail.start();
    }

    @AfterAll
    static void stopGreenMail() {
        greenMail.stop();
    }

    @Test
    void sendEmail_usingYamlConfig() throws Exception {
        // Load test-resources/audit-templates.yml
        TemplateConfig.init();
        EmailAuditEventHandler handler = new EmailAuditEventHandler();

        handler.handle("email", "orig@example.com", "masked@example.com");

        MimeMessage[] msgs = greenMail.getReceivedMessages();
        assertEquals(1, msgs.length);
        assertEquals("[AUDIT] field=email", msgs[0].getSubject());
        String body = (String)msgs[0].getContent();
        assertTrue(body.contains("before: orig@example.com"));
        assertTrue(body.contains("after: masked@example.com"));
    }
}
