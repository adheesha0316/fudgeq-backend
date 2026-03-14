package com.fudgeq.api.enums;

public enum OrderStatus {
    PENDING,        // Order placed, waiting for payment/confirmation
    CONFIRMED,      // Payment successful/Admin confirmed
    PROCESSING,     // Being prepared (Fudge is being made)
    SHIPPED,        // Handed over to delivery
    DELIVERED,      // Customer received the order
    CANCELLED,      // Cancelled by user or admin
    REFUNDED        // Money returned to customer
}
