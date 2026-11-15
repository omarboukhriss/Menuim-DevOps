package com.tableorder.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.tableorder.model.Order;
import com.tableorder.model.OrderItem;
import com.tableorder.model.OrderStatus;
import com.tableorder.repository.DataRepository;
import com.tableorder.repository.IDataRepository;
import java.util.List;

/**
 * OrderViewModel manages order creation, fulfillment lifecycle transitions,
 * table-specific lookups, and inventory availability validation for UI screens.
 */
public class OrderViewModel extends AndroidViewModel {

    private final IDataRepository repository;

    /**
     * Constructs OrderViewModel and connects to the DataRepository singleton.
     *
     * @param application Application instance.
     */
    public OrderViewModel(@NonNull Application application) {
        super(application);
        this.repository = DataRepository.getInstance(application);
    }

    /**
     * Observes the complete list of orders.
     *
     * @return LiveData list of orders.
     */
    public LiveData<List<Order>> getOrders() {
        return repository.getOrders();
    }

    /**
     * Creates and places a new customer order with a freshly generated unique ID.
     *
     * @param tableNumber Table number where order originated.
     * @param items       List of ordered items.
     * @param total       Total monetary cost.
     * @return The id of the new order (used to open the tracking screen).
     */
    public int placeOrder(int tableNumber, List<OrderItem> items, double total) {
        int orderId = repository.generateId();
        Order order = new Order(orderId, tableNumber, items, total);
        repository.addOrder(order);
        return orderId;
    }

    /**
     * Marks an order as reviewed so the client cannot review it twice.
     *
     * @param orderId Order ID.
     */
    public void markReviewed(int orderId) {
        Order order = repository.getOrderById(orderId);
        if (order != null) {
            order.setReviewed(true);
            repository.updateOrder(order);
        }
    }

    /**
     * Adds an existing Order object to the repository.
     *
     * @param order Order to add.
     */
    public void addOrder(Order order) {
        repository.addOrder(order);
    }

    /**
     * Finds an order by ID synchronously.
     *
     * @param id Unique order ID.
     * @return Order object if found, otherwise null.
     */
    public Order getOrderById(int id) {
        return repository.getOrderById(id);
    }

    /**
     * Retrieves all orders placed by a specific table.
     *
     * @param tableNumber Table number to filter.
     * @return List of matching orders.
     */
    public List<Order> getOrdersByTable(int tableNumber) {
        return repository.getOrdersByTable(tableNumber);
    }

    /**
     * Accepts an order, validates stock, decrements inventory, and records ETA.
     *
     * @param orderId          Unique ID of order to accept.
     * @param estimatedMinutes Estimated prep time in minutes.
     * @return true if order accepted, false if stock insufficient.
     */
    public boolean acceptOrder(int orderId, int estimatedMinutes) {
        return repository.acceptOrder(orderId, estimatedMinutes);
    }

    /**
     * Rejects an order with a reason and restores inventory stock if previously accepted.
     *
     * @param orderId Unique ID of order to reject.
     * @param reason  Explanation for rejection.
     */
    public void rejectOrder(int orderId, String reason) {
        repository.rejectOrder(orderId, reason);
    }

    /**
     * Updates an order's lifecycle status.
     *
     * @param orderId   Order identifier.
     * @param newStatus New OrderStatus value.
     */
    public void updateOrderStatus(int orderId, OrderStatus newStatus) {
        repository.updateOrderStatus(orderId, newStatus);
    }

    /**
     * Validates inventory stock for an order by its ID.
     *
     * @param orderId Order ID to check.
     * @return null if stock is sufficient, or error message detailing deficits.
     */
    public String getInsufficientStockMessage(int orderId) {
        Order order = repository.getOrderById(orderId);
        if (order == null) {
            return null;
        }
        return repository.getInsufficientStockMessage(order);
    }

    /**
     * Validates inventory stock directly from an Order instance.
     *
     * @param order Order to evaluate.
     * @return null if stock is sufficient, or error message detailing deficits.
     */
    public String getInsufficientStockMessage(Order order) {
        return repository.getInsufficientStockMessage(order);
    }

    /**
     * Generates a unique incremented ID by delegating to the repository.
     *
     * @return Unique integer ID.
     */
    public int generateId() {
        return repository.generateId();
    }
}
