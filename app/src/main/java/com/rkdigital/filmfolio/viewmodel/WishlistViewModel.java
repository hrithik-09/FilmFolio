package com.rkdigital.filmfolio.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rkdigital.filmfolio.model.Movie;
import com.rkdigital.filmfolio.model.MovieRepository;
import com.rkdigital.filmfolio.model.Reminder;
import com.rkdigital.filmfolio.model.Wishlist;
import com.rkdigital.filmfolio.model.WishlistRepository;

import java.util.List;

public class WishlistViewModel extends AndroidViewModel {
    private WishlistRepository wishlistRepository;

    public WishlistViewModel(@NonNull Application application,String userId) {
        super(application);
        this.wishlistRepository = new WishlistRepository(application,userId);
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
}
