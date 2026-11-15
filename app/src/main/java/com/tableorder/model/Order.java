package com.tableorder.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Order represents a dining party's order placed from a table.
 * Manages order items, total calculation, progress status, preparation ETA,
 * rejection reasoning, timestamps, and customer review state.
 */
public class Order implements Serializable {

    private int id;
    private int tableNumber;
    private List<OrderItem> items;
    private double total;
    private OrderStatus status;
    private int estimatedMinutes;
    private String rejectReason;
    private long createdAt;
    private long acceptedAt;
    private boolean reviewed;

    /**
     * Default constructor required for Gson deserialization.
     */
    public Order() {
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.rejectReason = "";
        this.createdAt = System.currentTimeMillis();
        this.acceptedAt = 0L;
        this.reviewed = false;
    }

    /**
     * Creates a new Order at the time of customer placement.
     * Sets status to PENDING, records creation timestamp, and initializes reviewed to false.
     *
     * @param id          Unique order ID.
     * @param tableNumber Table number where order originated.
     * @param items       List of ordered items.
     * @param total       Total monetary cost of the order.
     */
    public Order(int id, int tableNumber, List<OrderItem> items, double total) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.items = items != null ? items : new ArrayList<>();
        this.total = total;
        this.status = OrderStatus.PENDING;
        this.estimatedMinutes = 0;
        this.rejectReason = "";
        this.createdAt = System.currentTimeMillis();
        this.acceptedAt = 0L;
        this.reviewed = false;
    }

    /**
     * Full constructor for reconstructing orders with all historical fields.
     */
    public Order(int id, int tableNumber, List<OrderItem> items, double total,
                 OrderStatus status, int estimatedMinutes, String rejectReason,
                 long createdAt, long acceptedAt, boolean reviewed) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.items = items != null ? items : new ArrayList<>();
        this.total = total;
        this.status = status;
        this.estimatedMinutes = estimatedMinutes;
        this.rejectReason = rejectReason;
        this.createdAt = createdAt;
        this.acceptedAt = acceptedAt;
        this.reviewed = reviewed;
    }

    // ── Getters and Setters ───────────────────────────────────────────

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(long acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
    }
}
