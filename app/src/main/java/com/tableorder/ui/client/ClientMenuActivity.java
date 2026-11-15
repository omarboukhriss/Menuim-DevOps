package com.tableorder.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tableorder.adapter.MenuAdapter;
import com.tableorder.databinding.ActivityClientMenuBinding;
import com.tableorder.model.MenuItem;
import com.tableorder.model.OrderItem;
import com.tableorder.util.Constants;
import com.tableorder.viewmodel.MenuViewModel;
import com.tableorder.viewmodel.OrderViewModel;

import java.util.List;
import java.util.Locale;

/**
 * ClientMenuActivity — Stage 5.
 *
 * Shows the menu grouped by category. The client picks quantities with +/-,
 * sees a cart summary at the bottom, then confirms. On confirm we create the
 * order (status PENDING) and open the tracking screen.
 *
 * Receives: EXTRA_RESTAURANT_NAME and EXTRA_TABLE_NUMBER (from ClientScanActivity).
 */
public class ClientMenuActivity extends AppCompatActivity implements MenuAdapter.Listener {

    private ActivityClientMenuBinding binding;
    private MenuAdapter adapter;
    private OrderViewModel orderViewModel;

    private String restaurantName;
    private int tableNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClientMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ── Read what the scan screen sent us ──
        restaurantName = getIntent().getStringExtra(Constants.EXTRA_RESTAURANT_NAME);
        tableNumber = getIntent().getIntExtra(Constants.EXTRA_TABLE_NUMBER, -1);
        if (restaurantName == null || tableNumber <= 0) {
            // Should never happen, but better safe than sorry
            Toast.makeText(this, "Missing table information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.toolbar.setTitle(restaurantName + " - Table " + tableNumber);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // ── ViewModels ──
        MenuViewModel menuViewModel = new ViewModelProvider(this).get(MenuViewModel.class);
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        // ── List ──
        adapter = new MenuAdapter(this);
        binding.rvMenu.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMenu.setAdapter(adapter);

        // Menu changes live (e.g. the manager accepts an order and stock drops)
        menuViewModel.getMenuItems().observe(this, items -> {
            adapter.setItems(items);
            boolean empty = items == null || items.isEmpty();
            binding.tvEmpty.setVisibility(empty ? android.view.View.VISIBLE : android.view.View.GONE);
        });

        binding.btnConfirm.setOnClickListener(v -> onConfirmClicked());
        updateCartBar();
    }

    // ── MenuAdapter.Listener ───────────────────────────────────────

    @Override
    public void onCartChanged() {
        updateCartBar();
    }

    @Override
    public void onStockLimit(MenuItem item) {
        Toast.makeText(this,
                "Only " + item.getStockQuantity() + " left of " + item.getName(),
                Toast.LENGTH_SHORT).show();
    }

    // ── Cart bar ───────────────────────────────────────────────────

    private void updateCartBar() {
        int count = adapter.getTotalCount();
        if (count == 0) {
            binding.tvCartSummary.setText("Cart is empty");
        } else {
            binding.tvCartSummary.setText(String.format(Locale.US, "%d item(s)  •  %.2f TND",
                    count, adapter.getTotalPrice()));
        }
    }

    // ── Confirm flow ───────────────────────────────────────────────

    private void onConfirmClicked() {
        // Validation: empty cart
        if (adapter.getTotalCount() == 0) {
            Toast.makeText(this, "Your cart is empty. Add at least one item.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Build a readable summary for the confirmation dialog
        List<OrderItem> items = adapter.getCartAsOrderItems();
        StringBuilder sb = new StringBuilder();
        for (OrderItem item : items) {
            sb.append(item.getQty()).append("x ").append(item.getName())
                    .append("  (").append(String.format(Locale.US, "%.2f", item.getSubtotal()))
                    .append(" TND)\n");
        }
        sb.append("\nTotal: ").append(String.format(Locale.US, "%.2f TND", adapter.getTotalPrice()));

        new AlertDialog.Builder(this)
                .setTitle("Confirm your order")
                .setMessage(sb.toString())
                .setPositiveButton("Confirm", (d, w) -> placeOrder(items))
                .setNegativeButton("Back", null)
                .show();
    }

    private void placeOrder(List<OrderItem> items) {
        // Creates the order with status PENDING and returns its id
        int orderId = orderViewModel.placeOrder(tableNumber, items, adapter.getTotalPrice());

        // Open the tracking screen, then close the menu so "Order again" starts a fresh cart
        Intent intent = new Intent(this, ClientOrderTrackingActivity.class);
        intent.putExtra(Constants.EXTRA_ORDER_ID, orderId);
        intent.putExtra(Constants.EXTRA_TABLE_NUMBER, tableNumber);
        intent.putExtra(Constants.EXTRA_RESTAURANT_NAME, restaurantName);
        startActivity(intent);
        finish();
    }
}
