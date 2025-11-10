package com.cafefusion.backend.orders.api.model;

public enum OrderStatus {
    PENDING_APPROVAL,       // User has placed the order, admin must approve
    CONFIRMED,              // Admin has confirmed the order
    IN_PROGRESS,            // Kitchen is preparing the order
    READY_FOR_PICKUP,       // Order is ready for the user
    COMPLETED,              // User has picked up the order
    CANCELLED               // Order was cancelled
}
