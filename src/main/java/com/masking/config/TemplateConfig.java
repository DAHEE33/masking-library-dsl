package com.masking.config;


import com.masking.util.YamlLoader;

import java.io.IOException;
import java.io.InputStream;

public class TemplateConfig {
    private static AuditTemplates templates;

    public static void init() throws IOException {
        try (InputStream is =
                     TemplateConfig.class.getResourceAsStream("/audit-templates.yml")) {
            templates = YamlLoader.load(is, AuditTemplates.class);
        }
    }

    public static AuditTemplates get() throws IOException {
        if (templates == null) init();
        return templates;
    }
}