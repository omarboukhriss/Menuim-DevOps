package com.tableorder.ui.manager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.tableorder.databinding.ActivityAddEditMenuItemBinding;
import com.tableorder.model.MenuItem;
import com.tableorder.util.Constants;
import com.tableorder.viewmodel.MenuViewModel;

/**
 * AddEditMenuItemActivity — one form for both "add" and "edit".
 * If an item id is passed in the Intent we are editing, otherwise we are adding.
 */
public class AddEditMenuItemActivity extends AppCompatActivity {

    private ActivityAddEditMenuItemBinding binding;
    private MenuViewModel viewModel;

    // -1 means "no item passed" = add mode
    private int editingId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEditMenuItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        viewModel = new ViewModelProvider(this).get(MenuViewModel.class);

        editingId = getIntent().getIntExtra(Constants.EXTRA_MENU_ITEM_ID, -1);
        if (editingId != -1) {
            MenuItem item = viewModel.getMenuItemById(editingId);
            if (item == null) {            // item was deleted meanwhile
                finish();
                return;
            }
            binding.toolbar.setTitle("Edit item");
            fillForm(item);
        } else {
            // Handy defaults for a new item
            binding.etThreshold.setText("5");
            binding.etStock.setText("10");
        }

        binding.btnSave.setOnClickListener(v -> save());
    }

    /** Puts the existing item's values in the form fields. */
    private void fillForm(MenuItem item) {
        binding.etName.setText(item.getName());
        binding.etDescription.setText(item.getDescription());
        binding.etPrice.setText(String.valueOf(item.getPrice()));
        binding.etCategory.setText(item.getCategory());
        binding.etStock.setText(String.valueOf(item.getStockQuantity()));
        binding.etThreshold.setText(String.valueOf(item.getLowStockThreshold()));
        binding.switchAvailable.setChecked(item.isAvailable());
    }

    /** Validates every field; if all OK, saves the item and closes the screen. */
    private void save() {
        // Clear old errors
        binding.tilName.setError(null);
        binding.tilPrice.setError(null);
        binding.tilCategory.setError(null);
        binding.tilStock.setError(null);
        binding.tilThreshold.setError(null);

        String name = binding.etName.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String priceText = binding.etPrice.getText().toString().trim();
        String category = binding.etCategory.getText().toString().trim();
        String stockText = binding.etStock.getText().toString().trim();
        String thresholdText = binding.etThreshold.getText().toString().trim();

        boolean ok = true;
        if (TextUtils.isEmpty(name)) {
            binding.tilName.setError("Name is required");
            ok = false;
        }
        if (TextUtils.isEmpty(category)) {
            binding.tilCategory.setError("Category is required");
            ok = false;
        }

        double price = 0;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            binding.tilPrice.setError("Enter a price greater than 0");
            ok = false;
        }

        int stock = 0;
        try {
            stock = Integer.parseInt(stockText);
            if (stock < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            binding.tilStock.setError("Enter 0 or more");
            ok = false;
        }

        int threshold = 0;
        try {
            threshold = Integer.parseInt(thresholdText);
            if (threshold < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            binding.tilThreshold.setError("Enter 0 or more");
            ok = false;
        }

        if (!ok) return;

        // Rule: stock 0 means the item can never be "available"
        boolean available = binding.switchAvailable.isChecked() && stock > 0;

        if (editingId == -1) {
            // id 0 -> the repository assigns a fresh unique id
            viewModel.addMenuItem(new MenuItem(0, name, description, price,
                    category, available, stock, threshold));
            Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.updateMenuItem(new MenuItem(editingId, name, description, price,
                    category, available, stock, threshold));
            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
