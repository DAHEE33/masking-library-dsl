package com.masking.audit;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * JavaMail을 사용하여 이메일로 감사로그를 전송합니다.
 */
public class EmailAuditEventHandler implements AuditEventHandler {
    private final Session session;
    private final String from;
    private final String to;

    public EmailAuditEventHandler(String smtpHost, int smtpPort, String from, String to,
                                  final String username, final String password) {
        this.from = from;
        this.to = to;
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", String.valueOf(smtpPort));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    @Override
    public void handle(String field, String before, String after) {
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject("[AUDIT] field=" + field);
            msg.setText("before: " + before + "\nafter: " + after);
            Transport.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException("Email 감사 전송 실패", e);
        }
    }
}

