package com.rkdigital.filmfolio.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.rkdigital.filmfolio.repository.MovieRepository;
import com.rkdigital.filmfolio.model.Wishlist;
import com.rkdigital.filmfolio.model.WishlistMovie;
import com.rkdigital.filmfolio.repository.WishlistRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WishlistViewModel extends AndroidViewModel {
    private WishlistRepository wishlistRepository;
    private final MovieRepository movieRepository;
    private MutableLiveData<WishlistMovie> movieDetailLiveData;
    private final Map<Integer, LiveData<WishlistMovie>> movieCache = new HashMap<>();

    public WishlistViewModel(@NonNull Application application,String userId) {
        super(application);
        this.wishlistRepository = new WishlistRepository(application,userId);
        this.movieRepository = new MovieRepository(application);
    }

    public LiveData<List<Wishlist>>getWishlistByUser(String userId)
    {
        return wishlistRepository.getWishlistByUser(userId);
    }

    public void addNewWishlist(Wishlist wishlist){
        wishlistRepository.syncWishlist(wishlist);
    }

    public void deleteWishlist(Wishlist wishlist){
        wishlistRepository.deleteWishlist(wishlist);
    }

    public LiveData<Wishlist>getWishlistByMovie(int movieId, String userid){
        return wishlistRepository.getWishlistForMovie(movieId,userid);
    }
    public void clearLocalWishlist(){
        wishlistRepository.clearLocalWishlist();
    }

    public LiveData<WishlistMovie> getMovieDetails(int movieId) {
        if (!movieCache.containsKey(movieId)) {
            MutableLiveData<WishlistMovie> liveData = new MutableLiveData<>();
            movieCache.put(movieId, liveData);
            fetchMovieDetails(movieId, liveData);
        }
        return movieCache.get(movieId);
    }

    private void fetchMovieDetails(int movieId, MutableLiveData<WishlistMovie> liveData) {
        movieRepository.getWishlistMovieDetails(movieId).observeForever(new Observer<WishlistMovie>() {
            @Override
            public void onChanged(WishlistMovie movie) {
                liveData.setValue(movie);
                movieRepository.getWishlistMovieDetails(movieId).removeObserver(this);
            }
        });
    }
}
