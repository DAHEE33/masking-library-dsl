## 📄 라이선스
Apache-2.0 © Your Name

---

## 1. 개요 & 필요성
기존 “파이프라인” 개념은 여러 단계를 일괄 처리할 때 유용했지만, 실제 현업에서는 **“마스킹만”, “암호화만”** 같은 **단계별 호출**이 더 자주 필요합니다.

**왜 ‘파이프라인’이라 부르는가?**
- 순차적 흐름(예: 마스킹→토큰화→암호화)을 **조합**해서도, 개별 단계만 **단독 실행**해도 쓰기 때문에 ‘파이프라인’ 엔진이라 칭합니다.
- `Actions.of(a,b,c).apply(record)` 시 **등록 순서대로 자동 실행**되는 메커니즘에 착안한 명칭입니다.

**단계별 사용 필요성**
- 민감 데이터 보호 수준이 상황마다 다릅니다:
  - **로그 저장**: 마스킹만 → 데이터 유출 최소화
  - **외부 전송**: 암호화만 → 성능 고려
  - **전체 보호(Use Case)**: "데이터 분석" 또는 "보안 감사" 같은 시나리오에서 사용하며, 
```
// 예: 대규모 로그 데이터를 외부 분석 서비스에 안전하게 제공할 때
Actions.of(
  MaskAction.of("email", policy),         // 민감 식별자 일부 마스킹
  TokenizeAction.of("userId", uuidStrategy), // 사용자 ID 토큰화
  EncryptAction.of("ssn", aesStrategy)      // 완전 암호화
).apply(record);
```

**단일** Action 호출**(`MaskAction.apply(record)`)으로 필요한 단계만 골라 쓰면, 성능과 유지보수 모두 최적화됩니다.

---

## 2. 사용 예시: Action vs Actions

### 단일 Action 호출
```java
// 예: 대규모 로그 데이터를 외부 분석 서비스에 안전하게 제공할 때
Actions.of(
  MaskAction.of("email", policy),         // 민감 식별자 일부 마스킹
  TokenizeAction.of("userId", uuidStrategy), // 사용자 ID 토큰화
  EncryptAction.of("ssn", aesStrategy)      // 완전 암호화
).apply(record);
```

### 복합 단계 조합
```java
Actions pipeline = Actions.of(
  MaskAction.of("email", policy),
  EncryptAction.of("ssn", aesStrategy),
  AuditAction.of("userData", List.of(consoleHandler))
);
pipeline.apply(record);  // 순차 실행
```

> **Q:** 하나의 객체 안에서 `action(mask)` 하면 마스킹만 되나요?  
> **A:** 네, `MaskAction` 인스턴스만 생성해 `apply()` 하면 해당 필드만 처리합니다.

---

## 3. 주요 보호 전략 (예시)

### 3.1 마스킹(Mask)
- **PartialMaskStrategy**: 문자열의 **앞/뒤 일정 글자만 남기고** 나머지를 마스킹합니다.
  - *적용 예시*: 고정 길이 형태(계좌번호, 짧은 코드 등)에 유용합니다.
  - `PartialMaskStrategy.of(2, 2, '*')` → `ab******yz`

- **RegexMaskStrategy**: **정규표현식**을 사용해 원하는 패턴만 마스킹합니다.
  - *적용 예시*: 이메일, 전화번호처럼 구조가 복잡할 때
  - 이메일 로컬 파트(‘@’ 앞 부분) 중 첫 글자를 제외한 나머지 문자만 마스킹하려면:
    ```java
    RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*')
    ```

- **CharClassMaskStrategy**: 문자 **분류별**(영문, 숫자, 한글, 공백 등)로 일괄 마스킹합니다.
  - *적용 예시*: 모든 숫자와 영문 대소문자를 마스킹하려면:
    ```java
    CharClassMaskStrategy.of(
      EnumSet.of(CharClass.LETTER, CharClass.DIGIT), '*'
    )
    ```

```java
// Partial (앞/뒤 2글자만 남기고 마스크)
MaskAction.of("userId", PartialMaskStrategy.of(2,2,'*'))

// Regex (이메일 @ 앞 1글자 제외 모두 마스크)
MaskAction.of("email",
  RegexMaskStrategy.of("(?<=.).(?=[^@]+@)", '*')
)

// CharClass (숫자, 영문자만 마스크)
MaskAction.of("phone",
  CharClassMaskStrategy.of(
    EnumSet.of(CharClass.DIGIT, CharClass.LETTER), '*'
  )
)
```
  <thead>
    <tr><th>전략</th><th>설명</th><th>예시 코드</th></tr>
  </thead>
  <tbody>
    <tr>
      <td>Partial</td>
      <td>앞/뒤 n글자 남김 (일관된 길이에 유용)</td>
      <td><code>PartialMaskStrategy.of(2,2,'*')</code></td>
    </tr>
    <tr>
      <td>Regex</td>
      <td>패턴 기반 마스킹 (예: 이메일 로컬 파트 제외 코드)</td>
      <td><code>RegexMaskStrategy.of("(?<=.{1}).(?=[^@]+@)", '*')</code></td>
    </tr>
    <tr>
      <td>CharClass</td>
      <td>문자 클래스별 마스킹</td>
      <td><code>CharClassMaskStrategy.of(EnumSet.of(LETTER, DIGIT), '*')</code></td>
    </tr>
  </tbody>
