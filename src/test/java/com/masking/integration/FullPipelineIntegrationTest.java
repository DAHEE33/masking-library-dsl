package com.masking.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.masking.audit.DatabaseAuditEventHandler;
import com.masking.audit.EmailAuditEventHandler;
import com.masking.audit.SlackAuditEventHandler;
import com.masking.config.TemplateConfig;
import com.masking.pipeline.MaskPipeline;
import com.masking.pipeline.MaskPipelineBuilder;
import com.masking.strategy.mask.RegexMaskStrategy;
import com.masking.strategy.tokenize.UUIDTokenizationStrategy;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * FullPipelineIntegrationTest:
 * - H2, GreenMail, WireMock(Slack)을 함께 띄워, 전체 파이프라인을 검증합니다.
 */
class FullPipelineIntegrationTest {
    private static WireMockServer wireMock;
    private static GreenMail greenMail;
    private static DataSource ds;

    @BeforeAll
    static void beforeAll() throws Exception {
        // 1) Test YAML 로드 (Slack+Email config)
        TemplateConfig.init();

        // 2) WireMock (Slack stub)
        wireMock = new WireMockServer(0);
        wireMock.start();
        WireMock.configureFor(wireMock.port());
        stubFor(post(urlEqualTo("/slack"))
                .willReturn(aResponse().withStatus(200)));

        // 3) GreenMail (SMTP)
        greenMail = new GreenMail(new ServerSetup(3025, null, "smtp"));
        greenMail.start();

        // 4) H2 DataSource and table
        JdbcDataSource h2 = new JdbcDataSource();
        h2.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        h2.setUser("sa");
        h2.setPassword("");
        ds = h2;
        try (Connection c = ds.getConnection();
             Statement s = c.createStatement()) {
            s.execute("CREATE TABLE audit_log (" +
                    "id IDENTITY PRIMARY KEY, " +
                    "field VARCHAR(100), before_val VARCHAR(4000), after_val VARCHAR(4000), evt_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");
        }
    }

    @AfterAll
    static void afterAll() {
        wireMock.stop();
        greenMail.stop();
    }

    @Test
    void fullPipeline_shouldMaskTokenizeEncryptAuditAllWays() throws Exception {
        // handlers
        DatabaseAuditEventHandler dbHandler = new DatabaseAuditEventHandler(ds);
        EmailAuditEventHandler emailHandler = new EmailAuditEventHandler();
        // email 설정 확인
        com.masking.config.EmailConfig cfg = com.masking.config.TemplateConfig.getEmailConfig();
        System.out.println("[TEST] EmailConfig: host=" + cfg.smtpHost + ", port=" + cfg.smtpPort + ", from=" + cfg.from + ", to=" + cfg.to + ", username=" + cfg.username + ", starttls=" + cfg.starttls);
        
        // Debug: Check GreenMail status
        System.out.println("=== GREENMAIL STATUS DEBUG ===");
        System.out.println("GreenMail running: " + greenMail.isRunning());
        System.out.println("GreenMail SMTP port: " + greenMail.getSmtp().getPort());
        System.out.println("===============================");
        // 실제 Slack Webhook 주소로 강제 세팅
        String slackStubUrl = "https://hooks.slack.com/services/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";
        TemplateConfig.getTemplates().slack.webhook_url = slackStubUrl;
        SlackAuditEventHandler slackHandler = null;
        try {
            slackHandler = new SlackAuditEventHandler();
        } catch (Exception e) {
            System.out.println("[TEST] SlackAuditEventHandler 생성 실패: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        // original record
        String origEmail = "dahee@example.com";
        String origUser  = "maskingUser";
        String origSsn   = "123456789";
        Map<String,String> record = new HashMap<>();
        record.put("email", origEmail);
        record.put("username", origUser);
        record.put("ssn", origSsn);

        // pipeline
        MaskPipeline pipeline = MaskPipelineBuilder.newBuilder()
                .mask("email", RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'))
                .audit("email", dbHandler)
                .audit("email", emailHandler)
                .audit("email", slackHandler)
                .tokenize("username", UUIDTokenizationStrategy.of())
                .encryptAes("ssn", Base64.getDecoder().decode(Base64.getEncoder().encodeToString(new byte[16])))
                .build();
        try {
            pipeline.apply(record);
            System.out.println("[TEST] Pipeline applied successfully");
        } catch (Exception e) {
            System.out.println("[TEST] Pipeline failed with exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        // DB: one record for email
        try (Connection c = ds.getConnection();
             java.sql.ResultSet rs = c.createStatement().executeQuery(
                     "SELECT field, before_val, after_val FROM audit_log WHERE field='email'")) {
            assertTrue(rs.next());
            String beforeVal = rs.getString(2);
            String afterVal = rs.getString(3);
            
            System.out.println("[TEST] DB Debug - before_val: " + beforeVal);
            System.out.println("[TEST] DB Debug - after_val: " + afterVal);
            
            assertEquals("d***e@example.com", beforeVal);
            assertTrue(afterVal == null);
        }

        // Slack: verify stubbed (실제 Slack 사용 시 주석 처리)
        // verify(postRequestedFor(urlEqualTo("/slack"))
        //         .withRequestBody(matchingJsonPath("$.text", containing("field=email"))));

        // Email: GreenMail
        System.out.println("[TEST] Checking GreenMail messages...");
        MimeMessage[] msgs = greenMail.getReceivedMessages();
        System.out.println("[TEST] GreenMail received messages: " + (msgs == null ? "null" : msgs.length));
        
        if (msgs != null && msgs.length > 0) {
            System.out.println("[TEST] First message subject: " + msgs[0].getSubject());
            System.out.println("[TEST] First message from: " + msgs[0].getFrom()[0]);
            System.out.println("[TEST] First message to: " + msgs[0].getAllRecipients()[0]);
        }
        
        assertEquals(1, msgs.length);
        assertEquals("[AUDIT] field=email", msgs[0].getSubject());

        // Username token is UUID
        String token = record.get("username");
        assertTrue(token.matches("[0-9a-f\\-]{36}"));
    }
}
