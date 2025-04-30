package com.rkdigital.filmfolio.serviceapi;

import com.rkdigital.filmfolio.model.MovieDetail;
import com.rkdigital.filmfolio.model.Result;
import com.rkdigital.filmfolio.model.WishlistMovie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
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
    @GET("search/movie")
    Call<Result> searchMovies(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("page") int page
    );
    @GET("movie/{movie_id}")
    Call<MovieDetail> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("append_to_response") String extra
    );

    @GET("movie/{movie_id}")
    Call<WishlistMovie>getMovieWishlistDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("append_to_response") String extra
    );

}
