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
- **AuditEventHandler**: 콘솔, DB, Slack, Email 등 다양한 핸들러 제공
- **TemplateConfig & YAML**: `audit-templates.yml`을 통해 Slack/Webhook, Email, DB 설정을 외부화
- **AuditAction**: `before`·`after` 값과 필드명을 `handle(field, before, after)`로 전달

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
```

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
├─ audit        # AuditAction, AuditEventHandler, handlers (Slack, Email, DB)
├─ config       # AuditTemplates, EmailConfig, TemplateConfig, YamlLoader
├─ demo         # Demo 애플리케이션
└─ util         # CryptoUtil, YamlLoader

```

---
## 5. 추가 할 리스트
1. **운영 SMTP 환경** 구성 및 테스트  
2. **이메일, DB, Slack** 중 선택적 감사 알림 지원  
3. **Step/Action 확장 로직** (Jackson 모듈, Kafka 이벤트 핸들러 벤치마크)


---

**기여 환영(Fork & PR)!**
