package com.rkdigital.filmfolio.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rkdigital.filmfolio.model.Movie;
import com.rkdigital.filmfolio.model.MovieRepository;
import com.rkdigital.filmfolio.model.Reminder;
import com.rkdigital.filmfolio.model.ReminderRepository;
import com.rkdigital.filmfolio.model.Wishlist;
import com.rkdigital.filmfolio.model.WishlistRepository;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private MovieRepository repository;
    private String currentSearchQuery = null;
    private ReminderRepository reminderRepository;
    private WishlistRepository wishlistRepository;
    public MainActivityViewModel(@NonNull Application application,String userId) {
        super(application);
        this.repository = new MovieRepository(application);
        this.reminderRepository = new ReminderRepository(application, userId);
        this.wishlistRepository= new WishlistRepository(application,userId);

    }
    public LiveData<List<Movie>>getAllMovies(){
        return repository.getMutableLiveData();
    }
    public void refreshMovies() {
        if (currentSearchQuery != null && !currentSearchQuery.isEmpty()) {
            repository.searchMovies(currentSearchQuery, 1);
        } else {
            repository.refreshMovies(); // just filter
        }
    }

    public void searchMovies(String query) {
        currentSearchQuery = query;
        repository.searchMovies(query, 1);
    }

    public void clearSearch() {
        currentSearchQuery = null;
        repository.refreshMovies();
    }

    public void loadMoreMovies() {
        if (currentSearchQuery != null) {
            repository.searchMovies(currentSearchQuery, repository.getNextPage());
        } else {
            repository.loadMoreMovies();
        }
    }


    public LiveData<Reminder>getReminderByMovie(int movieId, String userid){
        return reminderRepository.getReminderForMovie(movieId,userid);
    }

    public LiveData<Wishlist>getWishlistByMovie(int movieId, String userid){
        return wishlistRepository.getWishlistForMovie(movieId,userid);
    }
}
