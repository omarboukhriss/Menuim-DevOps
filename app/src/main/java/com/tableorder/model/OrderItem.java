package com.tableorder.model;

import java.io.Serializable;

/**
 * OrderItem represents a single item line within an Order,
 * preserving a snapshot of the item's name, unit price, and chosen quantity.
 */
public class OrderItem implements Serializable {

    private int itemId;
    private String name;
    private int qty;
    private double price;

    /**
     * Default constructor required for Gson deserialization.
     */
    public OrderItem() {
    }

    /**
     * Constructs an OrderItem with all required details.
     *
     * @param itemId Unique ID of the associated MenuItem.
     * @param name   Name of the item at the time the order is created.
     * @param qty    Quantity requested by the customer.
     * @param price  Price per unit at order placement.
     */
    public OrderItem(int itemId, String name, int qty, double price) {
        this.itemId = itemId;
        this.name = name;
        this.qty = qty;
        this.price = price;
    }

    /**
     * Computes the line item subtotal.
     *
     * @return Quantity multiplied by unit price.
     */
    public double getSubtotal() {
        return qty * price;
    }

    // ── Getters and Setters ───────────────────────────────────────────

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
