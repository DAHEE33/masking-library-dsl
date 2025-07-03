package com.masking.config;

/**
 * audit-templates.yml 전체 구조를 읽어 오는 루트 DTO
 */
public class AuditTemplates {
    public Slack slack;
    public EmailConfig email;

    public static class Slack {
        public String message;
        public String channel;
        public String username;
        public String icon_emoji;
        public String icon_url;
        public String webhook_url;
    }
}
