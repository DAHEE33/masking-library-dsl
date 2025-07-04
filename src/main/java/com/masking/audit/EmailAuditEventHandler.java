package com.masking.audit;

import com.masking.config.AuditTemplates;
import com.masking.config.EmailConfig;
import com.masking.config.TemplateConfig;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

/**
 * JavaMail을 사용하여 이메일로 감사로그를 전송합니다.
 * 테스트 환경(GreenMail)에서는 인증 없이도 작동하도록 구성합니다.
 */
public class EmailAuditEventHandler implements AuditEventHandler {
    private final Session session;
    private final EmailConfig emailCfg;

    public EmailAuditEventHandler() {
        // 1) 템플릿 로드 및 email 설정 추출
        TemplateConfig.init();
        this.emailCfg = TemplateConfig.getEmailConfig();

        // 2) SMTP 프로퍼티 구성
        Properties props = new Properties();
        props.put("mail.smtp.host",       emailCfg.smtpHost);
        props.put("mail.smtp.port",       String.valueOf(emailCfg.smtpPort));
        props.put("mail.smtp.auth",       String.valueOf(emailCfg.username != null && !emailCfg.username.isEmpty()));
        props.put("mail.smtp.starttls.enable", String.valueOf(emailCfg.starttls));

        // 3) 인증 유무에 따른 세션 생성
        if (emailCfg.username != null && !emailCfg.username.isEmpty()) {
            this.session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailCfg.username, emailCfg.password);
                }
            });
        } else {
            this.session = Session.getInstance(props);
        }
    }

    @Override
    public void handle(String field, String before, String after) {
        try {
            // 4) 메일 메시지 생성 및 설정
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(emailCfg.from));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailCfg.to));
            msg.setSubject("[AUDIT] field=" + field);

            String body = new StringBuilder()
                    .append("before: ").append(before).append("\n")
                    .append("after: ").append(after)
                    .toString();
            msg.setText(body);

            // 5) 메일 전송
            Transport.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException("Email 감사 전송 실패", e);
        }
    }
}