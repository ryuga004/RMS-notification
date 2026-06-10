package com.rms.notification.consumer;

import com.rms.notification.dto.Email;
import com.rms.notification.service.EmailNotificationService;
import com.rms.notification.utils.constants.QueueConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminNotificationListener {

    private final EmailNotificationService emailNotificationService;

    @RabbitListener(queues = QueueConstants.AdminNotification.Queue.MAIN)
    public void handleEmailNotification(Email message) {
        log.info("Received email notification for {}", message != null ? message.getTo() : "unknown");
        if (message == null) {
            throw new IllegalArgumentException("Email notification message was null.");
        }
        emailNotificationService.send(message);
    }

    @RabbitListener(queues = QueueConstants.AdminNotification.Queue.DLQ)
    public void handleEmailNotificationDlq(Email message) {
        log.error("Email notification moved to DLQ for {}", message != null ? message.getTo() : "unknown");
    }
}
