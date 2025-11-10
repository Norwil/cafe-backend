package com.cafefusion.backend.orders.internal;

import com.cafefusion.backend.orders.api.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds all orders placed by a specific user,
     * ordered by when they were created (newest first).
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    // --- ADMIN ROLE ---

    Page<Order> findByStatusIn(List<OrderStatus> statuses, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    long countByStatus(OrderStatus status);

    long countByStatusAndCreatedAtBetween(OrderStatus status, Instant start, Instant end);


}
