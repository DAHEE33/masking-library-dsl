package com.masking.audit;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Slack Incoming Webhook URL로 간단한 메시지를 전송합니다.
 */
public class SlackAuditEventHandler implements AuditEventHandler {
    private final String webhookUrl;

    public SlackAuditEventHandler(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void handle(String field, String before, String after) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // payload 예시: {"text":"[AUDIT] field=email before=user@example.com after=null"}
            String payload = String.format(
                    "{\"text\":\"[AUDIT] field=%s before=%s after=%s\"}",
                    field, before, after
            );
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }
            conn.getInputStream().close();  // 호출을 완료해야 전송됨
        } catch (Exception e) {
            throw new RuntimeException("Slack 감사 전송 실패", e);
        }
    }
}
