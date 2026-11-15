package com.tableorder.model;

/**
 * OrderStatus defines the lifecycle states of a customer order.
 *
 * PENDING    - Order just placed by the client; awaiting manager response.
 * ACCEPTED   - Order accepted by manager; kitchen preparation countdown starts.
 * REJECTED   - Order rejected by manager with an optional reason.
 * PREPARING  - Kitchen is actively preparing the food and drinks.
 * READY      - Order is fully prepared and ready for table delivery.
 * SERVED     - Order has been delivered to the customer table.
 */
public enum OrderStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    PREPARING,
    READY,
    SERVED
}
