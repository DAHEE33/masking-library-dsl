package com.masking.demo;

import com.masking.action.*;
import com.masking.audit.ConsoleAuditEventHandler;
import com.masking.audit.DatabaseAuditEventHandler;
import com.masking.config.DataSourceConfig;
import com.masking.pipeline.MaskPipelineBuilder;
import com.masking.strategy.mask.RegexMaskStrategy;
import com.masking.strategy.tokenize.UUIDTokenizationStrategy;
import com.masking.strategy.encrypt.AesEncryptionStrategy;
import com.masking.util.CryptoUtil;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 감사 추적의 실제 사용 사례를 보여주는 예제
 */
public class AuditExample {
    public static void main(String[] args) throws IOException {
        System.out.println("=== 감사 추적 실제 사용 사례 ===\n");

        // 1. 개인정보보호법 준수 시나리오
        demonstratePrivacyCompliance();
        
        // 2. 보안 사고 대응 시나리오  
        demonstrateSecurityIncident();
        
        // 3. 데이터 품질 관리 시나리오
        demonstrateDataQuality();
        
        // 4. 데이터베이스 감사 로그 저장
        demonstrateDatabaseAudit();
    }

    private static void demonstratePrivacyCompliance() throws IOException {
        System.out.println("1. 개인정보보호법 준수 시나리오");
        System.out.println("목적: 고객 데이터 마스킹 시 법적 증거 보존\n");
        
        Map<String, String> customerData = new HashMap<>();
        customerData.put("email", "kim.dahee@company.com");
        customerData.put("phone", "010-1234-5678");
        customerData.put("ssn", "123-45-6789");
        
        ConsoleAuditEventHandler auditHandler = new ConsoleAuditEventHandler();
        
        // 감사 추적과 함께 마스킹 실행
        MaskPipelineBuilder.newBuilder()
            .maskWithAudit("email", RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'), auditHandler)
            .maskWithAudit("phone", RegexMaskStrategy.of("(?<=.{3}).(?=.{4})", '*'), auditHandler)
            .encryptAesWithAudit("ssn", generateAesKey(), auditHandler)
            .build()
            .apply(customerData);
            
        System.out.println("결과: " + customerData);
        System.out.println("→ 법적 감사 시 '언제, 어떻게 개인정보를 보호했는지' 증명 가능\n");
    }

    private static void demonstrateSecurityIncident() throws IOException {
        System.out.println("2. 보안 사고 대응 시나리오");
        System.out.println("목적: 의심스러운 데이터 접근 패턴 감지\n");
        
        Map<String, String> suspiciousData = new HashMap<>();
        suspiciousData.put("admin_email", "admin@company.com");
        suspiciousData.put("admin_ssn", "987-65-4321");
        
        ConsoleAuditEventHandler securityAudit = new ConsoleAuditEventHandler();
        
        // 보안 강화를 위한 암호화 + 감사
        MaskPipelineBuilder.newBuilder()
            .encryptAesWithAudit("admin_email", generateAesKey(), securityAudit)
            .encryptAesWithAudit("admin_ssn", generateAesKey(), securityAudit)
            .build()
            .apply(suspiciousData);
            
        System.out.println("결과: " + suspiciousData);
        System.out.println("→ 보안 사고 발생 시 '어떤 관리자 데이터가 언제 암호화되었는지' 추적 가능\n");
    }

    private static void demonstrateDataQuality() throws IOException {
        System.out.println("3. 데이터 품질 관리 시나리오");
        System.out.println("목적: 데이터 정제 과정에서의 변경 이력 추적\n");
        
        Map<String, String> dirtyData = new HashMap<>();
        dirtyData.put("email", "invalid-email-format");
        dirtyData.put("phone", "010-1234-5678");
        
        ConsoleAuditEventHandler qualityAudit = new ConsoleAuditEventHandler();
        
        // 데이터 정제 + 감사
        MaskPipelineBuilder.newBuilder()
            .maskWithAudit("email", RegexMaskStrategy.of("(?<=.).(?=.{3,}@)", '*'), qualityAudit)
            .maskWithAudit("phone", RegexMaskStrategy.of("(?<=.{3}).(?=.{4})", '*'), qualityAudit)
            .build()
            .apply(dirtyData);
            
        System.out.println("결과: " + dirtyData);
        System.out.println("→ 데이터 품질 개선 과정에서 '어떤 오류가 어떻게 수정되었는지' 추적 가능\n");
    }

    private static void demonstrateDatabaseAudit() throws IOException {
        System.out.println("4. 데이터베이스 감사 로그 저장");
        System.out.println("목적: 영구적인 감사 로그 보관\n");
        
        try {
            // H2 데이터베이스 설정
            DataSource ds = DataSourceConfig.createH2DataSource();
            setupAuditTable(ds);
            
            DatabaseAuditEventHandler dbAudit = new DatabaseAuditEventHandler(ds);
            
            Map<String, String> testData = new HashMap<>();
            testData.put("email", "test@example.com");
            testData.put("ssn", "111-22-3333");
            
            // 데이터베이스에 감사 로그 저장
            MaskPipelineBuilder.newBuilder()
                .maskWithAudit("email", RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'), dbAudit)
                .encryptAesWithAudit("ssn", generateAesKey(), dbAudit)
                .build()
                .apply(testData);
                
            // 저장된 감사 로그 조회
            System.out.println("저장된 감사 로그:");
            printAuditLogs(ds);
            
        } catch (Exception e) {
            System.err.println("데이터베이스 감사 로그 저장 실패: " + e.getMessage());
        }
    }

    private static byte[] generateAesKey() {
        String keyBase64 = CryptoUtil.generateAesKeyBase64();
        return CryptoUtil.decodeBase64(keyBase64);
    }

    private static void setupAuditTable(DataSource ds) throws Exception {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS audit_log (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "field VARCHAR(100)," +
                "before_val TEXT," +
                "after_val TEXT," +
                "evt_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")")) {
            ps.executeUpdate();
        }
    }

    private static void printAuditLogs(DataSource ds) throws Exception {
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                "SELECT field, before_val, after_val, evt_time FROM audit_log ORDER BY evt_time");
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                System.out.printf("  [%s] %s: %s → %s%n",
                    rs.getTimestamp("evt_time"),
                    rs.getString("field"),
                    rs.getString("before_val"),
                    rs.getString("after_val"));
            }
        }
    }
} 