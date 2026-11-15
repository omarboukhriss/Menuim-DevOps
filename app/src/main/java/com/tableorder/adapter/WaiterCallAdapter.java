package com.tableorder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tableorder.databinding.ItemWaiterCallBinding;
import com.tableorder.model.WaiterCall;
import com.tableorder.util.OrderUtils;

import java.util.ArrayList;
import java.util.List;

/** WaiterCallAdapter — one card per waiter call with a "Done" button. */
public class WaiterCallAdapter extends RecyclerView.Adapter<WaiterCallAdapter.CallViewHolder> {

    public interface Listener {
        void onDone(WaiterCall call);
    }

    private final List<WaiterCall> calls = new ArrayList<>();
    private final Listener listener;

    public WaiterCallAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setCalls(List<WaiterCall> newCalls) {
        calls.clear();
        if (newCalls != null) {
            calls.addAll(newCalls);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWaiterCallBinding b = ItemWaiterCallBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CallViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull CallViewHolder holder, int position) {
        WaiterCall call = calls.get(position);
        ItemWaiterCallBinding b = holder.binding;

        b.tvTable.setText("Table " + call.getTableNumber() + " needs a waiter");
        b.tvTime.setText("Called at " + OrderUtils.formatTime(call.getCreatedAt()));

        // Handled calls show a green label instead of the button
        b.btnDone.setVisibility(call.isHandled() ? View.GONE : View.VISIBLE);
        b.tvHandled.setVisibility(call.isHandled() ? View.VISIBLE : View.GONE);
        b.btnDone.setOnClickListener(v -> listener.onDone(call));
    }

    @Override
    public int getItemCount() {
        return calls.size();
    }

    static class CallViewHolder extends RecyclerView.ViewHolder {
        final ItemWaiterCallBinding binding;

        CallViewHolder(ItemWaiterCallBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
