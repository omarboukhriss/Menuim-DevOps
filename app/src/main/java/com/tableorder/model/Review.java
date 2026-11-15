package com.tableorder.model;

import java.io.Serializable;

/**
 * Review represents post-dining feedback submitted by a customer for an order.
 * Tracks star rating (1-5), user comment, ordered items summary, and creation timestamp.
 */
public class Review implements Serializable {

    private int id;
    private int orderId;
    private int tableNumber;
    private int rating;
    private String comment;
    private String itemsOrdered;
    private long createdAt;

    /**
     * Default constructor required for Gson deserialization.
     */
    public Review() {
        this.createdAt = System.currentTimeMillis();
    }

    /**
     * Constructor for creating a new Review.
     * Automatically sets createdAt to the current system timestamp.
     *
     * @param id           Unique review ID.
     * @param orderId      Associated order ID.
     * @param tableNumber  Table number where the order was consumed.
     * @param rating       Star rating between 1 and 5.
     * @param comment      Customer's feedback notes or remarks.
     * @param itemsOrdered Formatted text of items ordered.
     */
    public Review(int id, int orderId, int tableNumber, int rating, String comment, String itemsOrdered) {
        this.id = id;
        this.orderId = orderId;
        this.tableNumber = tableNumber;
        this.rating = rating;
        this.comment = comment;
        this.itemsOrdered = itemsOrdered;
        this.createdAt = System.currentTimeMillis();
    }

    /**
     * Full constructor including explicit createdAt timestamp.
     */
    public Review(int id, int orderId, int tableNumber, int rating, String comment, String itemsOrdered, long createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.tableNumber = tableNumber;
        this.rating = rating;
        this.comment = comment;
        this.itemsOrdered = itemsOrdered;
        this.createdAt = createdAt;
    }

    // ── Getters and Setters ───────────────────────────────────────────

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getItemsOrdered() {
        return itemsOrdered;
    }

    public void setItemsOrdered(String itemsOrdered) {
        this.itemsOrdered = itemsOrdered;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
