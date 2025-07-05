package com.masking.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.masking.config.AuditTemplates;
import com.masking.config.TemplateConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Slack Incoming Webhook을 통해 감사 메시지를 전송합니다.
 * audit-templates.yml 설정을 로드하여 webhook URL, 채널, 봇 이름, 이모지, 아이콘 URL을 사용합니다.
 */
public class SlackAuditEventHandler implements AuditEventHandler {
    private final AuditTemplates.Slack tpl;

    /**
     * 설정파일에서 Slack 템플릿을 로드하여 핸들러를 초기화합니다.
     */
    public SlackAuditEventHandler() throws IOException {
        TemplateConfig.init();
        this.tpl = TemplateConfig.getSlackConfig();
        System.out.println("[SLACK DEBUG] SlackAuditEventHandler 생성됨. webhook_url=" + this.tpl.webhook_url);
    }

    @Override
    public void handle(String field, String before, String after) {
        System.out.println("[SLACK DEBUG] handle() 호출됨. field=" + field + ", before=" + before + ", after=" + after);
        HttpURLConnection conn = null;
        try {
            // 플레이스홀더 치환
            String text = tpl.message
                    .replace("${field}", field)
                    .replace("${before}", before == null ? "null" : before)
                    .replace("${after}", after == null ? "null" : after);

            // 페이로드 구성
            Map<String, Object> payload = new HashMap<>();
            payload.put("text", text);
            if (tpl.channel != null)    payload.put("channel", tpl.channel);
            if (tpl.username != null)   payload.put("username", tpl.username);
            if (tpl.icon_emoji != null) payload.put("icon_emoji", tpl.icon_emoji);
            if (tpl.icon_url != null)   payload.put("icon_url", tpl.icon_url);

            String json = new ObjectMapper().writeValueAsString(payload);

            // 디버그 출력
            System.out.println("[SLACK DEBUG] Webhook URL: " + tpl.webhook_url);
            System.out.println("[SLACK DEBUG] Payload: " + json);

            // HTTP 연결
            URL url = new URL(tpl.webhook_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            // 전송
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            // 응답 확인
            int status = conn.getResponseCode();
            System.out.println("[SLACK DEBUG] Response status: " + status);
            if (status >= 400) {
                String error = readStream(conn.getErrorStream());
                throw new IOException("Slack Webhook returned HTTP " + status + ": " + error);
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException("잘못된 Slack Webhook URL: " + tpl.webhook_url, e);
        } catch (IOException e) {
            throw new RuntimeException("Slack 감사 전송 실패", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * InputStream을 문자열로 읽어 반환합니다.
     */
    private String readStream(InputStream is) {
        if (is == null) return "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException ignored) {
        }
        return sb.toString().trim();
    }
}