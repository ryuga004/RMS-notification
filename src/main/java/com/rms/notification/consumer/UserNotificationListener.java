package com.rms.notification.consumer;

import com.rms.notification.dto.UserNotificationEvent;
import com.rms.notification.service.MessagingRelayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserNotificationListener {

    private final MessagingRelayService messagingRelayService;

    @RabbitListener(queues = "notification.ws.queue")
    public void handleNotification(UserNotificationEvent event) {
        log.info("Received admin-side notification notificationId={} type={}", event.getNotificationId(), event.getType());
        messagingRelayService.broadcastViaWebSocket(event);
    }
}
