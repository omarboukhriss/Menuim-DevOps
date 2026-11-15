package com.tableorder.repository;

import androidx.lifecycle.LiveData;
import com.tableorder.model.MenuItem;
import com.tableorder.model.Order;
import com.tableorder.model.OrderStatus;
import com.tableorder.model.Review;
import com.tableorder.model.WaiterCall;
import java.util.List;

/**
 * IDataRepository defines the contract for data operations in the TableOrder app.
 * Provides access to reactive LiveData streams and synchronous mutation methods
 * for menu items, customer orders, reviews, and waiter calls.
 */
public interface IDataRepository {

    // ── Menu ──────────────────────────────────────────────────────────
    /**
     * Observes the complete list of menu items.
     *
     * @return LiveData list of menu items.
     */
    LiveData<List<MenuItem>> getMenuItems();

    /**
     * Adds a new menu item and persists changes.
     *
     * @param item Menu item to add.
     */
    void addMenuItem(MenuItem item);

    /**
     * Updates an existing menu item by ID and persists changes.
     *
     * @param item Menu item containing updated values.
     */
    void updateMenuItem(MenuItem item);

    /**
     * Deletes a menu item by ID and persists changes.
     *
     * @param itemId Unique ID of item to delete.
     */
    void deleteMenuItem(int itemId);

    /**
     * Finds and returns a menu item by its ID.
     *
     * @param id Unique ID of the menu item.
     * @return MenuItem if found, otherwise null.
     */
    MenuItem getMenuItemById(int id);

    // ── Orders ────────────────────────────────────────────────────────
    /**
     * Observes the list of all restaurant orders.
     *
     * @return LiveData list of orders.
     */
    LiveData<List<Order>> getOrders();

    /**
     * Inserts a newly placed order and persists changes.
     *
     * @param order Order to add.
     */
    void addOrder(Order order);

    /**
     * Updates an existing order and persists changes.
     *
     * @param order Order with updated values.
     */
    void updateOrder(Order order);

    /**
     * Finds and returns an order by its ID.
     *
     * @param id Unique order ID.
     * @return Order if found, otherwise null.
     */
    Order getOrderById(int id);

    /**
     * Returns all orders associated with a specific table number.
     *
     * @param tableNumber Table number to filter by.
     * @return List of orders for the table.
     */
    List<Order> getOrdersByTable(int tableNumber);

    /**
     * Accepts an order if sufficient stock is available for all ordered items.
     * Decrements inventory and begins preparation countdown.
     *
     * @param orderId          Unique ID of order to accept.
     * @param estimatedMinutes Estimated preparation duration in minutes.
     * @return true if accepted successfully, false if stock is insufficient.
     */
    boolean acceptOrder(int orderId, int estimatedMinutes);

    /**
     * Rejects an order with a reason. If the order was previously ACCEPTED,
     * restores inventory stock for each ordered item.
     *
     * @param orderId Unique ID of order to reject.
     * @param reason  Explanation note for rejection.
     */
    void rejectOrder(int orderId, String reason);

    /**
     * Updates the status of an order and notifies observers.
     *
     * @param orderId   Unique order ID.
     * @param newStatus New OrderStatus enum value.
     */
    void updateOrderStatus(int orderId, OrderStatus newStatus);

    /**
     * Checks stock availability for all items in an order.
     *
     * @param order Order to validate.
     * @return null if stock is sufficient, or a descriptive message like 'Not enough Pizza (need 2, have 1)'.
     */
    String getInsufficientStockMessage(Order order);

    // ── Reviews ───────────────────────────────────────────────────────
    /**
     * Observes the list of customer reviews.
     *
     * @return LiveData list of reviews.
     */
    LiveData<List<Review>> getReviews();

    /**
     * Inserts a new customer review and persists changes.
     *
     * @param review Review to add.
     */
    void addReview(Review review);

    // ── Waiter Calls ──────────────────────────────────────────────────
    /**
     * Observes the list of waiter assistance calls.
     *
     * @return LiveData list of waiter calls.
     */
    LiveData<List<WaiterCall>> getWaiterCalls();

    /**
     * Records a new waiter assistance call and persists changes.
     *
     * @param call WaiterCall to add.
     */
    void addWaiterCall(WaiterCall call);

    /**
     * Marks an assistance call as handled by restaurant staff.
     *
     * @param callId Unique ID of call to mark handled.
     */
    void handleWaiterCall(int callId);

    // ── Utility ───────────────────────────────────────────────────────
    /**
     * Generates a globally unique incrementing integer ID across entities.
     *
     * @return New unique integer ID.
     */
    int generateId();
}
