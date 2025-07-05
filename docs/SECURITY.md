# Security Guide

## 개요

이 문서는 Masking Library 사용 시 보안 관련 주의사항과 모범 사례를 제공합니다.

## 🔐 암호화 키 관리

### 키 생성
```java
// 안전한 키 생성
byte[] aesKey = CryptoUtil.generateSecureKey(256); // 256-bit AES 키
KeyPair rsaKeyPair = CryptoUtil.generateRsaKeyPair(2048); // 2048-bit RSA 키쌍
```

### 키 저장
```java
// ❌ 절대 하드코딩하지 마세요
String key = "my-secret-key"; // 위험!

// ✅ 환경변수나 보안 저장소 사용
String key = System.getenv("ENCRYPTION_KEY");
String key = keyStore.getKey("my-key");
```

### 키 로테이션
```java
// 정기적인 키 교체 권장
// 키 버전 관리
String keyVersion = "v2";
String encryptedData = encryptWithVersion(data, keyVersion);
```

## 🛡️ 민감 정보 로깅 방지

### 로그 마스킹
```java
// ❌ 민감 정보가 로그에 노출됨
logger.info("Processing SSN: {}", ssn);

// ✅ 로그에서 민감 정보 마스킹
logger.info("Processing SSN: {}", maskSensitiveData(ssn));
```

### 설정 파일 보안
```yaml
# ❌ 평문 비밀번호
email:
  password: "my-password"

# ✅ 환경변수 사용
email:
  password: "${EMAIL_PASSWORD}"
```

## 🔒 입력 검증

### 데이터 검증
```java
// 입력 데이터 검증
public void validateInput(String data) {
    if (data == null || data.trim().isEmpty()) {
        throw new IllegalArgumentException("데이터가 비어있습니다");
    }
    
    // SQL 인젝션 방지
    if (data.contains("'") || data.contains(";")) {
        throw new IllegalArgumentException("잘못된 문자가 포함되어 있습니다");
    }
}
```

### 파일 업로드 보안
```java
// 파일 확장자 검증
private static final Set<String> ALLOWED_EXTENSIONS = Set.of("txt", "csv", "json");

public void validateFile(String fileName) {
    String extension = getFileExtension(fileName);
    if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
        throw new SecurityException("허용되지 않는 파일 형식입니다");
    }
}
```

## 🚨 감사 로그 보안

### 감사 로그 무결성
```java
// 감사 로그 해시 검증
String auditLog = createAuditLog(field, before, after);
String hash = calculateHash(auditLog);
storeAuditLogWithHash(auditLog, hash);
```

### 감사 로그 암호화
```java
// 민감한 감사 로그 암호화 저장
String encryptedAuditLog = encryptAuditLog(auditLog, auditKey);
storeEncryptedAuditLog(encryptedAuditLog);
```

## 🔍 취약점 스캔

### 의존성 취약점 검사
```bash
# OWASP Dependency Check 실행
./gradlew dependencyCheckAnalyze

# 결과 확인
open build/reports/dependency-check-report.html
```

### 정기적인 보안 업데이트
```bash
# 의존성 업데이트 확인
./gradlew dependencyUpdates

# 보안 패치 적용
./gradlew build --refresh-dependencies
```

## 🛠️ 보안 설정 예시

### SSL/TLS 설정
```java
// 안전한 SSL 설정
System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");
System.setProperty("jdk.tls.client.protocols", "TLSv1.2,TLSv1.3");
```

### 암호화 알고리즘 설정
```java
// 안전한 암호화 알고리즘만 사용
CryptoUtil.setAllowedAlgorithms(Set.of("AES/GCM/NoPadding", "RSA/ECB/PKCS1Padding"));
```

## 📋 보안 체크리스트

- [ ] 암호화 키가 안전하게 관리되고 있는가?
- [ ] 민감 정보가 로그에 노출되지 않는가?
- [ ] 입력 데이터가 적절히 검증되는가?
- [ ] 최신 보안 패치가 적용되어 있는가?
- [ ] 감사 로그가 무결성을 보장하는가?
- [ ] SSL/TLS가 올바르게 설정되어 있는가?

## 🚨 보안 이슈 신고

보안 취약점을 발견하셨다면:

1. **즉시 공개하지 마세요**
2. **보안팀에 직접 연락하세요**
3. **상세한 재현 방법을 포함해주세요**

## 📚 추가 자료

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Java Security Best Practices](https://docs.oracle.com/javase/8/docs/technotes/guides/security/)
- [Cryptographic Standards](https://www.nist.gov/cryptography) 