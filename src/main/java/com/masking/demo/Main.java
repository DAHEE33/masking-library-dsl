package com.masking.demo;

import com.masking.action.*;
import com.masking.audit.ConsoleAuditEventHandler;
import com.masking.strategy.*;
import com.masking.strategy.encrypt.AesEncryptionStrategy;
import com.masking.strategy.mask.RegexMaskStrategy;
import com.masking.strategy.tokenize.UUIDTokenizationStrategy;
import com.masking.util.CryptoUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        // 샘플 레코드 준비
        Map<String, String> record = new HashMap<>();
        record.put("email",    "dahee@example.com");
        record.put("username", "maskingUser");
        record.put("ssn",      "123-45-6789");
        record.put("id",       "originalValue");

        // AES 키 생성 (Base64)
        String aesKeyBase64 = CryptoUtil.generateAesKeyBase64();
        byte[] aesKey = CryptoUtil.decodeBase64(aesKeyBase64);

        // 파이프라인 구성
        Action pipeline = Actions.of(
                // 감사(콘솔)
                AuditAction.of("email",   new ConsoleAuditEventHandler()),
                // 마스킹
                MaskAction.of("email",    RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*')),
                // 토큰화
                TokenizeAction.of("id",   UUIDTokenizationStrategy.of()),
                // 암호화
                EncryptAction.of("ssn",   AesEncryptionStrategy.of(aesKey))
        );

        // 실행
        pipeline.apply(record);

        // 결과 출력
        System.out.println("\n최종 레코드:");
        record.forEach((k, v) -> System.out.printf("  %s -> %s%n", k, v));
    }
}