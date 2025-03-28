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
    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        this.repository = new MovieRepository(application);
    }
    public LiveData<List<Movie>>getAllMovies(){
        return repository.getMutableLiveData();
    }
    public void loadMoreMovies() {
        repository.loadMoreMovies();
    }

}
