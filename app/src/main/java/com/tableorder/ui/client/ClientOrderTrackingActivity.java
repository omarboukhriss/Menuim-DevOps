package com.tableorder.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.tableorder.databinding.ActivityClientOrderTrackingBinding;
import com.tableorder.model.Order;
import com.tableorder.model.OrderStatus;
import com.tableorder.util.Constants;
import com.tableorder.util.OrderUtils;
import com.tableorder.viewmodel.OrderViewModel;
import com.tableorder.viewmodel.WaiterCallViewModel;

import java.util.List;
import java.util.Locale;

/**
 * ClientOrderTrackingActivity — Stage 7.
 *
 * Watches ONE order live (LiveData) and shows:
 *   PENDING    "Waiting for the restaurant..."
 *   ACCEPTED   "Estimated time: X min" + CountDownTimer computed from acceptedAt
 *   REJECTED   the reason given by the manager
 *   PREPARING  being prepared (countdown continues)
 *   READY      ready, on its way
 *   SERVED     enjoy! + "Rate your experience" button (once per order)
 * Always available: "Order again" and "Call waiter".
 *
 * Receives: EXTRA_ORDER_ID, EXTRA_TABLE_NUMBER, EXTRA_RESTAURANT_NAME.
 */
public class ClientOrderTrackingActivity extends AppCompatActivity {

    private ActivityClientOrderTrackingBinding binding;
    private WaiterCallViewModel waiterViewModel;

    private int orderId;
    private int tableNumber;
    private String restaurantName;

    // The running timer (null when no countdown is active)
    private CountDownTimer timer;
    // The acceptedAt value the current timer was built for.
    // If it is the same on the next LiveData update we keep the timer running.
    private long timerAcceptedAt = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClientOrderTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderId = getIntent().getIntExtra(Constants.EXTRA_ORDER_ID, -1);
        tableNumber = getIntent().getIntExtra(Constants.EXTRA_TABLE_NUMBER, -1);
        restaurantName = getIntent().getStringExtra(Constants.EXTRA_RESTAURANT_NAME);
        if (restaurantName == null) {
            restaurantName = Constants.DEFAULT_RESTAURANT_NAME;
        }

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        OrderViewModel orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        waiterViewModel = new ViewModelProvider(this).get(WaiterCallViewModel.class);

        // Every change of any order triggers this; we only care about ours
        orderViewModel.getOrders().observe(this, orders -> {
            Order order = findOrder(orders);
            if (order == null) {
                binding.tvStatusTitle.setText("Order not found");
                return;
            }
            showOrder(order);
        });

        // Order again: open a fresh menu for the same restaurant and table
        binding.btnOrderAgain.setOnClickListener(v -> {
            Intent intent = new Intent(this, ClientMenuActivity.class);
            intent.putExtra(Constants.EXTRA_RESTAURANT_NAME, restaurantName);
            intent.putExtra(Constants.EXTRA_TABLE_NUMBER, tableNumber);
            startActivity(intent);
            finish();
        });

        // Call waiter: records a WaiterCall that the manager sees
        binding.btnCallWaiter.setOnClickListener(v -> {
            waiterViewModel.addWaiterCall(tableNumber);
            Toast.makeText(this, "A waiter is on the way to table " + tableNumber,
                    Toast.LENGTH_SHORT).show();
            // Avoid spamming: disable the button after one tap
            binding.btnCallWaiter.setEnabled(false);
            binding.btnCallWaiter.setText("Waiter called ✓");
        });

        // Rate: open the review screen for this order
        binding.btnReview.setOnClickListener(v -> {
            Intent intent = new Intent(this, ClientReviewActivity.class);
            intent.putExtra(Constants.EXTRA_ORDER_ID, orderId);
            startActivity(intent);
        });
    }

    private Order findOrder(List<Order> orders) {
        if (orders == null) {
            return null;
        }
        for (Order o : orders) {
            if (o.getId() == orderId) {
                return o;
            }
        }
        return null;
    }

    /** Fills the screen according to the order status. */
    private void showOrder(Order order) {
        OrderStatus status = order.getStatus();

        binding.tvOrderSummary.setText("Order #" + order.getId() + " (Table "
                + order.getTableNumber() + ")\n"
                + OrderUtils.itemsSummary(order) + "\n"
                + String.format(Locale.US, "Total: %.2f TND", order.getTotal()));

        // By default no countdown and no review button
        binding.tvCountdown.setVisibility(View.GONE);
        binding.btnReview.setVisibility(View.GONE);

        switch (status) {
            case PENDING:
                stopTimer();
                binding.tvStatusTitle.setText("⏳ Pending");
                binding.tvStatusDetail.setText("Waiting for the restaurant to accept your order...");
                break;

            case ACCEPTED:
                binding.tvStatusTitle.setText("✅ Accepted");
                binding.tvStatusDetail.setText("Estimated time: " + order.getEstimatedMinutes() + " min");
                startCountdown(order);
                break;

            case REJECTED:
                stopTimer();
                binding.tvStatusTitle.setText("❌ Rejected");
                binding.tvStatusDetail.setText("Reason: " + order.getRejectReason());
                break;

            case PREPARING:
                binding.tvStatusTitle.setText("👨‍🍳 Preparing");
                binding.tvStatusDetail.setText("The kitchen is preparing your order.");
                startCountdown(order);
                break;

            case READY:
                stopTimer();
                binding.tvStatusTitle.setText("🍽️ Ready");
                binding.tvStatusDetail.setText("Your order is ready and will be at your table in a moment.");
                break;

            case SERVED:
                stopTimer();
                binding.tvStatusTitle.setText("😋 Served");
                binding.tvStatusDetail.setText("Enjoy your meal!");
                // Review is allowed only once per order
                binding.btnReview.setVisibility(order.isReviewed() ? View.GONE : View.VISIBLE);
                if (order.isReviewed()) {
                    binding.tvStatusDetail.setText("Enjoy your meal! Thanks for your review.");
                }
                break;
        }
    }

    // ── Countdown ──────────────────────────────────────────────────

    /**
     * Starts (or keeps) a countdown that ends at acceptedAt + estimatedMinutes.
     * Because it is based on acceptedAt (not on "now + X"), reopening the screen
     * shows the correct remaining time.
     */
    private void startCountdown(Order order) {
        // Same acceptance as the running timer -> nothing to do
        if (timer != null && timerAcceptedAt == order.getAcceptedAt()) {
            binding.tvCountdown.setVisibility(View.VISIBLE);
            return;
        }
        stopTimer();

        long endTime = order.getAcceptedAt() + order.getEstimatedMinutes() * 60_000L;
        long remaining = endTime - System.currentTimeMillis();

        binding.tvCountdown.setVisibility(View.VISIBLE);
        if (remaining <= 0) {
            binding.tvCountdown.setText("Almost there...");
            return;
        }

        timerAcceptedAt = order.getAcceptedAt();
        timer = new CountDownTimer(remaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long totalSeconds = millisUntilFinished / 1000;
                binding.tvCountdown.setText(String.format(Locale.US, "%02d:%02d",
                        totalSeconds / 60, totalSeconds % 60));
            }

            @Override
            public void onFinish() {
                binding.tvCountdown.setText("Almost there...");
            }
        }.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timerAcceptedAt = -1;
    }

    // Always cancel the timer, otherwise it would keep running after the screen is closed
    @Override
    protected void onDestroy() {
        stopTimer();
        super.onDestroy();
    }
}
