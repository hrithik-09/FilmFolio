package com.rkdigital.filmfolio.model;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;

import com.rkdigital.filmfolio.SharedPreferencesHelper;
import com.rkdigital.filmfolio.model.Movie;
import com.rkdigital.filmfolio.R;
import com.rkdigital.filmfolio.serviceapi.MovieApiService;
import com.rkdigital.filmfolio.serviceapi.RetrofitInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        SharedPreferencesHelper prefs = SharedPreferencesHelper.getInstance(application);

        // Genre Mapping
        List<Integer> genreIndexes = prefs.getGenres();
        Map<Integer, String> genreMap = new HashMap<>();
        genreMap.put(0, "28");
        genreMap.put(1, "35");
        genreMap.put(2, "18");
        genreMap.put(3, "27");
        genreMap.put(4, "878");
        genreMap.put(5, "53");
        genreMap.put(6, "10749");

        List<String> genreIds = new ArrayList<>();
        for (int index : genreIndexes) {
            if (genreMap.containsKey(index)) {
                genreIds.add(genreMap.get(index));
            }
        }
        String genreParam = TextUtils.join(",", genreIds);

        // Sort mapping
        String sortOption = prefs.getSortOption();
        String sortParam = "popularity.desc"; // default
        if ("Rating".equals(sortOption)) sortParam = "vote_average.desc";
        else if ("Release Date".equals(sortOption)) sortParam = "release_date.desc";

        // Year range
        Pair<Integer, Integer> yearRange = prefs.getYearRange();
        String dateFrom = yearRange.first + "-01-01";
        String dateTo = yearRange.second + "-12-31";

        // Min rating
        int minRating = prefs.getMinRating();

        Call<Result> call = movieApiService.getFilteredMovies(
                application.getString(R.string.api_key),
                page,
                genreParam.isEmpty() ? null : genreParam,
                sortParam,
                dateFrom,
                dateTo,
                minRating
        );

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (result != null && result.getResults() != null) {
                    if (page == 1) movies.clear(); // Reset list on fresh filter apply
                    movies.addAll(result.getResults());
                    mutableLiveData.setValue(movies);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e("API_ERROR", "Filtered movie fetch failed: " + t.getMessage());
            }
        });
    }
    public void refreshMovies() {
        currentPage = 1;
        movies.clear();
        loadMovies(currentPage);
    }
}
