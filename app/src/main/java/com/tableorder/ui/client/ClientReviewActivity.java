package com.tableorder.ui.client;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.tableorder.databinding.ActivityClientReviewBinding;
import com.tableorder.model.Order;
import com.tableorder.model.OrderStatus;
import com.tableorder.model.Review;
import com.tableorder.util.Constants;
import com.tableorder.util.OrderUtils;
import com.tableorder.viewmodel.OrderViewModel;
import com.tableorder.viewmodel.ReviewViewModel;

/**
 * ClientReviewActivity — Stage 8 (client side).
 *
 * Rules:
 *   - only for an order that is SERVED
 *   - only ONCE per order (Order.isReviewed)
 *   - rating from 1 to 5 stars is required, comment is optional
 *
 * Receives: EXTRA_ORDER_ID.
 */
public class ClientReviewActivity extends AppCompatActivity {

    private ActivityClientReviewBinding binding;
    private OrderViewModel orderViewModel;
    private ReviewViewModel reviewViewModel;
    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClientReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);

        int orderId = getIntent().getIntExtra(Constants.EXTRA_ORDER_ID, -1);
        order = orderViewModel.getOrderById(orderId);

        // Guard clauses: leave the screen if the review is not allowed
        if (order == null) {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (order.getStatus() != OrderStatus.SERVED) {
            Toast.makeText(this, "You can review only after your order is served",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (order.isReviewed()) {
            Toast.makeText(this, "You already reviewed this order", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        binding.tvOrderInfo.setText("Order #" + order.getId() + " - Table "
                + order.getTableNumber() + "\n" + OrderUtils.itemsSummary(order));

        binding.btnSubmit.setOnClickListener(v -> submit());
    }

    private void submit() {
        int rating = (int) binding.ratingBar.getRating();
        if (rating < 1) {
            Toast.makeText(this, "Please choose a rating (1 to 5 stars)",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String comment = binding.etComment.getText().toString().trim();

        // id = 0 -> the repository generates a real id
        Review review = new Review(0, order.getId(), order.getTableNumber(),
                rating, comment, OrderUtils.itemsSummary(order));
        reviewViewModel.addReview(review);

        // Lock the order so it cannot be reviewed again
        orderViewModel.markReviewed(order.getId());

        Toast.makeText(this, "Thank you for your review!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
