package com.rms.notification.controller;

import com.rms.notification.dto.ChatMessagePayload;
import com.rms.notification.dto.NotificationPayload;
import com.rms.notification.service.MessagingRelayService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessagingController {

    private final MessagingRelayService messagingRelayService;

    @MessageMapping("/chat.send")
    public void sendChat(ChatMessagePayload message) {
        messagingRelayService.handleChat(message);
    }

    @MessageMapping("/notification.send")
    public void sendNotification(NotificationPayload notification) {
        messagingRelayService.handleNotification(notification);
    }
}
