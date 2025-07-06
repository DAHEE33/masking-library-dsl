package com.masking.audit;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * 감사 로그를 DB 테이블에 저장합니다.
 * 테이블 schema 예시
 *   CREATE TABLE audit_log (
 *     id        BIGINT AUTO_INCREMENT PRIMARY KEY,
 *     field     VARCHAR(100),
 *     before_val TEXT,
 *     after_val  TEXT,
 *     evt_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
 *   );
 *
 *
 *   H2에 넣으로 쓸거야
 */
public class DatabaseAuditEventHandler implements AuditEventHandler {
    private final DataSource dataSource;

    public DatabaseAuditEventHandler(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void handle(String field, String before, String after) {
        // 메시지 템플릿 적용 (yml에서 읽어옴)
        String messageTpl = null;
        try {
            com.masking.config.DatabaseConfig dbCfg = com.masking.config.TemplateConfig.getTemplates().database;
            if (dbCfg != null && dbCfg.message != null) {
                messageTpl = dbCfg.message;
            }
        } catch (Exception ignored) {}
        if (messageTpl == null) {
            messageTpl = "field=${field}, before=${before}, after=${after}";
        }
        String message = messageTpl.replace("${field}", field)
                                  .replace("${before}", before)
                                  .replace("${after}", after);

        String sql = "INSERT INTO audit_log(field, before_val, after_val) VALUES (?,?,?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, field);
            ps.setString(2, message); // 템플릿 메시지를 before_val에 저장
            ps.setString(3, after);   // after_val은 그대로
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("DB 감사로그 쓰기 실패", e);
        }
    }
}
