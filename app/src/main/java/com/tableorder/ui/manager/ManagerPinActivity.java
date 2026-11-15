package com.tableorder.ui.manager;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tableorder.databinding.ActivityManagerPinBinding;
import com.tableorder.util.Constants;

/**
 * ManagerPinActivity — simple PIN gate before accessing management screens.
 *
 * The PIN is hard-coded in Constants.MANAGER_PIN ("1234").
 * If correct, we open ManagerHomeActivity and finish() this screen
 * so the user can't press Back to bypass the PIN.
 */
public class ManagerPinActivity extends AppCompatActivity {

    private ActivityManagerPinBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManagerPinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Back arrow on toolbar
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.btnEnter.setOnClickListener(v -> {
            String pin = binding.etPin.getText().toString().trim();

            if (pin.isEmpty()) {
                binding.tilPin.setError("Please enter the PIN");
                return;
            }

            if (pin.equals(Constants.MANAGER_PIN)) {
                // Correct PIN — open manager home and close this screen
                startActivity(new Intent(this, ManagerHomeActivity.class));
                finish();
            } else {
                // Wrong PIN — show error
                binding.tilPin.setError("Incorrect PIN. Try again.");
                binding.etPin.setText("");
            }
        });

        // Clear error when user starts typing
        binding.etPin.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) binding.tilPin.setError(null);
        });
    }
}
