package com.tableorder.ui.manager;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tableorder.adapter.OrderAdapter;
import com.tableorder.databinding.ActivityManagerOrdersBinding;
import com.tableorder.model.Order;
import com.tableorder.model.OrderStatus;
import com.tableorder.viewmodel.OrderViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ManagerOrdersActivity — Stage 6.
 *
 * Live list of orders (newest first) with a status filter.
 * Accept -> dialog for estimated minutes (stock is checked by the repository).
 * Reject -> dialog for a reason.
 * Then PREPARING -> READY -> SERVED with one button each.
 */
public class ManagerOrdersActivity extends AppCompatActivity implements OrderAdapter.Listener {

    private ActivityManagerOrdersBinding binding;
    private OrderViewModel viewModel;
    private OrderAdapter adapter;

    // Last list received from LiveData (kept so we can re-filter when a chip is tapped)
    private List<Order> allOrders = new ArrayList<>();

    // null = show all statuses
    private OrderStatus currentFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManagerOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        adapter = new OrderAdapter(this);
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrders.setAdapter(adapter);

        // Filter chips: when the selection changes, map the chip to a status
        binding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            int id = checkedIds.isEmpty() ? binding.chipAll.getId() : checkedIds.get(0);
            if (id == binding.chipPending.getId())        currentFilter = OrderStatus.PENDING;
            else if (id == binding.chipAccepted.getId())  currentFilter = OrderStatus.ACCEPTED;
            else if (id == binding.chipPreparing.getId()) currentFilter = OrderStatus.PREPARING;
            else if (id == binding.chipReady.getId())     currentFilter = OrderStatus.READY;
            else if (id == binding.chipServed.getId())    currentFilter = OrderStatus.SERVED;
            else if (id == binding.chipRejected.getId())  currentFilter = OrderStatus.REJECTED;
            else                                          currentFilter = null;
            refreshList();
        });

        // Live updates: a new client order appears here by itself
        viewModel.getOrders().observe(this, orders -> {
            allOrders = orders != null ? orders : new ArrayList<>();
            refreshList();
        });
    }

    /** Applies the filter, sorts newest first and gives the result to the adapter. */
    private void refreshList() {
        List<Order> shown = new ArrayList<>();
        for (Order order : allOrders) {
            if (currentFilter == null || order.getStatus() == currentFilter) {
                shown.add(order);
            }
        }
        // Newest first
        Collections.sort(shown, (a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));

        adapter.setOrders(shown);
        binding.tvEmpty.setVisibility(shown.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // ── OrderAdapter.Listener ──────────────────────────────────────

    @Override
    public void onAccept(Order order) {
        // Ask for the estimated preparation time (default 15 minutes)
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Estimated minutes");
        input.setText("15");

        new AlertDialog.Builder(this)
                .setTitle("Accept order #" + order.getId())
                .setMessage("How many minutes to prepare it?")
                .setView(input)
                .setPositiveButton("Accept", (d, w) -> {
                    int minutes;
                    try {
                        minutes = Integer.parseInt(input.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        minutes = 0;
                    }
                    if (minutes <= 0) {
                        Toast.makeText(this, "Enter a number of minutes greater than 0",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    tryAccept(order, minutes);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /** Calls the repository; if the stock is not enough, explains why. */
    private void tryAccept(Order order, int minutes) {
        boolean accepted = viewModel.acceptOrder(order.getId(), minutes);
        if (!accepted) {
            String message = viewModel.getInsufficientStockMessage(order.getId());
            if (message == null) {
                message = "Could not accept this order.";
            }
            new AlertDialog.Builder(this)
                    .setTitle("Not enough stock")
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    @Override
    public void onReject(Order order) {
        EditText input = new EditText(this);
        input.setHint("Reason (e.g. kitchen closed)");

        new AlertDialog.Builder(this)
                .setTitle("Reject order #" + order.getId())
                .setView(input)
                .setPositiveButton("Reject", (d, w) -> {
                    String reason = input.getText().toString().trim();
                    if (reason.isEmpty()) {
                        reason = "No reason given";
                    }
                    viewModel.rejectOrder(order.getId(), reason);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onNextStatus(Order order, OrderStatus nextStatus) {
        viewModel.updateOrderStatus(order.getId(), nextStatus);
    }
}
