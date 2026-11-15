package com.tableorder.ui.manager;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tableorder.adapter.StockAdapter;
import com.tableorder.databinding.ActivityStockManagementBinding;
import com.tableorder.model.MenuItem;
import com.tableorder.util.Constants;
import com.tableorder.viewmodel.MenuViewModel;

/**
 * StockManagementActivity — the manager sees every menu item with its stock.
 * Actions: add (FAB), edit, delete, manual restock.
 * The list updates by itself because we observe LiveData from the ViewModel.
 */
public class StockManagementActivity extends AppCompatActivity implements StockAdapter.Listener {

    private ActivityStockManagementBinding binding;
    private MenuViewModel viewModel;
    private StockAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStockManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        viewModel = new ViewModelProvider(this).get(MenuViewModel.class);

        adapter = new StockAdapter(this);
        binding.rvStock.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStock.setAdapter(adapter);

        // Every time the menu changes (add/edit/delete/order accepted), refresh the list
        viewModel.getMenuItems().observe(this, items -> {
            adapter.setItems(items);
            boolean empty = items == null || items.isEmpty();
            binding.tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        });

        // Add button -> open the form with no item id (= "new item" mode)
        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditMenuItemActivity.class)));
    }

    @Override
    public void onEdit(MenuItem item) {
        Intent intent = new Intent(this, AddEditMenuItemActivity.class);
        intent.putExtra(Constants.EXTRA_MENU_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(MenuItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete item")
                .setMessage("Delete \"" + item.getName() + "\" from the menu?")
                .setPositiveButton("Delete", (d, w) -> viewModel.deleteMenuItem(item.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onRestock(MenuItem item) {
        // Small dialog asking how many units to add
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Quantity to add");

        new AlertDialog.Builder(this)
                .setTitle("Restock " + item.getName())
                .setView(input)
                .setPositiveButton("Add", (d, w) -> {
                    String text = input.getText().toString().trim();
                    int qty;
                    try {
                        qty = Integer.parseInt(text);
                    } catch (NumberFormatException e) {
                        qty = 0;
                    }
                    if (qty <= 0) {
                        Toast.makeText(this, "Enter a quantity greater than 0",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Work on a copy so we never edit the list shown on screen directly
                    MenuItem updated = copyOf(item);
                    updated.setStockQuantity(item.getStockQuantity() + qty);
                    updated.setAvailable(true); // stock > 0 again
                    viewModel.updateMenuItem(updated);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private MenuItem copyOf(MenuItem i) {
        return new MenuItem(i.getId(), i.getName(), i.getDescription(), i.getPrice(),
                i.getCategory(), i.isAvailable(), i.getStockQuantity(), i.getLowStockThreshold());
    }
}
