# 이메일 설정 가이드

## 개요

실제 운영 환경에서 Gmail, Naver 등의 SMTP 서비스를 사용하는 방법을 안내합니다.

## 🔐 보안 원칙

**절대 하드코딩하지 마세요!**
- ❌ `password: "my-password"`
- ✅ `password: "${EMAIL_PASSWORD}"`

## 📧 Gmail 설정

### 1. Gmail 앱 비밀번호 생성
1. Google 계정 설정 → 보안
2. 2단계 인증 활성화
3. 앱 비밀번호 생성 (16자리)
4. 생성된 앱 비밀번호를 환경변수로 설정

### 2. 환경변수 설정
```bash
# Linux/Mac
export EMAIL_FROM="alerts@mycompany.com"
export EMAIL_TO="ops@mycompany.com"
export EMAIL_USERNAME="alerts@mycompany.com"
export EMAIL_PASSWORD="your-16-digit-app-password"

# Windows
set EMAIL_FROM=alerts@mycompany.com
set EMAIL_TO=ops@mycompany.com
set EMAIL_USERNAME=alerts@mycompany.com
set EMAIL_PASSWORD=your-16-digit-app-password
```

### 3. 설정 파일 활성화
`audit-templates.yml`에서 Gmail 설정 주석 해제:
```yaml
email:
  smtpHost:   "smtp.gmail.com"
  smtpPort:   587
  from:       "${EMAIL_FROM}"
  to:         "${EMAIL_TO}"
  username:   "${EMAIL_USERNAME}"
  password:   "${EMAIL_PASSWORD}"
  starttls:   true
```

## 📧 Naver 설정

### 1. Naver 메일 설정
1. Naver 계정 → 메일 설정
2. POP3/SMTP 사용 설정
3. 보안 메일 설정에서 SMTP 사용 허용

### 2. 환경변수 설정
```bash
export EMAIL_FROM="alerts@mycompany.com"
export EMAIL_TO="ops@mycompany.com"
export EMAIL_USERNAME="your-naver-id"
export EMAIL_PASSWORD="your-naver-password"
```

### 3. 설정 파일 활성화
`audit-templates.yml`에서 Naver 설정 주석 해제:
```yaml
email:
  smtpHost:   "smtp.naver.com"
  smtpPort:   587
  from:       "${EMAIL_FROM}"
  to:         "${EMAIL_TO}"
  username:   "${EMAIL_USERNAME}"
  password:   "${EMAIL_PASSWORD}"
  starttls:   true
```

## 🐳 Docker 환경에서 사용

### Docker Compose 예시
```yaml
version: '3.8'
services:
  masking-app:
    image: masking-library:latest
    environment:
      - EMAIL_FROM=alerts@mycompany.com
      - EMAIL_TO=ops@mycompany.com
      - EMAIL_USERNAME=alerts@mycompany.com
      - EMAIL_PASSWORD=${EMAIL_PASSWORD}
    env_file:
      - .env
```

### .env 파일 (Git에 커밋하지 않음)
```env
EMAIL_PASSWORD=your-secure-password
```

## ☁️ 클라우드 환경에서 사용

### AWS ECS/Fargate
```json
{
  "environment": [
    {
      "name": "EMAIL_PASSWORD",
      "value": "arn:aws:secretsmanager:region:account:secret:email-password"
    }
  ]
}
```

### Kubernetes
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: email-secret
type: Opaque
data:
  email-password: <base64-encoded-password>
---
apiVersion: apps/v1
kind: Deployment
spec:
  template:
    spec:
      containers:
      - name: masking-app
        env:
        - name: EMAIL_PASSWORD
          valueFrom:
            secretKeyRef:
              name: email-secret
              key: email-password
```

## 🔍 테스트 방법

### 1. 환경변수 확인
```bash
echo $EMAIL_USERNAME
echo $EMAIL_PASSWORD
```

### 2. SMTP 연결 테스트
```bash
# 테스트 실행
./gradlew test --tests SmtpIntegrationTest
```

### 3. 실제 이메일 전송 테스트
```java
// Demo.java에서 테스트
EmailAuditEventHandler emailHandler = new EmailAuditEventHandler();
emailHandler.handle("test", "original", "masked");
```

## 🚨 주의사항

1. **환경변수 파일(.env)을 Git에 커밋하지 마세요**
2. **CI/CD에서는 시크릿 관리 시스템 사용**
3. **정기적으로 비밀번호 변경**
4. **로그에 비밀번호가 노출되지 않도록 주의**

## 📋 체크리스트

- [ ] 2단계 인증 활성화 (Gmail)
- [ ] 앱 비밀번호 생성 (Gmail)
- [ ] SMTP 사용 허용 (Naver)
- [ ] 환경변수 설정
- [ ] .env 파일을 .gitignore에 추가
- [ ] SMTP 연결 테스트
- [ ] 실제 이메일 전송 테스트

## 🔧 문제 해결

### Gmail 오류
- **535 Authentication failed**: 앱 비밀번호 확인
- **550 Relaying not allowed**: 발신자 이메일 주소 확인

### Naver 오류
- **535 Authentication failed**: SMTP 사용 설정 확인
- **550 Invalid sender**: 발신자 이메일 주소 확인 