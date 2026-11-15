package com.tableorder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tableorder.databinding.ItemStockBinding;
import com.tableorder.model.MenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * StockAdapter — shows one card per menu item on the manager's stock screen.
 * The Activity gets notified of button clicks through the Listener interface.
 */
public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    /** The Activity implements this to react to the three buttons. */
    public interface Listener {
        void onRestock(MenuItem item);
        void onEdit(MenuItem item);
        void onDelete(MenuItem item);
    }

    private final List<MenuItem> items = new ArrayList<>();
    private final Listener listener;

    public StockAdapter(Listener listener) {
        this.listener = listener;
    }

    /** Replaces the list content and refreshes the screen. */
    public void setItems(List<MenuItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStockBinding b = ItemStockBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new StockViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        MenuItem item = items.get(position);
        ItemStockBinding b = holder.binding;

        b.tvName.setText(item.getName());
        b.tvInfo.setText(String.format(Locale.US, "%s  •  %.2f TND",
                item.getCategory(), item.getPrice()));
        b.tvStock.setText("Stock: " + item.getStockQuantity()
                + (item.isAvailable() ? "" : "  (not available)"));

        // Red badge when stock is at or below the threshold
        boolean low = item.getStockQuantity() <= item.getLowStockThreshold();
        b.tvLowStock.setVisibility(low ? View.VISIBLE : View.GONE);

        b.btnRestock.setOnClickListener(v -> listener.onRestock(item));
        b.btnEdit.setOnClickListener(v -> listener.onEdit(item));
        b.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class StockViewHolder extends RecyclerView.ViewHolder {
        final ItemStockBinding binding;

        StockViewHolder(ItemStockBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
