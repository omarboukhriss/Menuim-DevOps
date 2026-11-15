package com.tableorder.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tableorder.model.MenuItem;
import com.tableorder.model.Order;
import com.tableorder.model.OrderItem;
import com.tableorder.model.OrderStatus;
import com.tableorder.model.Review;
import com.tableorder.model.WaiterCall;
import com.tableorder.util.Constants;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DataRepository is the singleton data manager implementing IDataRepository.
 * Manages in-memory cached lists of entities, handles thread-safe mutations,
 * updates reactive LiveData streams, and persists all state as JSON in SharedPreferences.
 */
public class DataRepository implements IDataRepository {

    // Double-checked locking singleton instance
    private static volatile DataRepository instance;

    private final Context context;
    private final SharedPreferences prefs;
    private final Gson gson;

    // In-memory entity collections
    private final ArrayList<MenuItem> menuItems = new ArrayList<>();
    private final ArrayList<Order> orders = new ArrayList<>();
    private final ArrayList<Review> reviews = new ArrayList<>();
    private final ArrayList<WaiterCall> waiterCalls = new ArrayList<>();

    // Reactive LiveData holders (exposed as read-only LiveData)
    private final MutableLiveData<List<MenuItem>> menuItemsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Order>> ordersLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Review>> reviewsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<WaiterCall>> waiterCallsLiveData = new MutableLiveData<>();

    // Global unique ID counter
    private final AtomicInteger idCounter = new AtomicInteger(1);

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes dependencies and loads existing persisted data.
     *
     * @param context Application context to avoid activity memory leaks.
     */
    private DataRepository(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        loadData();
    }

