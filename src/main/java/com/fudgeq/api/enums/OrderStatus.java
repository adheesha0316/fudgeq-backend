package com.fudgeq.api.enums;

public enum OrderStatus {
    PENDING_REVIEW,   // Waiting for Admin to check the date
    CONFIRMED,        // Admin confirmed the delivery date
    PROCESSING,       // Fudge is being prepared
    SHIPPED,          // Out for delivery
    DELIVERED,        // Received by customer
    CANCELLED,        // Cancelled by customer or admin
    REJECTED,         // Rejected by admin with a reason
    REFUNDED          // If online payment was made
}
