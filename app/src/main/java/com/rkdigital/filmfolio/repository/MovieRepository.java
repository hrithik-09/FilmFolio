package com.rkdigital.filmfolio.repository;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rkdigital.filmfolio.model.Movie;
import com.rkdigital.filmfolio.model.MovieDetail;
import com.rkdigital.filmfolio.model.Result;
import com.rkdigital.filmfolio.model.WishlistMovie;
import com.rkdigital.filmfolio.storage.SharedPreferencesHelper;
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

    public void searchMovies(String query, int page) {
        MovieApiService movieApiService = RetrofitInstance.getService();

        Call<Result> call = movieApiService.searchMovies(
                application.getString(R.string.api_key),
                query,
                page
        );

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Result result = response.body();
                if (result != null && result.getResults() != null) {
                    List<Movie> searchResults = result.getResults();


                    SharedPreferencesHelper prefs = SharedPreferencesHelper.getInstance(application);
                    List<Movie> filteredResults = applyClientSideFilters(searchResults, prefs);

                    if (page == 1) movies.clear();
                    movies.addAll(filteredResults);
                    mutableLiveData.setValue(movies);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e("SEARCH_ERROR", "Search failed: " + t.getMessage());
            }
        });
    }
    private List<Movie> applyClientSideFilters(List<Movie> source, SharedPreferencesHelper prefs) {
        List<Movie> filtered = new ArrayList<>();

        List<Integer> selectedGenres = prefs.getGenres();
        Pair<Integer, Integer> yearRange = prefs.getYearRange();
        int minRating = prefs.getMinRating();

        for (Movie movie : source) {
            boolean matchesGenre = selectedGenres.isEmpty() || movieHasAnyGenre(movie, selectedGenres);
            boolean matchesYear = movie.getReleaseDate() != null && isInYearRange(movie.getReleaseDate(), yearRange);
            boolean matchesRating = movie.getVoteAverage() >= minRating;

            if (matchesGenre && matchesYear && matchesRating) {
                filtered.add(movie);
            }
        }

        return filtered;
    }

    private boolean movieHasAnyGenre(Movie movie, List<Integer> genreIndexes) {
        Map<Integer, String> genreMap = getGenreMap();
        for (int index : genreIndexes) {
            if (movie.getGenreIds().contains(Integer.parseInt(genreMap.get(index)))) {
                return true;
            }
        }
        return false;
    }

    private boolean isInYearRange(String date, Pair<Integer, Integer> yearRange) {
        try {
            int year = Integer.parseInt(date.substring(0, 4));
            return year >= yearRange.first && year <= yearRange.second;
        } catch (Exception e) {
            return false;
        }
    }

    private Map<Integer, String> getGenreMap() {
        Map<Integer, String> genreMap = new HashMap<>();
        genreMap.put(0, "28");
        genreMap.put(1, "35");
        genreMap.put(2, "18");
        genreMap.put(3, "27");
        genreMap.put(4, "878");
        genreMap.put(5, "53");
        genreMap.put(6, "10749");
        return genreMap;
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
                    if (page == 1) movies.clear();
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
    public int getNextPage() {
        return ++currentPage;
    }
    public LiveData<MovieDetail> getMovieDetails(int movieId)
        {
        MutableLiveData<MovieDetail> movieDetailLiveData = new MutableLiveData<>();
        MovieApiService apiService = RetrofitInstance.getService();
        apiService.getMovieDetails(movieId, application.getString(R.string.api_key), "credits").enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                if (response.isSuccessful()) {
                    movieDetailLiveData.setValue(response.body());
                } else {
                    movieDetailLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                movieDetailLiveData.setValue(null);
            }
        });

        return movieDetailLiveData;
    }

    public LiveData<WishlistMovie> getWishlistMovieDetails(int movieId)
    {
        MutableLiveData<WishlistMovie> wishlistMovieMutableLiveData = new MutableLiveData<>();
        MovieApiService apiService = RetrofitInstance.getService();
        apiService.getMovieWishlistDetails(movieId, application.getString(R.string.api_key), "credits").enqueue(new Callback<WishlistMovie>() {
            @Override
            public void onResponse(Call<WishlistMovie> call, Response<WishlistMovie> response) {
                if (response.isSuccessful()) {
                    wishlistMovieMutableLiveData.setValue(response.body());
                } else {
                    wishlistMovieMutableLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<WishlistMovie> call, Throwable t) {
                wishlistMovieMutableLiveData.setValue(null);
            }
        });

        return wishlistMovieMutableLiveData;
    }

}
