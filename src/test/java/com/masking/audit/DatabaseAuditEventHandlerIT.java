package com.masking.audit;

import com.masking.config.DataSourceConfig;
import org.junit.jupiter.api.*;
import javax.sql.DataSource;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseAuditEventHandlerIT {
    private static DataSource ds;

    @BeforeAll
    static void setup() throws Exception {
        ds = DataSourceConfig.createH2DataSource();
        try (Connection c = ds.getConnection();
             Statement s = c.createStatement()) {
            s.executeUpdate(
                    "CREATE TABLE audit_log (" +
                            " id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                            " field VARCHAR(100)," +
                            " before_val VARCHAR(4000)," +
                            " after_val VARCHAR(4000)," +
                            " evt_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP()" +
                            ");"
            );
        }
    }

    @Test
    void handle_shouldInsertRow() throws Exception {
        DatabaseAuditEventHandler handler = new DatabaseAuditEventHandler(ds);
        // handler.handle 인자도 정상적인 리터럴로 변경
        handler.handle("email", "foo@bar.com", "f**@bar.com");

        try (Connection c = ds.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT field, before_val, after_val FROM audit_log");
             ResultSet rs = ps.executeQuery()) {

            assertTrue(rs.next());
            assertEquals("email",         rs.getString("field"));
            assertEquals("foo@bar.com",   rs.getString("before_val"));
            assertEquals("f**@bar.com",   rs.getString("after_val"));
            assertFalse(rs.next());
        }
    }
}
