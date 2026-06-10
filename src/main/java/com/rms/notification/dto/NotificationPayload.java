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
public class NotificationPayload {
    private String notificationId;
    private Long userId;
    private String title;
    private String body;
    private String type;
    private boolean broadcast;
    private Map<String, Object> metadata;
    private Instant createdAt;
}
