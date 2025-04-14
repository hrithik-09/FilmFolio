package com.rkdigital.filmfolio.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rkdigital.filmfolio.model.MovieDetail;
import com.rkdigital.filmfolio.model.MovieRepository;
import com.rkdigital.filmfolio.serviceapi.RetrofitInstance;

public class MovieAboutViewModel extends AndroidViewModel {

    private final MovieRepository repository;
    private MutableLiveData<MovieDetail> movieDetailLiveData;

    public MovieAboutViewModel(@NonNull Application application) {
        super(application);
        repository = new MovieRepository(application);
    }

    public LiveData<MovieDetail> getMovieDetails(int movieId) {
        if (movieDetailLiveData == null) {
            movieDetailLiveData = (MutableLiveData<MovieDetail>) repository.getMovieDetails(movieId);
        }
        return movieDetailLiveData;
    }

}