    /**
     * Obtains the singleton instance using double-checked locking for thread safety.
     *
     * @param context Context used to initialize application context reference.
     * @return Shared DataRepository instance.
     */
    public static DataRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (DataRepository.class) {
                if (instance == null) {
                    instance = new DataRepository(context);
                }
            }
        }
        return instance;
    }

    // ── Data Loading & Initialization ─────────────────────────────────

    /**
     * Loads persisted JSON datasets from SharedPreferences into in-memory lists.
     * If first launch or if menu items are empty, populates default sample data.
     * Updates idCounter to exceed the highest existing ID across all entities.
     */
    private void loadData() {
        boolean isFirstLaunch = prefs.getBoolean(Constants.KEY_FIRST_LAUNCH, true);

        // Load Menu Items
        String menuJson = prefs.getString(Constants.KEY_MENU_ITEMS, null);
        if (menuJson != null && !menuJson.trim().isEmpty()) {
            Type type = new TypeToken<ArrayList<MenuItem>>() {}.getType();
            ArrayList<MenuItem> loadedMenu = gson.fromJson(menuJson, type);
            if (loadedMenu != null) {
                menuItems.clear();
                menuItems.addAll(loadedMenu);
            }
        }

        // Load Orders
        String ordersJson = prefs.getString(Constants.KEY_ORDERS, null);
        if (ordersJson != null && !ordersJson.trim().isEmpty()) {
            Type type = new TypeToken<ArrayList<Order>>() {}.getType();
            ArrayList<Order> loadedOrders = gson.fromJson(ordersJson, type);
            if (loadedOrders != null) {
                orders.clear();
                orders.addAll(loadedOrders);
            }
        }

        // Load Reviews
        String reviewsJson = prefs.getString(Constants.KEY_REVIEWS, null);
        if (reviewsJson != null && !reviewsJson.trim().isEmpty()) {
            Type type = new TypeToken<ArrayList<Review>>() {}.getType();
            ArrayList<Review> loadedReviews = gson.fromJson(reviewsJson, type);
            if (loadedReviews != null) {
                reviews.clear();
                reviews.addAll(loadedReviews);
            }
        }

        // Load Waiter Calls
        String waiterCallsJson = prefs.getString(Constants.KEY_WAITER_CALLS, null);
        if (waiterCallsJson != null && !waiterCallsJson.trim().isEmpty()) {
            Type type = new TypeToken<ArrayList<WaiterCall>>() {}.getType();
            ArrayList<WaiterCall> loadedCalls = gson.fromJson(waiterCallsJson, type);
            if (loadedCalls != null) {
                waiterCalls.clear();
                waiterCalls.addAll(loadedCalls);
            }
        }

        // Populate sample data if initial launch or menu is empty
        if (isFirstLaunch || menuItems.isEmpty()) {
            loadSampleData();
        }

        // Compute maximum ID across all collections to avoid ID collisions
        int maxId = 0;
        for (MenuItem item : menuItems) {
            if (item.getId() > maxId) {
                maxId = item.getId();
            }
        }
        for (Order order : orders) {
            if (order.getId() > maxId) {
                maxId = order.getId();
            }
        }
        for (Review review : reviews) {
            if (review.getId() > maxId) {
                maxId = review.getId();
            }
        }
        for (WaiterCall call : waiterCalls) {
            if (call.getId() > maxId) {
                maxId = call.getId();
            }
        }
        idCounter.set(maxId + 1);

        // Publish initial LiveData states
        notifyMenuItemsChanged();
        notifyOrdersChanged();
        notifyReviewsChanged();
        notifyWaiterCallsChanged();
    }

    /**
     * Seeds initial 14 menu items across 4 categories and marks first launch as complete.
     */
    public void loadSampleData() {
        menuItems.clear();
        int sampleId = 1;

        // Hot Drinks (4 items)
        menuItems.add(new MenuItem(sampleId++, "Espresso", "Rich and bold single shot of pure espresso", 3.50, "Hot Drinks", true, 50, 10));
        menuItems.add(new MenuItem(sampleId++, "Cappuccino", "Espresso topped with steamed milk and thick velvety foam", 4.50, "Hot Drinks", true, 40, 10));
        menuItems.add(new MenuItem(sampleId++, "Latte", "Smooth espresso balanced with creamy steamed milk", 4.00, "Hot Drinks", true, 40, 10));
        menuItems.add(new MenuItem(sampleId++, "Hot Chocolate", "Decadent melted cocoa served with warm frothy milk", 3.50, "Hot Drinks", true, 30, 5));

        // Cold Drinks (3 items)
        menuItems.add(new MenuItem(sampleId++, "Iced Coffee", "Chilled brewed espresso poured over crisp ice", 4.00, "Cold Drinks", true, 30, 5));
        menuItems.add(new MenuItem(sampleId++, "Fresh Orange Juice", "Freshly squeezed Valencia oranges with pulp", 5.00, "Cold Drinks", true, 20, 5));
        menuItems.add(new MenuItem(sampleId++, "Lemonade", "Zesty house-made lemonade infused with garden mint", 3.50, "Cold Drinks", true, 25, 5));

        // Food (4 items)
        menuItems.add(new MenuItem(sampleId++, "Margherita Pizza", "San Marzano tomatoes, fresh mozzarella, extra virgin olive oil, basil", 12.00, "Food", true, 15, 3));
        menuItems.add(new MenuItem(sampleId++, "Caesar Salad", "Crispy romaine, parmesan curls, herb croutons, creamy Caesar dressing", 9.50, "Food", true, 20, 5));
        menuItems.add(new MenuItem(sampleId++, "Club Sandwich", "Toasted artisan bread, grilled chicken, smoked bacon, tomato, lettuce", 8.50, "Food", true, 18, 5));
        menuItems.add(new MenuItem(sampleId++, "Cheese Burger", "Charbroiled beef patty, sharp cheddar, pickles, house burger relish", 10.00, "Food", true, 15, 3));

        // Desserts (3 items)
        menuItems.add(new MenuItem(sampleId++, "Chocolate Cake", "Warm molten chocolate fudge cake with dark ganache drizzle", 6.50, "Desserts", true, 12, 3));
        menuItems.add(new MenuItem(sampleId++, "Tiramisu", "Savoiardi ladyfingers soaked in espresso layered with mascarpone cream", 7.00, "Desserts", true, 10, 3));
        menuItems.add(new MenuItem(sampleId++, "Ice Cream", "Three scoops of gourmet artisanal gelato (vanilla, chocolate, berry)", 5.00, "Desserts", true, 20, 5));

        // Persist to SharedPreferences and mark first launch false
        saveMenuItems();
        prefs.edit().putBoolean(Constants.KEY_FIRST_LAUNCH, false).apply();
    }

    // ── Persistence Helpers ───────────────────────────────────────────

    public void saveMenuItems() {
        String json = gson.toJson(menuItems);
        prefs.edit().putString(Constants.KEY_MENU_ITEMS, json).apply();
    }

    public void saveOrders() {
        String json = gson.toJson(orders);
        prefs.edit().putString(Constants.KEY_ORDERS, json).apply();
    }

    public void saveReviews() {
        String json = gson.toJson(reviews);
        prefs.edit().putString(Constants.KEY_REVIEWS, json).apply();
    }

    public void saveWaiterCalls() {
        String json = gson.toJson(waiterCalls);
        prefs.edit().putString(Constants.KEY_WAITER_CALLS, json).apply();
    }

    // ── LiveData Notification Helpers ─────────────────────────────────

    private void notifyMenuItemsChanged() {
        ArrayList<MenuItem> copy = new ArrayList<>(menuItems);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            menuItemsLiveData.setValue(copy);
        } else {
            menuItemsLiveData.postValue(copy);
        }
    }

    private void notifyOrdersChanged() {
        ArrayList<Order> copy = new ArrayList<>(orders);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ordersLiveData.setValue(copy);
        } else {
            ordersLiveData.postValue(copy);
        }
    }

    private void notifyReviewsChanged() {
        ArrayList<Review> copy = new ArrayList<>(reviews);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            reviewsLiveData.setValue(copy);
        } else {
            reviewsLiveData.postValue(copy);
        }
    }

    private void notifyWaiterCallsChanged() {
        ArrayList<WaiterCall> copy = new ArrayList<>(waiterCalls);
        if (Looper.myLooper() == Looper.getMainLooper()) {
            waiterCallsLiveData.setValue(copy);
        } else {
            waiterCallsLiveData.postValue(copy);
        }
    }

    // ── Menu Operations ───────────────────────────────────────────────

    @Override
    public LiveData<List<MenuItem>> getMenuItems() {
        return menuItemsLiveData;
    }

    @Override
    public synchronized void addMenuItem(MenuItem item) {
        if (item == null) {
            return;
        }
        if (item.getId() <= 0) {
            item.setId(generateId());
        }
        menuItems.add(item);
        saveMenuItems();
        notifyMenuItemsChanged();
    }

    @Override
    public synchronized void updateMenuItem(MenuItem item) {
        if (item == null) {
            return;
        }
        for (int i = 0; i < menuItems.size(); i++) {
            if (menuItems.get(i).getId() == item.getId()) {
                menuItems.set(i, item);
                saveMenuItems();
                notifyMenuItemsChanged();
                return;
            }
        }
    }

    @Override
    public synchronized void deleteMenuItem(int itemId) {
        boolean removed = false;
        for (int i = 0; i < menuItems.size(); i++) {
            if (menuItems.get(i).getId() == itemId) {
                menuItems.remove(i);
                removed = true;
                break;
            }
        }
        if (removed) {
            saveMenuItems();
            notifyMenuItemsChanged();
        }
    }

    @Override
    public synchronized MenuItem getMenuItemById(int id) {
        for (MenuItem item : menuItems) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    // ── Order Operations ──────────────────────────────────────────────

    @Override
    public LiveData<List<Order>> getOrders() {
        return ordersLiveData;
    }

    @Override
    public synchronized void addOrder(Order order) {
        if (order == null) {
            return;
        }
        if (order.getId() <= 0) {
            order.setId(generateId());
        }
        orders.add(order);
        saveOrders();
        notifyOrdersChanged();
    }

    @Override
    public synchronized void updateOrder(Order order) {
        if (order == null) {
            return;
        }
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getId() == order.getId()) {
                orders.set(i, order);
                saveOrders();
                notifyOrdersChanged();
                return;
            }
        }
    }

    @Override
    public synchronized Order getOrderById(int id) {
        for (Order order : orders) {
            if (order.getId() == id) {
                return order;
            }
        }
        return null;
    }

    @Override
    public synchronized List<Order> getOrdersByTable(int tableNumber) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders) {
            if (order.getTableNumber() == tableNumber) {
                result.add(order);
            }
        }
        return result;
    }

    /**
     * Accepts an incoming order after verifying inventory stock.
     * Synchronized to guarantee atomic inventory check-and-decrement.
     *
     * 1. Find the order by ID
     * 2. For each OrderItem, find the MenuItem and check stockQuantity >= qty
     * 3. If ANY item has insufficient stock, return false (don't change anything)
     * 4. If all OK: decrement stockQuantity for each item. If stockQuantity reaches 0, set available=false.
     * 5. Set order status=ACCEPTED, estimatedMinutes, acceptedAt=System.currentTimeMillis()
     * 6. Save everything, notify LiveData
     * 7. Return true
     */
    @Override
    public synchronized boolean acceptOrder(int orderId, int estimatedMinutes) {
        // Step 1: Find order
        Order order = getOrderById(orderId);
        if (order == null) {
            return false;
        }

        // Aggregate quantities in case an item appears in multiple lines
        Map<Integer, Integer> neededQuantities = new LinkedHashMap<>();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                int currentQty = neededQuantities.getOrDefault(item.getItemId(), 0);
                neededQuantities.put(item.getItemId(), currentQty + item.getQty());
            }
        }

        // Step 2 & 3: Check stock for each item; abort if any insufficient
        for (Map.Entry<Integer, Integer> entry : neededQuantities.entrySet()) {
            MenuItem menuItem = getMenuItemById(entry.getKey());
            if (menuItem == null || menuItem.getStockQuantity() < entry.getValue()) {
                return false;
            }
        }

        // Step 4: Decrement stock for each item; set available=false if stock reaches 0
        for (Map.Entry<Integer, Integer> entry : neededQuantities.entrySet()) {
            MenuItem menuItem = getMenuItemById(entry.getKey());
            if (menuItem != null) {
                int newStock = menuItem.getStockQuantity() - entry.getValue();
                menuItem.setStockQuantity(newStock);
                if (newStock <= 0) {
                    menuItem.setAvailable(false);
                }
            }
        }

        // Step 5: Update order properties
        order.setStatus(OrderStatus.ACCEPTED);
        order.setEstimatedMinutes(estimatedMinutes);
        order.setAcceptedAt(System.currentTimeMillis());

        // Step 6: Save everything and notify LiveData observers
        saveMenuItems();
        notifyMenuItemsChanged();
        saveOrders();
        notifyOrdersChanged();

        // Step 7: Acceptance successful
        return true;
    }

    /**
     * Rejects an order with a reason.
     * If the order was previously ACCEPTED, restores inventory stock.
     */
    @Override
    public synchronized void rejectOrder(int orderId, String reason) {
        Order order = getOrderById(orderId);
        if (order == null) {
            return;
        }

        // If order was previously ACCEPTED, restore inventory stock
        if (order.getStatus() == OrderStatus.ACCEPTED && order.getItems() != null) {
            Map<Integer, Integer> restoredQuantities = new LinkedHashMap<>();
            for (OrderItem item : order.getItems()) {
                int current = restoredQuantities.getOrDefault(item.getItemId(), 0);
                restoredQuantities.put(item.getItemId(), current + item.getQty());
            }

            for (Map.Entry<Integer, Integer> entry : restoredQuantities.entrySet()) {
                MenuItem menuItem = getMenuItemById(entry.getKey());
                if (menuItem != null) {
                    int restoredStock = menuItem.getStockQuantity() + entry.getValue();
                    menuItem.setStockQuantity(restoredStock);
                    if (restoredStock > 0) {
                        menuItem.setAvailable(true);
                    }
                }
            }
            saveMenuItems();
            notifyMenuItemsChanged();
        }

        order.setStatus(OrderStatus.REJECTED);
        order.setRejectReason(reason);

        saveOrders();
        notifyOrdersChanged();
    }

    @Override
    public synchronized void updateOrderStatus(int orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        if (order != null) {
            order.setStatus(newStatus);
            saveOrders();
            notifyOrdersChanged();
        }
    }

    /**
     * Validates whether current stock is sufficient for the specified order.
     *
     * @param order Order to evaluate.
     * @return null if stock is sufficient, or error message (e.g., 'Not enough Pizza (need 2, have 1)').
     */
    @Override
    public synchronized String getInsufficientStockMessage(Order order) {
        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            return null;
        }

        Map<Integer, Integer> neededQuantities = new LinkedHashMap<>();
        Map<Integer, String> itemNames = new LinkedHashMap<>();
        for (OrderItem item : order.getItems()) {
            int current = neededQuantities.getOrDefault(item.getItemId(), 0);
            neededQuantities.put(item.getItemId(), current + item.getQty());
            if (!itemNames.containsKey(item.getItemId())) {
                itemNames.put(item.getItemId(), item.getName());
            }
        }

        List<String> insufficientList = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : neededQuantities.entrySet()) {
            int itemId = entry.getKey();
            int needed = entry.getValue();
            MenuItem menuItem = getMenuItemById(itemId);
            int available = (menuItem != null) ? menuItem.getStockQuantity() : 0;
            String name = (menuItem != null) ? menuItem.getName() : itemNames.get(itemId);

            if (menuItem == null || available < needed) {
                insufficientList.add("Not enough " + name + " (need " + needed + ", have " + available + ")");
            }
        }

        if (insufficientList.isEmpty()) {
            return null;
        }

        return android.text.TextUtils.join(", ", insufficientList);
    }

    // ── Review Operations ─────────────────────────────────────────────

    @Override
    public LiveData<List<Review>> getReviews() {
        return reviewsLiveData;
    }

    @Override
    public synchronized void addReview(Review review) {
        if (review == null) {
            return;
        }
        if (review.getId() <= 0) {
            review.setId(generateId());
        }
        reviews.add(review);
        saveReviews();
        notifyReviewsChanged();
    }

    // ── Waiter Call Operations ────────────────────────────────────────

    @Override
    public LiveData<List<WaiterCall>> getWaiterCalls() {
        return waiterCallsLiveData;
    }

    @Override
    public synchronized void addWaiterCall(WaiterCall call) {
        if (call == null) {
            return;
        }
        if (call.getId() <= 0) {
            call.setId(generateId());
        }
        waiterCalls.add(call);
        saveWaiterCalls();
        notifyWaiterCallsChanged();
    }

    @Override
    public synchronized void handleWaiterCall(int callId) {
        for (WaiterCall call : waiterCalls) {
            if (call.getId() == callId) {
                call.setHandled(true);
                saveWaiterCalls();
                notifyWaiterCallsChanged();
                return;
            }
        }
    }

    // ── Utility ───────────────────────────────────────────────────────

    @Override
    public int generateId() {
        return idCounter.getAndIncrement();
    }
}
