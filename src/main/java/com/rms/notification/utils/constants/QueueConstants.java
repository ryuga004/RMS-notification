package com.rms.notification.utils.constants;

public interface QueueConstants {
    interface AdminNotification {
        String EXCHANGE = "admin-notification.exchange";
        interface Queue {
            String MAIN = "admin-notification.queue";
            String DLQ = "admin-notification.dlq";
        }
        interface RoutingKey {
            String MAIN = "admin.notification.routingkey";
            String DLQ = "admin.notification.dlq";
        }
    }

    interface Audit {
        String EXCHANGE = "admin-audit.exchange";
        interface Queue {
            String MAIN = "admin-audit.queue";
            String DLQ = "admin-audit.dlq";
        }
        interface RoutingKey {
            String MAIN = "admin.audit.routingkey";
            String DLQ = "admin.audit.dlq";
        }
    }

    interface Chat {
        String EXCHANGE = "chat.exchange";
        interface Queue {
            String MAIN = "chat.queue";
            String DLQ = "chat.dlq";
        }
        interface RoutingKey {
            String MAIN = "chat.message";
            String DLQ = "chat.dlq";
        }
    }

    interface UserNotification {
        String EXCHANGE = "notification.exchange";
        interface Queue {
            String MAIN = "notification.queue";
            String DLQ = "notification.dlq";
        }
        interface RoutingKey {
            String MAIN = "notification.message";
            String DLQ = "notification.dlq";
        }
    }
}
