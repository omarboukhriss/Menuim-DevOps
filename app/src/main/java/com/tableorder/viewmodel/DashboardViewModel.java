package com.tableorder.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.tableorder.model.MenuItem;
import com.tableorder.model.Order;
import com.tableorder.model.Review;
import com.tableorder.repository.DataRepository;
import com.tableorder.repository.IDataRepository;
import java.util.List;

/**
 * DashboardViewModel provides high-level restaurant data streams (orders, reviews, menu items)
 * to dashboard and analytics screens.
 *
 * Activities observe these streams to calculate metrics such as daily revenue,
 * order volume, top-selling items, average ratings, and peak hours.
 */
public class DashboardViewModel extends AndroidViewModel {

    private final IDataRepository repository;

    /**
     * Constructs DashboardViewModel and initializes repository singleton.
     *
     * @param application Application instance.
     */
    public DashboardViewModel(@NonNull Application application) {
        super(application);
        this.repository = DataRepository.getInstance(application);
    }

    /**
     * Observes orders to compute revenue, order count, and peak hours.
     *
     * @return LiveData list of orders.
     */
    public LiveData<List<Order>> getOrders() {
        return repository.getOrders();
    }

    /**
     * Observes customer reviews to compute average rating and sentiment.
     *
     * @return LiveData list of reviews.
     */
    public LiveData<List<Review>> getReviews() {
        return repository.getReviews();
    }

    /**
     * Observes menu catalog to cross-reference item names and stock alerts.
     *
     * @return LiveData list of menu items.
     */
    public LiveData<List<MenuItem>> getMenuItems() {
        return repository.getMenuItems();
    }
}
