package com.rkdigital.filmfolio.model;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.rkdigital.filmfolio.model.Movie;
import com.rkdigital.filmfolio.R;
import com.rkdigital.filmfolio.serviceapi.MovieApiService;
import com.rkdigital.filmfolio.serviceapi.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {
    private ArrayList<Movie>movies=new ArrayList<>();
    private MutableLiveData<List<Movie>> mutableLiveData=new MutableLiveData<>();
    private Application application;
    private int currentPage = 1;

    public MovieRepository(Application application) {
        this.application = application;
    }
//    public MutableLiveData<List<Movie>> getMutableLiveData(){
//        MovieApiService movieApiService= RetrofitInstance.getService();
//        Call<Result> call = movieApiService.getPopularMovies(application.getApplicationContext().getString(R.string.api_key));
//        call.enqueue(new Callback<Result>() {
//            @Override
//            public void onResponse(Call<Result> call, Response<Result> response) {
//                Result result = response.body();
//                if (result!=null && result.getResults()!=null){
//                    movies=(ArrayList<Movie>) result.getResults();
//                    mutableLiveData.setValue(movies);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Result> call, Throwable throwable) {
//
//            }
//        });
//        return mutableLiveData;
//    }
    public MutableLiveData<List<Movie>> getMutableLiveData() {
        loadMovies(currentPage);
        return mutableLiveData;
    }

    public void loadMoreMovies() {
        currentPage++;
        loadMovies(currentPage);
    }
    private void loadMovies(int page) {
        MovieApiService movieApiService = RetrofitInstance.getService();
        Call<Result> call = movieApiService.getPopularMovies(
                application.getApplicationContext().getString(R.string.api_key),
                page
        );

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (result != null && result.getResults() != null) {
                    movies.addAll(result.getResults());  // Append new data
                    mutableLiveData.setValue(movies);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable throwable) {
                Log.e("API_ERROR", "Error fetching movies: " + throwable.getMessage());
            }
        });
    }
}
