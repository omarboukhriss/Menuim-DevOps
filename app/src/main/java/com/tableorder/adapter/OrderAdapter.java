package com.tableorder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tableorder.databinding.ItemOrderBinding;
import com.tableorder.model.Order;
import com.tableorder.model.OrderStatus;
import com.tableorder.util.OrderUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * OrderAdapter — one card per order on the manager's orders screen.
 *
 * Buttons depend on the status:
 *   PENDING    -> Accept, Reject
 *   ACCEPTED   -> "Start preparing", Reject
 *   PREPARING  -> "Mark ready"
 *   READY      -> "Mark served"
 *   SERVED / REJECTED -> no buttons
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    /** The Activity implements this to react to the buttons. */
    public interface Listener {
        void onAccept(Order order);
        void onReject(Order order);
        void onNextStatus(Order order, OrderStatus nextStatus);
    }

    private final List<Order> orders = new ArrayList<>();
    private final Listener listener;

    public OrderAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setOrders(List<Order> newOrders) {
        orders.clear();
        if (newOrders != null) {
            orders.addAll(newOrders);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding b = ItemOrderBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        ItemOrderBinding b = holder.binding;

        b.tvTitle.setText("Order #" + order.getId() + "  •  Table " + order.getTableNumber());

        // Colored status badge
        b.tvStatus.setText(OrderUtils.statusLabel(order.getStatus()));
        b.tvStatus.setBackgroundColor(ContextCompat.getColor(
                b.getRoot().getContext(), OrderUtils.statusColorRes(order.getStatus())));

        b.tvItems.setText("Table " + order.getTableNumber() + " ordered: "
                + OrderUtils.itemsSummary(order));
        b.tvTotalTime.setText(String.format(Locale.US, "Total: %.2f TND  •  %s",
                order.getTotal(), OrderUtils.formatTime(order.getCreatedAt())));

        // Rejection reason
        boolean rejected = order.getStatus() == OrderStatus.REJECTED;
        b.tvReason.setVisibility(rejected ? View.VISIBLE : View.GONE);
        if (rejected) {
            b.tvReason.setText("Reason: " + order.getRejectReason());
        }

        bindButtons(b, order);
    }

    /** Shows only the buttons that make sense for the current status. */
    private void bindButtons(ItemOrderBinding b, Order order) {
        OrderStatus status = order.getStatus();

        boolean showAccept = status == OrderStatus.PENDING;
        boolean showReject = status == OrderStatus.PENDING || status == OrderStatus.ACCEPTED;

        // The "next" button moves the order one step forward
        final OrderStatus next;
        String nextText;
        switch (status) {
            case ACCEPTED:
                next = OrderStatus.PREPARING;
                nextText = "Start preparing";
                break;
            case PREPARING:
                next = OrderStatus.READY;
                nextText = "Mark ready";
                break;
            case READY:
                next = OrderStatus.SERVED;
                nextText = "Mark served";
                break;
            default:
                next = null;
                nextText = "";
                break;
        }

        b.btnAccept.setVisibility(showAccept ? View.VISIBLE : View.GONE);
        b.btnReject.setVisibility(showReject ? View.VISIBLE : View.GONE);
        b.btnNext.setVisibility(next != null ? View.VISIBLE : View.GONE);
        b.btnNext.setText(nextText);

        // Hide the whole row when there is nothing to click
        b.layoutButtons.setVisibility(
                (showAccept || showReject || next != null) ? View.VISIBLE : View.GONE);

        b.btnAccept.setOnClickListener(v -> listener.onAccept(order));
        b.btnReject.setOnClickListener(v -> listener.onReject(order));
        b.btnNext.setOnClickListener(v -> listener.onNextStatus(order, next));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        final ItemOrderBinding binding;

        OrderViewHolder(ItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
