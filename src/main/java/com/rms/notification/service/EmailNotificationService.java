package com.rms.notification.service;

import com.rms.notification.dto.Email;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.enable:true}")
    private boolean emailEnabled;

    @Value("${app.email.default-from:noreply@example.com}")
    private String defaultFrom;

    public void send(Email message) {
        if (message == null) {
            throw new IllegalArgumentException("Email notification message is null.");
        }

        if (!emailEnabled) {
            log.info("Email sending is disabled. Skipping email to {}", message.getTo());
            return;
        }

        if (!StringUtils.hasText(message.getTo())) {
            throw new IllegalArgumentException("Email notification missing recipient.");
        }

        if (message.isHtml()) {
            sendHtml(message);
        } else {
            sendPlainText(message);
        }
    }

    private void sendPlainText(Email message) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(message.getTo());
        mail.setFrom(resolveFrom(message.getFrom()));
        mail.setSubject(message.getSubject());
        mail.setText(message.getBody());
        mailSender.send(mail);
        log.info("Plain text email sent to {}", message.getTo());
    }

    private void sendHtml(Email message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(message.getTo());
            helper.setFrom(resolveFrom(message.getFrom()));
            helper.setSubject(message.getSubject());
            helper.setText(message.getBody(), true);
            mailSender.send(mimeMessage);
            log.info("HTML email sent to {}", message.getTo());
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to {}", message.getTo(), e);
            throw new RuntimeException("Failed to send HTML email: " + e.getMessage(), e);
        }
    }

    private String resolveFrom(String from) {
        if (StringUtils.hasText(from)) {
            return from;
        }
        return defaultFrom;
    }
}
