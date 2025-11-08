package com.cafefusion.backend.orders.api;


import com.cafefusion.backend.orders.api.model.CreateOrderRequest;
import com.cafefusion.backend.orders.api.model.OrderDto;

/**
 * This is the PUBLIC API for the Orders Module.
 * Other odules will use this to create new orders.
 */
public interface OrdersApi {

    /**
     * Creates a new order based on a list of menu item IDs.
     * @param request The request containing the list of item IDs
     * @return A DTO of the newly created order.
     */
    OrderDto createOrder(CreateOrderRequest request);
}
