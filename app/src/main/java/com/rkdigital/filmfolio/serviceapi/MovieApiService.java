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
}
