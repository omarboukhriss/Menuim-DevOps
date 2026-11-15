package com.tableorder.ui.client;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.tableorder.databinding.ActivityClientScanBinding;
import com.tableorder.util.Constants;

/**
 * ClientScanActivity — the client's entry point.
 *
 * Two ways to get a table:
 *   1. "Scan QR Code"  -> opens the camera (ZXing) and reads "restaurantName|tableNumber".
 *   2. "Go to Menu"    -> simulation: type the name + pick a table (works on emulators).
 *
 * Both ways send the text through handleQrData(), so the validation is identical.
 */
public class ClientScanActivity extends AppCompatActivity {

    private ActivityClientScanBinding binding;

    // ZXing scanner launcher. The library asks for camera permission by itself.
    private final ActivityResultLauncher<ScanOptions> scanLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() == null) {
                    // User pressed back in the scanner
                    Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    handleQrData(result.getContents());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClientScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Spinner with table numbers 1..20 (same range as the manager's QR generator)
        String[] tables = new String[20];
        for (int i = 0; i < 20; i++) {
            tables[i] = "Table " + (i + 1);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, tables);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTableNumber.setAdapter(adapter);

        // Real scan
        binding.btnScanQr.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            options.setPrompt("Point the camera at the QR code on your table");
            options.setBeepEnabled(false);
            options.setOrientationLocked(false);
            scanLauncher.launch(options);
        });

        // Simulated scan: build the same text a real QR would contain
        binding.btnSimulate.setOnClickListener(v -> {
            String name = binding.etRestaurantName.getText().toString().trim();
            int table = binding.spinnerTableNumber.getSelectedItemPosition() + 1;
            handleQrData(name + Constants.QR_SEPARATOR + table);
        });
    }

    /**
     * Validates the text read from the QR and opens the menu.
     * Expected format: "restaurantName|tableNumber", e.g. "Cafe Sousse|12".
     */
    private void handleQrData(String data) {
        // Split on the separator. Pattern.quote is not needed: we use indexOf instead of regex.
        int sep = data.lastIndexOf(Constants.QR_SEPARATOR);
        if (sep <= 0 || sep == data.length() - 1) {
            showInvalid();
            return;
        }

        String restaurant = data.substring(0, sep).trim();
        String tableText = data.substring(sep + 1).trim();

        int table;
        try {
            table = Integer.parseInt(tableText);
        } catch (NumberFormatException e) {
            showInvalid();
            return;
        }

        if (restaurant.isEmpty() || table <= 0) {
            showInvalid();
            return;
        }

        // Everything is valid -> open the menu and pass the two values
        Intent intent = new Intent(this, ClientMenuActivity.class);
        intent.putExtra(Constants.EXTRA_RESTAURANT_NAME, restaurant);
        intent.putExtra(Constants.EXTRA_TABLE_NUMBER, table);
        startActivity(intent);
    }

    private void showInvalid() {
        Toast.makeText(this,
                "Invalid QR code. Expected format: RestaurantName|TableNumber",
                Toast.LENGTH_LONG).show();
    }
}
