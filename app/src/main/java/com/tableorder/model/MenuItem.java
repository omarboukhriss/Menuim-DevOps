package com.tableorder.model;

import java.io.Serializable;

/**
 * MenuItem represents an item on the restaurant menu.
 * Includes pricing, categorization, availability status, and inventory stock tracking.
 */
public class MenuItem implements Serializable {

    private int id;
    private String name;
    private String description;
    private double price;
    private String category;
    private boolean available;
    private int stockQuantity;
    private int lowStockThreshold;

    /**
     * Default constructor required for Gson deserialization.
     */
    public MenuItem() {
    }

    /**
     * Constructs a MenuItem with all attributes specified.
     *
     * @param id                 Unique ID of the menu item.
     * @param name               Name of the menu item.
     * @param description        Brief description of the item ingredients or taste.
     * @param price              Unit price in dollars/local currency.
     * @param category           Category name (e.g. Hot Drinks, Cold Drinks, Food, Desserts).
     * @param available          Whether this item is currently available for ordering.
     * @param stockQuantity      Current units in stock.
     * @param lowStockThreshold  Threshold below which a low-stock alert is triggered.
     */
    public MenuItem(int id, String name, String description, double price,
                    String category, boolean available, int stockQuantity, int lowStockThreshold) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = available;
        this.stockQuantity = stockQuantity;
        this.lowStockThreshold = lowStockThreshold;
    }

    // ── Getters and Setters ───────────────────────────────────────────

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }
}
