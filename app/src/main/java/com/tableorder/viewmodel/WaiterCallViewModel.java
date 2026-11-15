package com.tableorder.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.tableorder.model.WaiterCall;
import com.tableorder.repository.DataRepository;
import com.tableorder.repository.IDataRepository;
import java.util.List;

/**
 * WaiterCallViewModel manages table waiter assistance requests.
 * Exposes active and historical waiter calls and methods to summon staff or mark calls as handled.
 */
public class WaiterCallViewModel extends AndroidViewModel {

    private final IDataRepository repository;

    /**
     * Constructs WaiterCallViewModel and initializes repository singleton.
     *
     * @param application Application instance.
     */
    public WaiterCallViewModel(@NonNull Application application) {
        super(application);
        this.repository = DataRepository.getInstance(application);
    }

    /**
     * Observes the list of waiter assistance calls.
     *
     * @return LiveData list of WaiterCall objects.
     */
    public LiveData<List<WaiterCall>> getWaiterCalls() {
        return repository.getWaiterCalls();
    }

    /**
     * Creates and records a new waiter assistance call for the given table.
     *
     * @param tableNumber Table number requesting service.
     */
    public void addWaiterCall(int tableNumber) {
        int callId = repository.generateId();
        WaiterCall call = new WaiterCall(callId, tableNumber);
        repository.addWaiterCall(call);
    }

    /**
     * Records an already constructed WaiterCall object.
     *
     * @param call WaiterCall instance.
     */
    public void addWaiterCall(WaiterCall call) {
        repository.addWaiterCall(call);
    }

    /**
     * Marks an assistance request as handled by waitstaff.
     *
     * @param callId ID of the call to resolve.
     */
    public void handleWaiterCall(int callId) {
        repository.handleWaiterCall(callId);
    }
}
