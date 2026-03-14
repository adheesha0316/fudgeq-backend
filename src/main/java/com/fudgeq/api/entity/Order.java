package com.fudgeq.api.entity;

import com.fudgeq.api.enums.OrderStatus;
import com.fudgeq.api.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Order extends BaseEntity{

    @Id
    @Column(name = "order_id", nullable = false, updatable = false)
    private String orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "preferred_delivery_date", nullable = false)
    private LocalDate preferredDeliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "is_paid")
    private boolean isPaid = false;
}
