package com.tableorder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tableorder.databinding.ItemMenuHeaderBinding;
import com.tableorder.databinding.ItemMenuRowBinding;
import com.tableorder.model.MenuItem;
import com.tableorder.model.OrderItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * MenuAdapter — client menu list grouped by category.
 *
 * The list contains two kinds of rows:
 *   - a String        -> category header ("Hot Drinks")
 *   - a MenuItem      -> an orderable item with  - qty +  buttons
 *
 * The adapter also owns the CART: a map of itemId -> quantity.
 * The Activity is told (via Listener) every time the cart changes.
 */
public class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /** Implemented by the Activity. */
    public interface Listener {
        /** Called after any +/- click so the cart bar can be refreshed. */
        void onCartChanged();
        /** Called when the client tries to add more than what is in stock. */
        void onStockLimit(MenuItem item);
    }

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    // Mix of String (headers) and MenuItem (rows)
    private final List<Object> rows = new ArrayList<>();
    // The cart: itemId -> quantity chosen
    private final Map<Integer, Integer> cart = new HashMap<>();
    // Needed to rebuild OrderItems (name + price) from ids
    private final Map<Integer, MenuItem> itemsById = new HashMap<>();

    private final Listener listener;

    public MenuAdapter(Listener listener) {
        this.listener = listener;
    }

    // ── Data ───────────────────────────────────────────────────────

    /** Replaces the menu content, grouping items by category. */
    public void setItems(List<MenuItem> menuItems) {
        rows.clear();
        itemsById.clear();

        // LinkedHashMap keeps categories in the order they first appear
        Map<String, List<MenuItem>> grouped = new LinkedHashMap<>();
        if (menuItems != null) {
            for (MenuItem item : menuItems) {
                itemsById.put(item.getId(), item);
                String cat = (item.getCategory() == null || item.getCategory().isEmpty())
                        ? "Other" : item.getCategory();
                if (!grouped.containsKey(cat)) {
                    grouped.put(cat, new ArrayList<>());
                }
                grouped.get(cat).add(item);
            }
        }
        for (Map.Entry<String, List<MenuItem>> entry : grouped.entrySet()) {
            rows.add(entry.getKey());
            rows.addAll(entry.getValue());
        }

        // Clean the cart: remove items that disappeared or are no longer orderable,
        // and reduce quantities that are now above the stock.
        List<Integer> toRemove = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
            MenuItem item = itemsById.get(e.getKey());
            if (item == null || !isOrderable(item)) {
                toRemove.add(e.getKey());
            } else if (e.getValue() > item.getStockQuantity()) {
                e.setValue(item.getStockQuantity());
            }
        }
        for (Integer id : toRemove) {
            cart.remove(id);
        }

        notifyDataSetChanged();
        listener.onCartChanged();
    }

    /** An item can be ordered only if it is available AND has stock. */
    private boolean isOrderable(MenuItem item) {
        return item.isAvailable() && item.getStockQuantity() > 0;
    }

    // ── Cart helpers (used by the Activity) ────────────────────────

    /** Total number of units in the cart. */
    public int getTotalCount() {
        int count = 0;
        for (int qty : cart.values()) {
            count += qty;
        }
        return count;
    }

    /** Total price of the cart. */
    public double getTotalPrice() {
        double total = 0;
        for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
            MenuItem item = itemsById.get(e.getKey());
            if (item != null) {
                total += item.getPrice() * e.getValue();
            }
        }
        return total;
    }

    /** Converts the cart into the OrderItem list expected by OrderViewModel.placeOrder. */
    public List<OrderItem> getCartAsOrderItems() {
        List<OrderItem> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
            MenuItem item = itemsById.get(e.getKey());
            if (item != null && e.getValue() > 0) {
                result.add(new OrderItem(item.getId(), item.getName(), e.getValue(), item.getPrice()));
            }
        }
        return result;
    }

    // ── RecyclerView plumbing ──────────────────────────────────────

    @Override
    public int getItemViewType(int position) {
        return rows.get(position) instanceof String ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            return new HeaderHolder(ItemMenuHeaderBinding.inflate(inflater, parent, false));
        }
        return new ItemHolder(ItemMenuRowBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object row = rows.get(position);

        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).binding.tvCategory.setText((String) row);
            return;
        }

        MenuItem item = (MenuItem) row;
        ItemMenuRowBinding b = ((ItemHolder) holder).binding;

        b.tvName.setText(item.getName());
        b.tvDescription.setText(item.getDescription());
        b.tvDescription.setVisibility(
                item.getDescription() == null || item.getDescription().isEmpty()
                        ? View.GONE : View.VISIBLE);
        b.tvPrice.setText(String.format(Locale.US, "%.2f TND", item.getPrice()));

        // Out of stock -> hide the buttons, show the red label
        boolean orderable = isOrderable(item);
        b.layoutQty.setVisibility(orderable ? View.VISIBLE : View.GONE);
        b.tvOutOfStock.setVisibility(orderable ? View.GONE : View.VISIBLE);

        int qty = cart.containsKey(item.getId()) ? cart.get(item.getId()) : 0;
        b.tvQty.setText(String.valueOf(qty));

        b.btnPlus.setOnClickListener(v -> {
            int current = cart.containsKey(item.getId()) ? cart.get(item.getId()) : 0;
            if (current >= item.getStockQuantity()) {
                listener.onStockLimit(item);   // cannot order more than the stock
                return;
            }
            cart.put(item.getId(), current + 1);
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) notifyItemChanged(pos);
            listener.onCartChanged();
        });

        b.btnMinus.setOnClickListener(v -> {
            int current = cart.containsKey(item.getId()) ? cart.get(item.getId()) : 0;
            if (current <= 0) {
                return;
            }
            if (current == 1) {
                cart.remove(item.getId());
            } else {
                cart.put(item.getId(), current - 1);
            }
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) notifyItemChanged(pos);
            listener.onCartChanged();
        });
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        final ItemMenuHeaderBinding binding;

        HeaderHolder(ItemMenuHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        final ItemMenuRowBinding binding;

        ItemHolder(ItemMenuRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
