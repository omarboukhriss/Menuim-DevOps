package com.tableorder.util;

/**
 * Constants — app-wide constant values.
 *
 * Keeping constants in one place makes them easy to find and change.
 * For example, the manager PIN is here so you can change it without
 * hunting through multiple files.
 */
public final class Constants {

    // Private constructor prevents instantiation (this is a utility class)
    private Constants() { }

    // ── Manager PIN ────────────────────────────────────────────────
    /** Hard-coded PIN the manager must enter to access management screens. */
    public static final String MANAGER_PIN = "1234";

    // ── SharedPreferences ──────────────────────────────────────────
    /** Name of the SharedPreferences file where all data is saved as JSON. */
    public static final String PREFS_NAME = "table_order_prefs";

    /** Key for the menu items JSON string inside SharedPreferences. */
    public static final String KEY_MENU_ITEMS = "menu_items";

    /** Key for the orders JSON string. */
    public static final String KEY_ORDERS = "orders";

    /** Key for the reviews JSON string. */
    public static final String KEY_REVIEWS = "reviews";

    /** Key for the waiter calls JSON string. */
    public static final String KEY_WAITER_CALLS = "waiter_calls";

    /** Key to track whether sample data has been loaded on first launch. */
    public static final String KEY_FIRST_LAUNCH = "first_launch";

    // ── QR Code Format ─────────────────────────────────────────────
    /**
     * QR codes encode data as "restaurantName|tableNumber".
     * This separator splits the two parts when scanning.
     */
    public static final String QR_SEPARATOR = "|";

    /** Default restaurant name used when generating QR codes. */
    public static final String DEFAULT_RESTAURANT_NAME = "Cafe Sousse";

    // ── Intent Extras ──────────────────────────────────────────────
    /** Key for passing the table number between activities. */
    public static final String EXTRA_TABLE_NUMBER = "table_number";

    /** Key for passing the order ID between activities. */
    public static final String EXTRA_ORDER_ID = "order_id";

    /** Key for passing the restaurant name between activities. */
    public static final String EXTRA_RESTAURANT_NAME = "restaurant_name";

    /** Key for passing a menu item ID (edit mode). */
    public static final String EXTRA_MENU_ITEM_ID = "menu_item_id";

    // ── Order Statuses ─────────────────────────────────────────────
    // These match the enum values in Order.java (Stage 2).
    // Listed here as documentation for quick reference.
    //   PENDING    → just placed by the client
    //   ACCEPTED   → manager accepted, countdown starts
    //   REJECTED   → manager rejected with a reason
    //   PREPARING  → kitchen is working on it
    //   READY      → food is ready for pickup/serving
    //   SERVED     → delivered to the table
}
