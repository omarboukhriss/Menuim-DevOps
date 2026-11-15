package com.tableorder.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.tableorder.model.Review;
import com.tableorder.repository.DataRepository;
import com.tableorder.repository.IDataRepository;
import java.util.List;

/**
 * ReviewViewModel exposes customer reviews and allows adding new reviews.
 * Observes the reactive review stream from DataRepository.
 */
public class ReviewViewModel extends AndroidViewModel {

    private final IDataRepository repository;

    /**
     * Constructs ReviewViewModel and initializes repository singleton.
     *
     * @param application Application instance.
     */
    public ReviewViewModel(@NonNull Application application) {
        super(application);
        this.repository = DataRepository.getInstance(application);
    }

    /**
     * Observes all customer reviews.
     *
     * @return LiveData list of Review objects.
     */
    public LiveData<List<Review>> getReviews() {
        return repository.getReviews();
    }

    /**
     * Records a new customer review.
     *
     * @param review Review object to persist.
     */
    public void addReview(Review review) {
        repository.addReview(review);
    }
}
