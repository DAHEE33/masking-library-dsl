## 📄 라이선스
Apache-2.0 © Your Name

---

## 1. 개요 & 필요성
Action 기반으로 **필요한 기능만 선택하여** 사용할 수 있는 **데이터 보호 라이브러리**입니다.  
단일 Action 호출부터 복합 구성, 전역 설정, 플러그인 확장까지 **실무 적용에 유연**하고 **직관적**입니다.

> **왜 Action인가?**
> - `MaskAction.apply(record)`처럼 **단일 Action**을 직접 실행하거나  
> - `Actions.of(a, b).apply(record)`로 **여러 Action**을 한 번에 묶어 실행

---

## 2. 빠른 시작 & 사용 예시

### 2.1 단일 Action 호출
```java
Action mask = MaskAction.of(
  "email",
  RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*')
);
mask.apply(record);
```

### 2.2 복합 Action 조합
```java
Actions actions = Actions.of(
  MaskAction.of("email", policy),
  EncryptAction.of("ssn", aesStrategy)
);
actions.apply(record);
```

### 2.3 파이프라인 빌더(Pipeline Builder)
```java
MaskPipeline pipeline = MaskPipelineBuilder.newBuilder()
  .audit("email", slackHandler)
  .mask("email", RegexMaskStrategy.of(pattern, '*'))
  .tokenize("username", UUIDTokenizationStrategy.of())
  .encryptAes("ssn", aesKey)
  .build();
pipeline.apply(record);
```

### 2.4 선택적 감사 알림
```java
// 특정 채널만 활성화
AuditConfig.enableChannel(AuditConfig.AuditChannel.SLACK);
AuditConfig.disableChannel(AuditConfig.AuditChannel.EMAIL);

// 복합 감사 핸들러 사용
CompositeAuditEventHandler auditHandler = new CompositeAuditEventHandler();
auditHandler.handle("email", "original@example.com", "m****@example.com");
```

### 2.5 확장 가능한 Action (SPI)
```java
// 사용자 정의 Action 등록
// META-INF/services/com.masking.spi.ActionProvider 파일에 등록
// com.example.CustomActionProvider

// 사용
Action customAction = ActionRegistry.createAction("custom", config);
```

> **유연성**: 원하는 Action/Step만 순서대로 조립해 실행 가능

---

## 3. 주요 보호 전략

### 3.1 마스킹(Mask)
- **PartialMaskStrategy**: 앞/뒤 N글자 제외 마스킹  
- **RegexMaskStrategy**: 패턴 기반 세밀 마스킹  
- **CharClassMaskStrategy**: 문자 클래스별(영문·숫자·한글·공백) 마스킹

```java
// 이메일 로컬 파트 첫 글자 제외 마스킹
Action mask = MaskAction.of(
  "email",
  RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*')
);
```

| 전략       | 설명                         | 예시 코드                                                        |
|-----------|-----------------------------|-----------------------------------------------------------------|
| Partial   | 앞/뒤 N글자 제외 마스킹       | `PartialMaskStrategy.of(2,2,'*')`                                |
| Regex     | 패턴 기반 마스킹              | `RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*')`            |
| CharClass | 클래스별(문자·숫자·한글 등) 마스킹 | `CharClassMaskStrategy.of(EnumSet.of(CharClass.LETTER), '*')`|

### 3.2 토큰화(Tokenize)
- **UUIDTokenizationStrategy**: UUID 치환  
- **HashTokenizationStrategy**: SHA-256 + salt  
- **NumericTokenizationStrategy**: 고정 길이 숫자 코드

### 3.3 암호화(Encrypt)
- **AES**: CBC/PKCS5Padding + Base64  
- **RSA**: 공개키 암호화 + Base64

### 3.4 감사로그(Audit)
- **AuditEventHandler**: 콘솔, DB, Slack, Email, Kafka 등 다양한 핸들러 제공
- **TemplateConfig & YAML**: `audit-templates.yml`을 통해 Slack/Webhook, Email, DB 설정을 외부화
- **AuditAction**: `before`·`after` 값과 필드명을 `handle(field, before, after)`로 전달
- **선택적 채널**: `AuditConfig`를 통해 원하는 감사 채널만 활성화

