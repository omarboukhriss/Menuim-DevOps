package com.tableorder.ui.manager;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tableorder.adapter.WaiterCallAdapter;
import com.tableorder.databinding.ActivityWaiterCallsBinding;
import com.tableorder.model.WaiterCall;
import com.tableorder.viewmodel.WaiterCallViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * WaiterCallsActivity — Stage 8 (manager side).
 * Pending calls first (newest on top), handled calls at the bottom.
 * "Done" marks the call as handled.
 */
public class WaiterCallsActivity extends AppCompatActivity implements WaiterCallAdapter.Listener {

    private ActivityWaiterCallsBinding binding;
    private WaiterCallViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWaiterCallsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(WaiterCallViewModel.class);

        WaiterCallAdapter adapter = new WaiterCallAdapter(this);
        binding.rvCalls.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCalls.setAdapter(adapter);

        viewModel.getWaiterCalls().observe(this, calls -> {
            List<WaiterCall> sorted = calls != null ? new ArrayList<>(calls) : new ArrayList<>();
            // Not handled first, then newest first
            Collections.sort(sorted, (a, b) -> {
                if (a.isHandled() != b.isHandled()) {
                    return a.isHandled() ? 1 : -1;
                }
                return Long.compare(b.getCreatedAt(), a.getCreatedAt());
            });
            adapter.setCalls(sorted);
            binding.tvEmpty.setVisibility(sorted.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDone(WaiterCall call) {
        viewModel.handleWaiterCall(call.getId());
    }
}
