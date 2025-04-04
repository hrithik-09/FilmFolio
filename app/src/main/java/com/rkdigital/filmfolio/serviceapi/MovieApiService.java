package com.rkdigital.filmfolio.serviceapi;

import com.rkdigital.filmfolio.model.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieApiService {

    @GET("movie/popular")
    Call<Result>getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page);

    @GET("discover/movie")
    Call<Result> getFilteredMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page,
            @Query("with_genres") String genreIds,
            @Query("sort_by") String sortBy,
            @Query("primary_release_date.gte") String releaseDateFrom,
            @Query("primary_release_date.lte") String releaseDateTo,
            @Query("vote_average.gte") int minRating
    );

}