```yaml
# audit-templates.yml 예시
slack:
  webhook_url: "https://hooks.slack.com/..."
  channel:     "#alerts"
  message:     "🔔 *${field}* 변경: 이전=${before}, 이후=${after}"
email:
  smtpHost:  "smtp.gmail.com"
  smtpPort:  587
  from:      "alerts@mycompany.com"
  to:        "ops@mycompany.com"
  username:  "${EMAIL_USER}"
  password:  "${EMAIL_PASSWORD}"
  starttls:  true
database:
  url: "jdbc:h2:mem:auditdb"
  username: "sa"
  password: ""
  table: "audit_logs"
```

---

## ⚙️ 환경변수(Secrets) 설정 및 CI/CD 연동

### 1. 이메일/감사 설정 환경변수

`audit-templates.yml`에서 아래와 같이 환경변수로 값을 지정하세요:

```yaml
email:
  smtpHost:   "${EMAIL_SMTP_HOST}"
  smtpPort:   "${EMAIL_SMTP_PORT}"
  from:       "${EMAIL_FROM}"
  to:         "${EMAIL_TO}"
  username:   "${EMAIL_USERNAME}"
  password:   "${EMAIL_PASSWORD}"
  starttls:   true
```

### 2. GitHub Secrets 등록 예시

GitHub 저장소 > Settings > Secrets and variables > Actions에서 아래와 같이 등록하세요:

- `EMAIL_SMTP_HOST`
- `EMAIL_SMTP_PORT`
- `EMAIL_FROM`
- `EMAIL_TO`
- `EMAIL_USERNAME`
- `EMAIL_PASSWORD`

### 3. CI/CD 환경변수 주입 예시

`.github/workflows/ci.yml`에서 아래처럼 환경변수를 주입합니다:

```yaml
env:
  GITHUB_TOKEN: ${{ secrets.MY_TOKEN }}
  EMAIL_SMTP_HOST: ${{ secrets.EMAIL_SMTP_HOST }}
  EMAIL_SMTP_PORT: ${{ secrets.EMAIL_SMTP_PORT }}
  EMAIL_FROM: ${{ secrets.EMAIL_FROM }}
  EMAIL_TO: ${{ secrets.EMAIL_TO }}
  EMAIL_USERNAME: ${{ secrets.EMAIL_USERNAME }}
  EMAIL_PASSWORD: ${{ secrets.EMAIL_PASSWORD }}
```

### 4. 배포 시 주의사항

- 이미 같은 버전이 업로드된 경우(409 Conflict),  
  `build.gradle.kts`에서 버전을 올려서 재배포해야 합니다.

---

## 4. 개발 환경 & 패키지 구조
- **Java 8** 호환 (source/target 1.8)  
- **Gradle** (Kotlin DSL)  
- **최소 의존성**: SLF4J, Jackson Databind, JUnit5

```kotlin
java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}
dependencies {
  // SLF4J + Jackson + JUnit
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")

    // H2 데이터베이스 (in-memory)
    implementation("com.h2database:h2:2.1.214")
    // 간단한 DataSource 풀 (선택)
    implementation("com.zaxxer:HikariCP:5.0.1")

    // JavaMail (이메일 전송)
    implementation("com.sun.mail:javax.mail:1.6.2")

    testImplementation("com.github.tomakehurst:wiremock-jre8:2.35.0") 
    implementation("com.icegreen:greenmail:1.6.3") // 데모용 GreenMail
    testImplementation("com.icegreen:greenmail:1.6.3")
}
```

```
com.masking
├─ action       # Action, MaskAction, TokenizeAction, EncryptAction, Actions
├─ strategy     # MaskStrategy, TokenizationStrategy 등
│   ├─ encrypt  # AES, RSA 구현체
│   ├─ mask     # Partial, Regex, CharClass 전략
│   └─ tokenize # UUID, Hash, Numeric 전략
├─ pipeline     # MaskPipeline, MaskPipelineBuilder
├─ audit        # AuditAction, AuditEventHandler, handlers (Slack, Email, DB, Kafka)
├─ config       # AuditTemplates, EmailConfig, TemplateConfig, YamlLoader, AuditConfig
├─ extension    # JacksonModule, KafkaAuditEventHandler
├─ spi          # ActionProvider, ActionRegistry (확장성)
├─ demo         # Demo 애플리케이션
├─ util         # CryptoUtil, YamlLoader
└─ performance  # 성능 테스트 및 벤치마크
```

---

