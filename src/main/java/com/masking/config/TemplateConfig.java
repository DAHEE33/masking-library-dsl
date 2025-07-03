package com.masking.config;


import com.masking.util.YamlLoader;

import java.io.IOException;
import java.io.InputStream;

/**
 * TemplateConfig:
 * - audit-templates.yml 파일에서 Slack 및 Email 설정을 포함한 모든 템플릿을 로드합니다.
 * - getTemplates() 로 가져온 AuditTemplates 에서 slack, email 설정을 추출합니다.
 */

public class TemplateConfig {
    private static AuditTemplates templates;

    /**
     * audit-templates.yml을 로드하여 AuditTemplates 객체에 매핑
     */
    public static void init() {
        try (InputStream is = TemplateConfig.class.getResourceAsStream("/audit-templates.yml")) {
            if (is == null) {
                throw new RuntimeException("Resource not found: /audit-templates.yml");
            }
            templates = YamlLoader.load(is, AuditTemplates.class);
        } catch (IOException e) {
            throw new RuntimeException("템플릿 로딩 실패", e);
        }
    }

    /**
     * AuditTemplates를 반환합니다. 아직 로드되지 않았다면 init()을 호출
     * @return AuditTemplates 객체 (slack, email 설정 포함)
     */
    public static AuditTemplates getTemplates() {
        if (templates == null) {
            init();
        }
        return templates;
    }

    /**
     * EmailConfig를 반환
     * @return EmailConfig DTO (audit-templates.yml의 email 섹션)
     */
    public static EmailConfig getEmailConfig() {
        return getTemplates().email;
    }

    /**
     * Slack 설정을 반환
     * @return Slack 설정 DTO (audit-templates.yml의 slack 섹션)
     */
    public static AuditTemplates.Slack getSlackConfig() {
        return getTemplates().slack;
    }


}