package com.cafefusion.backend.orders.api;


import com.cafefusion.backend.orders.api.model.CreateOrderRequest;
import com.cafefusion.backend.orders.api.model.OrderDto;
import com.cafefusion.backend.orders.api.model.UpdateOrderRequest;

import java.util.Optional;

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

    /**
     * Finds an order by its unique ID.
     * @param orderId The ID of the order to find.
     * @return An optional containing the order DTO if found.
     */
    Optional<OrderDto> getOrderById(Long orderId);

    /**
     * Updates the status of an existing order.
     * @param orderId The ID of the order to update
     * @param request request The request containing the new status
     * @return The updated order DTO.
     */
    OrderDto updateOrderStatus(Long orderId, UpdateOrderRequest request);

    /**
     * Deletes an order. (e.g., if it was a test or mistake)
     *
     * @param orderId
     */
    void deleteOrder(Long orderId);
}
