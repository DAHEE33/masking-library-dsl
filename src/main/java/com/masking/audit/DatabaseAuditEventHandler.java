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
        String sql = "INSERT INTO audit_log(field, before_val, after_val) VALUES (?,?,?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, field);
            ps.setString(2, before);
            ps.setString(3, after);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("DB 감사로그 쓰기 실패", e);
        }
    }
}
