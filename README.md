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

## 2. 사용 예시

### 2.1 단일 Action 호출
```java
// 마스킹만 수행
Action mask = MaskAction.of("email", RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*'));
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

> **Q:** `MaskAction` 하나만 선택해도 되나요?  
> **A:** 네, `MaskAction` 인스턴스 하나로 **마스킹만** 실행 가능합니다.

---

## 3. 주요 보호 전략

### 3.1 마스킹(Mask)
- **PartialMaskStrategy**: 앞/뒤 일정 글자만 남기고 마스킹  
- **RegexMaskStrategy**: 정규표현식 기반 세밀 마스킹  
- **CharClassMaskStrategy**: 문자 클래스별(영문·숫자·한글·공백) 마스킹

```java
// 예: 이메일 로컬 파트 첫 글자 제외 모두 마스킹
Action mask = MaskAction.of(
  "email",
  RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*')
);
```

| 전략       | 설명                            | 예시 코드                                                         |
|-----------|---------------------------------|------------------------------------------------------------------|
| Partial   | 앞/뒤 N글자 제외 마스킹          | `PartialMaskStrategy.of(2,2,'*')`                                 |
| Regex     | 패턴 기반 마스킹                  | `RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*')`               |
| CharClass | 문자 종류별(숫자·영문 등) 마스킹  | `CharClassMaskStrategy.of(EnumSet.of(CharClass.LETTER), '*')`   |

### 3.2 토큰화(Tokenize)
- **UUIDTokenizationStrategy**: UUID 치환  
- **HashTokenizationStrategy**: SHA-256 + salt  
- **NumericTokenizationStrategy**: 고정 길이 숫자 코드

### 3.3 암호화(Encrypt)
- **AES**: CBC/PKCS5Padding + Base64  
- **RSA**: 공개키 암호화 + Base64

### 3.4 감사로그(Audit)
- **AuditEventHandler**: 콘솔, DB, Slack, Email 등  
- **AuditAction**: `before`·`after` 이벤트 호출

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
  implementation("org.slf4j:slf4j-api:1.7.36")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4")
  testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}
```

```
com.masking
├─ action       # Action, MaskAction, TokenizeAction, EncryptAction, Actions
├─ strategy     # MaskStrategy 등 다양한 전략 인터페이스/구현
├─ audit        # AuditAction, AuditEventHandler 등
└─ util         # CryptoUtil, YamlLoader
```

---

## 5. 빠른 시작
```groovy
dependencies {
  implementation 'com.masking:masking-library-dsl:0.1.0'
}
```

```java
// 마스킹만
MaskAction.of("email", PartialMaskStrategy.of(2,2,'*')).apply(record);

// 마스킹+암호화
Actions.of(
  MaskAction.of("email", policy),
  EncryptAction.of("ssn", aesStrategy)
).apply(record);
```

---

**기여 환영(Fork & PR)!**
