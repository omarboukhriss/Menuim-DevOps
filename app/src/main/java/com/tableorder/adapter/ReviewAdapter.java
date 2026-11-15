package com.tableorder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tableorder.databinding.ItemReviewBinding;
import com.tableorder.model.Review;
import com.tableorder.util.OrderUtils;

import java.util.ArrayList;
import java.util.List;

/** ReviewAdapter — one card per customer review (manager side). */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final List<Review> reviews = new ArrayList<>();

    public void setReviews(List<Review> newReviews) {
        reviews.clear();
        if (newReviews != null) {
            reviews.addAll(newReviews);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReviewBinding b = ItemReviewBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ReviewViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        ItemReviewBinding b = holder.binding;

        // Draw the stars as text: ★★★☆☆
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            stars.append(i <= review.getRating() ? "★" : "☆");
        }
        b.tvStars.setText(stars.toString());

        b.tvMeta.setText("Table " + review.getTableNumber() + "  •  Order #"
                + review.getOrderId() + "  •  " + OrderUtils.formatDateTime(review.getCreatedAt()));
        b.tvItems.setText("Ordered: " + review.getItemsOrdered());

        boolean hasComment = review.getComment() != null && !review.getComment().isEmpty();
        b.tvComment.setVisibility(hasComment ? View.VISIBLE : View.GONE);
        if (hasComment) {
            b.tvComment.setText("\"" + review.getComment() + "\"");
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        final ItemReviewBinding binding;

        ReviewViewHolder(ItemReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
