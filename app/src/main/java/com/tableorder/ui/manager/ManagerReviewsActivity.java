package com.tableorder.ui.manager;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tableorder.adapter.ReviewAdapter;
import com.tableorder.databinding.ActivityManagerReviewsBinding;
import com.tableorder.model.Review;
import com.tableorder.viewmodel.ReviewViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * ManagerReviewsActivity — Stage 8 (manager side).
 * Shows the average rating and every review (newest first) with the items ordered.
 */
public class ManagerReviewsActivity extends AppCompatActivity {

    private ActivityManagerReviewsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManagerReviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        ReviewViewModel viewModel = new ViewModelProvider(this).get(ReviewViewModel.class);

        ReviewAdapter adapter = new ReviewAdapter();
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReviews.setAdapter(adapter);

        viewModel.getReviews().observe(this, reviews -> {
            // Copy before sorting: never sort the list that LiveData gave us
            List<Review> sorted = reviews != null ? new ArrayList<>(reviews) : new ArrayList<>();
            Collections.sort(sorted, (a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));
            adapter.setReviews(sorted);

            binding.tvEmpty.setVisibility(sorted.isEmpty() ? View.VISIBLE : View.GONE);

            // Average rating
            if (sorted.isEmpty()) {
                binding.tvAverage.setText("Average rating: -");
            } else {
                double sum = 0;
                for (Review r : sorted) {
                    sum += r.getRating();
                }
                binding.tvAverage.setText(String.format(Locale.US,
                        "★ %.1f / 5  (%d review%s)", sum / sorted.size(), sorted.size(),
                        sorted.size() == 1 ? "" : "s"));
            }
        });
    }
}
