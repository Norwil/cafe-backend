package com.cafefusion.backend.orders.internal;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items", schema = "orders")
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Long menuItemId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    public OrderItem() {}

    public OrderItem(Order order, Long menuItemId, String name, BigDecimal price) {
        this.order = order;
        this.menuItemId = menuItemId;
        this.name = name;
        this.price = price;
    }
}
