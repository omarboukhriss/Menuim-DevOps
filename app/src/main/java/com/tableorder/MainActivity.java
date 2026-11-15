package com.tableorder;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tableorder.databinding.ActivityMainBinding;
import com.tableorder.ui.client.ClientScanActivity;
import com.tableorder.ui.manager.ManagerPinActivity;

/**
 * MainActivity — the entry point of the app.
 *
 * Shows two cards: "I'm a Client" and "I'm a Manager".
 * Tapping a card launches the corresponding flow:
 *   - Client → QR scan screen → menu → order → tracking
 *   - Manager → PIN entry → home dashboard with all management tools
 *
 * KEY CONCEPT — ViewBinding:
 *   Instead of findViewById(R.id.cardClient), ViewBinding generates a class
 *   called ActivityMainBinding with a field for every view that has an ID.
 *   This is type-safe (no ClassCastException) and null-safe.
 */
public class MainActivity extends AppCompatActivity {

    // ViewBinding object — gives us direct access to all views in activity_main.xml
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using ViewBinding (replaces setContentView(R.layout...))
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ── Client card click ──────────────────────────────────────
        // Opens the QR scanning screen where the client scans or
        // simulates a table QR code, then browses the menu.
        binding.cardClient.setOnClickListener(v -> {
            startActivity(new Intent(this, ClientScanActivity.class));
        });

        // ── Manager card click ─────────────────────────────────────
        // Opens the PIN entry screen. After entering the correct PIN,
        // the manager accesses the home hub (orders, stock, etc.).
        binding.cardManager.setOnClickListener(v -> {
            startActivity(new Intent(this, ManagerPinActivity.class));
        });
    }
}
