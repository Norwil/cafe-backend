package com.cafefusion.backend.orders.api.exception;

import com.cafefusion.backend.orders.api.model.OrderStatus;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(OrderStatus from, OrderStatus to) {
        super("Cannot change status from " + from + " to " + to);
    }
}
