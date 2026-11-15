package com.tableorder.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.tableorder.model.MenuItem;
import com.tableorder.repository.DataRepository;
import com.tableorder.repository.IDataRepository;
import java.util.List;

/**
 * MenuViewModel bridges menu data and business actions between the repository and UI.
 * Exposes reactive LiveData stream of menu items and CRUD methods.
 */
public class MenuViewModel extends AndroidViewModel {

    private final IDataRepository repository;

    /**
     * Constructs MenuViewModel using application context to obtain repository singleton.
     *
     * @param application Application instance.
     */
    public MenuViewModel(@NonNull Application application) {
        super(application);
        this.repository = DataRepository.getInstance(application);
    }

    /**
     * Observes the active menu items.
     *
     * @return LiveData list of MenuItem objects.
     */
    public LiveData<List<MenuItem>> getMenuItems() {
        return repository.getMenuItems();
    }

    /**
     * Adds a new menu item to the catalog.
     *
     * @param item MenuItem to insert.
     */
    public void addMenuItem(MenuItem item) {
        repository.addMenuItem(item);
    }

    /**
     * Updates an existing menu item.
     *
     * @param item MenuItem with updated fields.
     */
    public void updateMenuItem(MenuItem item) {
        repository.updateMenuItem(item);
    }

    /**
     * Deletes a menu item by ID.
     *
     * @param id ID of item to delete.
     */
    public void deleteMenuItem(int id) {
        repository.deleteMenuItem(id);
    }

    /**
     * Retrieves a menu item by ID synchronously.
     *
     * @param id Item ID.
     * @return MenuItem if found, otherwise null.
     */
    public MenuItem getMenuItemById(int id) {
        return repository.getMenuItemById(id);
    }
}