## 5. 빌드 & 배포

### 5.1 로컬 빌드
```bash
# 테스트 실행
./gradlew test

# JAR 빌드
./gradlew build

# Javadoc 생성
./gradlew javadoc

# 보안 스캔
./gradlew dependencyCheckAnalyze
```

### 5.2 CI/CD 파이프라인
- **GitHub Actions**: 자동 테스트, 보안 스캔, 배포
- **다중 Java 버전**: Java 8, 11, 17 지원
- **자동 릴리즈**: 태그 생성 시 자동 배포

### 5.3 Maven Central 배포
```bash
# 배포 (환경변수 설정 필요)
./gradlew publish
```

---

## 6. 성능 & 보안

### 6.1 성능 벤치마크
- **단일 Action**: 10,000 레코드/초 이상
- **복합 Action**: 5,000 레코드/초 이상
- **메모리 사용량**: 100MB 이하
- **동시성**: 10 스레드 동시 처리 지원

### 6.2 보안 기능
- **OWASP Dependency Check**: 취약점 자동 스캔
- **암호화 키 관리**: 환경변수 기반 안전한 키 관리
- **입력 검증**: SQL 인젝션, XSS 방지
- **감사 로그**: 무결성 검증 및 암호화 저장

자세한 내용은 [보안 가이드](docs/SECURITY.md)를 참조하세요.

---

## 7. 확장성

### 7.1 사용자 정의 Action
```java
// 1. ActionProvider 구현
public class CustomActionProvider implements ActionProvider {
    @Override
    public String getActionName() {
        return "custom";
    }
    
    @Override
    public Action createAction(Map<String, Object> config) {
        return new CustomAction(config);
    }
}

// 2. META-INF/services/com.masking.spi.ActionProvider에 등록
// com.example.CustomActionProvider

// 3. 사용
Action customAction = ActionRegistry.createAction("custom", config);
```

### 7.2 Jackson 모듈 확장
```java
// Action을 JSON으로 직렬화/역직렬화
ObjectMapper mapper = JacksonModule.createObjectMapper();
String json = mapper.writeValueAsString(action);
Action action = mapper.readValue(json, Action.class);
```

### 7.3 Kafka 이벤트 핸들러
```java
// Kafka로 감사 이벤트 전송
KafkaAuditEventHandler kafkaHandler = 
    new KafkaAuditEventHandler("audit-topic", "localhost:9092");
kafkaHandler.handle("email", "original", "masked");
```

---

## 8. 테스트

### 8.1 단위 테스트
```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트만 실행
./gradlew test --tests PerformanceTest
```

### 8.2 통합 테스트
- **SMTP 연동 테스트**: 실제 이메일 서버 연동
- **데이터베이스 테스트**: H2 인메모리 DB 사용
- **Slack 연동 테스트**: WireMock을 통한 모킹

### 8.3 성능 테스트
```bash
# 성능 테스트 실행
./gradlew test --tests PerformanceTest
```

---

## 9. 문서화

### 9.1 API 문서
```bash
# Javadoc 생성
./gradlew javadoc

# 생성된 문서 확인
open build/docs/javadoc/index.html
```

### 9.2 사용 예제
- [Demo 애플리케이션](src/main/java/com/masking/demo/Demo.java)
- [통합 테스트](src/test/java/com/masking/integration/)
- [성능 테스트](src/test/java/com/masking/performance/)

---

## 10. 기여하기

### 10.1 개발 환경 설정
1. 프로젝트 클론
2. IDE에서 Gradle 프로젝트로 열기
3. 테스트 실행으로 환경 확인

### 10.2 기여 프로세스
1. Fork & Clone
2. Feature 브랜치 생성
3. 개발 및 테스트
4. Pull Request 생성

### 10.3 코딩 컨벤션
- **Java**: Google Java Style Guide 준수
- **Javadoc**: 모든 public API에 문서화
- **테스트**: 새로운 기능마다 테스트 코드 작성

---

## 11. 라이선스

Apache License 2.0 - 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

---

**기여 환영(Fork & PR)!**

## 📊 프로젝트 상태

![Java](https://img.shields.io/badge/Java-8+-orange.svg)
![Gradle](https://img.shields.io/badge/Gradle-7.0+-green.svg)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)
![Tests](https://img.shields.io/badge/Tests-Passing-brightgreen.svg)
