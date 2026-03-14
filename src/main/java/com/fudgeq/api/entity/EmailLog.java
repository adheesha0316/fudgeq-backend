package com.fudgeq.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class EmailLog extends BaseEntity {

    @Id
    @Column(name = "log_id", nullable = false, updatable = false)
    private String logId; // Changed from Long to String

    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "is_success", nullable = false)
    private boolean isSuccess;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
