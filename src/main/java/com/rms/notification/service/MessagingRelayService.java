package com.rms.notification.service;

import com.rms.notification.dto.ChatMessagePayload;
import com.rms.notification.dto.NotificationPayload;
import com.rms.notification.dto.UserNotificationEvent;
import com.rms.notification.utils.constants.QueueConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagingRelayService {

    private static final String CHAT_TOPIC_PREFIX = "/topic/chat.";
    private static final String USER_CHAT_TOPIC_PREFIX = "/topic/users.";
    private static final String USER_CHAT_TOPIC_SUFFIX = ".chat";
    private static final String NOTIFICATION_TOPIC = "/topic/notifications";
    private static final String USER_NOTIFICATION_TOPIC_SUFFIX = ".notifications";

    private final SimpMessagingTemplate messagingTemplate;
    private final RabbitTemplate rabbitTemplate;

    public ChatMessagePayload handleChat(ChatMessagePayload message) {
        if (message == null) {
            throw new IllegalArgumentException("Chat message payload was null.");
        }

        ChatMessagePayload normalized = normalizeChat(message);
        String destination = resolveChatDestination(normalized);

        messagingTemplate.convertAndSend(destination, normalized);
        sendChatToSender(normalized);
        rabbitTemplate.convertAndSend(QueueConstants.Chat.EXCHANGE, QueueConstants.Chat.RoutingKey.MAIN, normalized);

        log.info("Relayed chat message messageId={} destination={}", normalized.getMessageId(), destination);
        return normalized;
    }

    /**
     * Called by the RabbitMQ listener for admin-service domain events.
     * Only broadcasts via WebSocket — does NOT re-publish to RabbitMQ
     * (the reporting service already consumed the original message for persistence).
     */
    public void broadcastViaWebSocket(UserNotificationEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("UserNotificationEvent was null.");
        }
        NotificationPayload payload = NotificationPayload.builder()
                .notificationId(event.getNotificationId())
                .userId(event.getUserId())
                .title(event.getTitle())
                .body(event.getBody())
                .type(event.getType())
                .broadcast(event.isBroadcast())
                .metadata(event.getMetadata())
                .createdAt(event.getCreatedAt())
                .build();
        String destination = resolveNotificationDestination(payload);
        messagingTemplate.convertAndSend(destination, payload);
        log.info("WS broadcast from admin event notificationId={} type={}", event.getNotificationId(), event.getType());
    }

    public NotificationPayload handleNotification(NotificationPayload payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Notification payload was null.");
        }

        NotificationPayload normalized = normalizeNotification(payload);
        String destination = resolveNotificationDestination(normalized);

        messagingTemplate.convertAndSend(destination, normalized);
        rabbitTemplate.convertAndSend(QueueConstants.UserNotification.EXCHANGE, QueueConstants.UserNotification.RoutingKey.MAIN, normalized);

        log.info("Relayed notification notificationId={} destination={}", normalized.getNotificationId(), destination);
        return normalized;
    }

    private ChatMessagePayload normalizeChat(ChatMessagePayload message) {
        return ChatMessagePayload.builder()
                .messageId(StringUtils.hasText(message.getMessageId()) ? message.getMessageId() : UUID.randomUUID().toString())
                .roomId(message.getRoomId())
                .senderId(message.getSenderId())
                .recipientId(message.getRecipientId())
                .content(message.getContent())
                .metadata(message.getMetadata())
                .sentAt(message.getSentAt() != null ? message.getSentAt() : Instant.now())
                .build();
    }

    private NotificationPayload normalizeNotification(NotificationPayload payload) {
        return NotificationPayload.builder()
                .notificationId(StringUtils.hasText(payload.getNotificationId()) ? payload.getNotificationId() : UUID.randomUUID().toString())
                .userId(payload.getUserId())
                .title(payload.getTitle())
                .body(payload.getBody())
                .type(payload.getType())
                .broadcast(payload.isBroadcast())
                .metadata(payload.getMetadata())
                .createdAt(payload.getCreatedAt() != null ? payload.getCreatedAt() : Instant.now())
                .build();
    }

    private String resolveChatDestination(ChatMessagePayload message) {
        if (StringUtils.hasText(message.getRoomId())) {
            return CHAT_TOPIC_PREFIX + message.getRoomId();
        }
        if (message.getRecipientId() != null) {
            return USER_CHAT_TOPIC_PREFIX + message.getRecipientId() + USER_CHAT_TOPIC_SUFFIX;
        }
        return CHAT_TOPIC_PREFIX + "broadcast";
    }

    private void sendChatToSender(ChatMessagePayload message) {
        if (StringUtils.hasText(message.getRoomId())) {
            return;
        }
        if (message.getSenderId() == null || message.getRecipientId() == null) {
            return;
        }
        if (message.getSenderId().equals(message.getRecipientId())) {
            return;
        }
        String senderDestination = USER_CHAT_TOPIC_PREFIX + message.getSenderId() + USER_CHAT_TOPIC_SUFFIX;
        messagingTemplate.convertAndSend(senderDestination, message);
    }

    private String resolveNotificationDestination(NotificationPayload payload) {
        if (payload.isBroadcast()) {
            return NOTIFICATION_TOPIC;
        }
        if (payload.getUserId() == null) {
            throw new IllegalArgumentException("Notification payload missing userId or broadcast=true.");
        }
        return USER_CHAT_TOPIC_PREFIX + payload.getUserId() + USER_NOTIFICATION_TOPIC_SUFFIX;
    }
}
