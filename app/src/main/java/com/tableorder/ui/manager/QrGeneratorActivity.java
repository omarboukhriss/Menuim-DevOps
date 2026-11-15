package com.tableorder.ui.manager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.tableorder.databinding.ActivityQrGeneratorBinding;
import com.tableorder.util.Constants;

/**
 * QrGeneratorActivity — generates QR codes for each table.
 *
 * The QR encodes "restaurantName|tableNumber" (e.g., "Cafe Sousse|5").
 * The manager can select a table number from a spinner (1-20),
 * generate the QR, and show it on-screen (or print it).
 *
 * Uses ZXing's MultiFormatWriter to create the QR bitmap.
 */
public class QrGeneratorActivity extends AppCompatActivity {

    private ActivityQrGeneratorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrGeneratorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Populate spinner with table numbers 1-20
        String[] tables = new String[20];
        for (int i = 0; i < 20; i++) {
            tables[i] = "Table " + (i + 1);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, tables);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTable.setAdapter(adapter);

        // Generate button
        binding.btnGenerate.setOnClickListener(v -> generateQr());

        // Auto-generate for table 1 on launch
        generateQr();
    }

    /**
     * Generates a QR code bitmap from the selected table number
     * and displays it in the ImageView.
     */
    private void generateQr() {
        // Table numbers are 1-indexed (spinner position + 1)
        int tableNumber = binding.spinnerTable.getSelectedItemPosition() + 1;

        // Build the QR data string: "Cafe Sousse|5"
        String data = Constants.DEFAULT_RESTAURANT_NAME
                + Constants.QR_SEPARATOR + tableNumber;

        try {
            // ZXing: encode the string as a QR code bitmap
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix matrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);

            // Display the QR code and the encoded data text
            binding.ivQrCode.setImageBitmap(bitmap);
            binding.tvQrData.setText("Encoded: " + data);
        } catch (Exception e) {
            Toast.makeText(this, "Error generating QR: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
