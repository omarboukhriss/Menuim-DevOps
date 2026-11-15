package com.tableorder.ui.manager;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tableorder.databinding.ActivityManagerHomeBinding;

/**
 * ManagerHomeActivity — navigation hub for all manager features.
 *
 * Six cards link to: Orders, Stock, QR Codes, Reviews, Waiter Calls, Dashboard.
 * This screen is only reachable after entering the correct PIN.
 */
public class ManagerHomeActivity extends AppCompatActivity {

    private ActivityManagerHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManagerHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Each card navigates to its corresponding management screen
        binding.cardOrders.setOnClickListener(v ->
                startActivity(new Intent(this, ManagerOrdersActivity.class)));

        binding.cardStock.setOnClickListener(v ->
                startActivity(new Intent(this, StockManagementActivity.class)));

        binding.cardQr.setOnClickListener(v ->
                startActivity(new Intent(this, QrGeneratorActivity.class)));

        binding.cardReviews.setOnClickListener(v ->
                startActivity(new Intent(this, ManagerReviewsActivity.class)));

        binding.cardWaiterCalls.setOnClickListener(v ->
                startActivity(new Intent(this, WaiterCallsActivity.class)));

        binding.cardDashboard.setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));
    }
}