</table>

### 3.1.1 이름(Name) 마스킹 예시
```java
// 1) 단일 파트 이름 (adgbjsgg): 첫 글자만 남기고 나머지는 마스킹
MaskAction.of(
  "name",
  RegexMaskStrategy.of("(?<=^.).","*")
).apply(record);  // a*******

// 2) 복합 파트 이름 (kim da hee): 각 단어의 첫 글자만 남김
MaskAction.of(
  "name",
  RegexMaskStrategy.of("(?<=\b.).","*")
).apply(record);  // k** d* h**

// 3) 임의 문자열 (sudif enjwkgnjwf): 공백 기준 단어별 첫 글자만 남김
MaskAction.of(
  "name",
  RegexMaskStrategy.of("(?<=\b.).","*")
).apply(record);  // s**** e***********

// 4) 클래스 기반: 영문자·한글자만 마스킹
MaskAction.of(
  "name",
  CharClassMaskStrategy.of(
    EnumSet.of(CharClass.LETTER, CharClass.HANGUL), '*'
  )
).apply(record);
```
  <thead>
    <tr><th>전략</th><th>설명</th><th>예시 코드</th></tr>
  </thead>
  <tbody>
    <tr>
      <td>Partial</td>
      <td>앞/뒤 n글자 남김</td>
      <td><code>PartialMaskStrategy.of(2,2,'*')</code></td>
    </tr>
    <tr>
      <td>Regex</td>
      <td>패턴 기준 마스킹</td>
      <td><code>RegexMaskStrategy.of("\d(?=\d{4})", '*')</code></td>
    </tr>
    <tr>
      <td>CharClass</td>
      <td>문자 클래스별 마스킹</td>
      <td><code>CharClassMaskStrategy.of(setOf(LETTER, DIGIT), '*')</code></td>
    </tr>
  </tbody>
</table>

```java
MaskAction.of(
  "phone", RegexMaskStrategy.of("\\d(?=\\d{4})", '*')
);
```

### 3.2 토큰화(Tokenize)
- **UUIDTokenizationStrategy**: UUID로 무작위 치환
- **HashTokenizationStrategy**: SHA-256 + salt 해시
- **NumericTokenizationStrategy**: 고정 길이 숫자 코드

### 3.3 암호화(Encrypt)
- **AES**(대칭키): CBC/PKCS5Padding + Base64 인코딩
- **RSA**(비대칭키): 공개키 암호화 + Base64

### 3.4 감사로그(Audit)
- **AuditEventHandler**: 콘솔, DB, Slack, Email 등 다채널 지원
- **AuditAction**: `before`/`after` 이벤트 생성 및 핸들러 호출

---

## 4. 개발 환경 & 패키지
- **Java**: 8 호환 (source/target 1.8)
- **Gradle**: Kotlin DSL
- **Jackson**: 기존 `ObjectMapper` 후크로 확장 (기본 모듈만 등록)

```kotlin
java { sourceCompatibility = JavaVersion.VERSION_1_8 }
dependencies {
  implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4")
}
```

```text
com.masking
├─ action       # MaskAction, TokenizeAction, EncryptAction, Actions
├─ strategy     # MaskStrategy, PartialMaskStrategy, RegexMaskStrategy, ...
├─ jackson      # ObjectMapperCustomizer, DefaultMaskingModule
├─ audit        # AuditEvent, AuditAction, AuditEventHandler implementations
└─ util         # CryptoUtil, YamlLoader
```

---

## 5. 빠른 시작
```groovy
dependencies {
  implementation 'com.masking:masking-pipeline-dsl:0.1.0'
}
```

```java
// 단일 마스킹
MaskAction.of("email", PartialMaskStrategy.of(2,2,'*')).apply(record);

// 전체 조합
Actions.of(
  MaskAction.of("email", policy),
  EncryptAction.of("ssn", aesStrategy),
  AuditAction.of("userData", List.of(new ConsoleAuditEventHandler()))
).apply(record);
```

---

**Fork & PR** 환영합니다!```
