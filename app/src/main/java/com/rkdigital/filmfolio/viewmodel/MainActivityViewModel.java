package com.rkdigital.filmfolio.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rkdigital.filmfolio.model.Movie;
import com.rkdigital.filmfolio.model.MovieRepository;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private MovieRepository repository;
    private String currentSearchQuery = null;
    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        this.repository = new MovieRepository(application);
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



}
