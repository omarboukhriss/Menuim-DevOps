package com.tableorder.model;

import java.io.Serializable;

/**
 * WaiterCall represents a table's request for waiter assistance.
 * Tracks table number, creation timestamp, and whether restaurant staff has handled it.
 */
public class WaiterCall implements Serializable {

    private int id;
    private int tableNumber;
    private long createdAt;
    private boolean handled;

    /**
     * Default constructor required for Gson deserialization.
     */
    public WaiterCall() {
        this.createdAt = System.currentTimeMillis();
        this.handled = false;
    }

    /**
     * Creates a new assistance call for the specified table.
     * Automatically sets createdAt to current time and handled to false.
     *
     * @param id          Unique call identifier.
     * @param tableNumber Table number making the request.
     */
    public WaiterCall(int id, int tableNumber) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.createdAt = System.currentTimeMillis();
        this.handled = false;
    }

    /**
     * Full constructor including explicit timestamp and handled flag.
     */
    public WaiterCall(int id, int tableNumber, long createdAt, boolean handled) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.createdAt = createdAt;
        this.handled = handled;
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }
}
