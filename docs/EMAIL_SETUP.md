# 이메일 설정 가이드

## 1. 개발/테스트 환경 (GreenMail)

### 설정
```yaml
# audit-templates.yml
email:
  smtpHost:  "localhost"
  smtpPort:  3025
  from:      "from@test.com"
  to:        "to@test.com"
  username:  ""
  password:  ""
  starttls:  false
```

### 테스트 실행
```bash
# GreenMail을 사용한 테스트 (기본)
./gradlew test --tests FullPipelineIntegrationTest
```

## 2. 운영 환경 (실제 SMTP)

### 환경변수 설정
```bash
# Gmail 예시
export EMAIL_SMTP_HOST="smtp.gmail.com"
export EMAIL_SMTP_PORT="587"
export EMAIL_FROM="alerts@mycompany.com"
export EMAIL_TO="ops@mycompany.com"
export EMAIL_USERNAME="alerts@mycompany.com"
export EMAIL_PASSWORD="your-app-password"

# Naver 예시
export EMAIL_SMTP_HOST="smtp.naver.com"
export EMAIL_SMTP_PORT="587"
export EMAIL_FROM="alerts@mycompany.com"
export EMAIL_TO="ops@mycompany.com"
export EMAIL_USERNAME="your-naver-id"
export EMAIL_PASSWORD="your-naver-password"
```

### YAML 설정
```yaml
# audit-templates.yml
email:
  smtpHost:   "${EMAIL_SMTP_HOST}"
  smtpPort:   "${EMAIL_SMTP_PORT}"
  from:       "${EMAIL_FROM}"
  to:         "${EMAIL_TO}"
  username:   "${EMAIL_USERNAME}"
  password:   "${EMAIL_PASSWORD}"
  starttls:   true
```

### 실제 SMTP 테스트 실행
```bash
# 환경변수 설정 후 테스트 실행
./gradlew test --tests SmtpIntegrationTest
```

## 3. Gmail 설정 (앱 비밀번호)

### 1. 2단계 인증 활성화
1. Google 계정 설정 → 보안
2. 2단계 인증 활성화

### 2. 앱 비밀번호 생성
1. Google 계정 설정 → 보안 → 앱 비밀번호
2. "앱 선택" → "기타"
3. 앱 이름 입력 (예: "Masking Library")
4. 생성된 16자리 비밀번호 사용

### 3. 환경변수 설정
```bash
export EMAIL_USERNAME="your-email@gmail.com"
export EMAIL_PASSWORD="your-16-digit-app-password"
```

## 4. 테스트 결과 확인

### 성공 시
```
[TEST] 이메일 발송 완료
[TEST] 커스텀 설정으로 이메일 발송 완료
```

### 실패 시
- 환경변수 확인
- SMTP 서버 설정 확인
- 방화벽/네트워크 확인
- 앱 비밀번호 확인 (Gmail)

## 5. 운영 환경 배포 시 주의사항

### 보안
- 환경변수는 절대 코드에 하드코딩하지 않음
- `.env` 파일은 `.gitignore`에 포함
- 앱 비밀번호는 정기적으로 갱신

### 모니터링
- 이메일 발송 실패 시 알림 설정
- 발송량 모니터링 (SMTP 서버 제한)
- 스팸 필터링 고려

### 백업
- 이메일 발송 실패 시 대체 알림 방법 준비
- Slack, SMS 등 다중 채널 구성 