package com.masking.config;

import java.util.Map;

public class AuditTemplates {
    public Slack slack;

    public static class Slack {
        public String message;
        public String channel;
        public String username;
        public String icon_emoji;
        public String icon_url;
        public String webhook_url;
    }
}
