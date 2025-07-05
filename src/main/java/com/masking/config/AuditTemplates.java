package com.masking.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * audit-templates.yml 전체 구조를 읽어 오는 루트 DTO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditTemplates {
    public Slack slack;
    public EmailConfig email;
    public DatabaseConfig database;

    public static class Slack {
        public String message;
        public String channel;
        public String username;
        public String icon_emoji;
        public String icon_url;
        public String webhook_url;
    }
}
