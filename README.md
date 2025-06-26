## 📄 라이선스
이 프로젝트는 [Apache-2.0](LICENSE) 라이선스 하에 배포됩니다.

## 📌 개요

**Masking Pipeline DSL**은 Java 애플리케이션에서 다루는 민감 데이터를  
“**마스킹 → 토큰화 → 암호화 → 감사 로그**” 순으로 처리하는  
도메인 특화(Domain-Specific) Language 기반 파이프라인 엔진입니다.  

- ❓ **왜 만들었나?**  
  - 개인정보, 신용카드 번호, 주민등록번호 등 민감 데이터를 매번 손수 처리하는 과정에서  
    *반복 코드*와 *실수 위험*이 컸습니다.  
  - DSL 선언만으로 처리 흐름 전체를 자동화해,  
    **생산성 향상**과 **보안 강화**를 동시에 달성합니다.

---

## ⚙️ 핵심 기능

| Step                  | 설명                                                            |
|-----------------------|-----------------------------------------------------------------|
| `MaskFieldStep`       | 문자열 필드 일부를 `*` 등으로 마스킹                            |
| `TokenizeFieldStep`   | UUID / 해시 / 난수 등으로 고유 식별자를 치환                    |
| `EncryptFieldStep`    | AES / RSA 암호화 (Base64 인코딩)                                |
| `AuditLogStep`        | 처리 전·후 상태를 콘솔·SLF4J·Webhook으로 기록                  |
| **유연한 조합**       | 필요한 Step만 골라서 원하는 순서로 DSL 선언 → `.apply(record) ` |

---

### ⚙️ 개발 환경
- **Language**: Java 17  
- **Build Tool**: Gradle (Kotlin DSL)  
- **Dependencies**:
  - SLF4J API (`org.slf4j:slf4j-api:1.7.36`)
  - SnakeYAML (`org.yaml:snakeyaml:1.33`) — *YAML 설정 지원*
  - JUnit Jupiter (`org.junit.jupiter:junit-jupiter:5.9.2`) — *테스트 프레임워크*
- **Gradle 설정** (`build.gradle.kts`):
  ```kotlin
  plugins {
      `java-library`
      `maven-publish`
  }

  group = "com.masking"
  version = "0.1.0"

  java {
      toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
  }

  repositories { mavenCentral() }

  dependencies {
      implementation("org.slf4j:slf4j-api:1.7.36")
      implementation("org.yaml:snakeyaml:1.33")
      testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
  }

  tasks.test {
      useJUnitPlatform()
  }

  ```

---  


### 패키지별 역할

- **`com.masking.pipeline`**  
  파이프라인 전체 흐름 관리: `Step` 목록을 순차 실행하고 결과를 반환하는 핵심 엔진.

- **`com.masking.step`**  
  데이터 처리의 개별 단위(단계)를 구현. 각 클래스가 한 가지 책임(마스킹, 토큰화, 암호화, 감사 로그)을 담당.

- **`com.masking.strategy`**  
  알고리즘 세부 구현(전략 패턴) 모음. `Step` 에 주입되어 다양한 처리 방식을 플러그인처럼 교체 가능.

- **`com.masking.util`**  
  파이프라인 전반에서 재사용되는 도우미 기능. 암호화 헬퍼, 설정 파일 파싱 등 보조 역할.

- **`src/test/java`**  
  단위 테스트 전용 디렉터리. JUnit5 기반으로 핵심 로직의 정확성을 검증.

---

## 🛠️ 설치

### Gradle
```groovy
implementation 'com.masking:masking-pipeline-dsl:0.1.0'


