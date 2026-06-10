package com.rms.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessagePayload {
    private String messageId;
    private String roomId;
    private Long senderId;
    private Long recipientId;
    private String content;
    private Map<String, Object> metadata;
    private Instant sentAt;
}
