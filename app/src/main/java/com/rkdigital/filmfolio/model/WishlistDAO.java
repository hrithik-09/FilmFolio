package com.rkdigital.filmfolio.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WishlistDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertWishlist(Wishlist wishlist);


    @Query("SELECT * FROM wishlist WHERE userId = :userId")
    LiveData<List<Wishlist>> getWishlistByUser(String userId);

    @Query("SELECT * FROM wishlist WHERE wishlistId = :wishlistId")
    Wishlist getWishlistById(String wishlistId);

    @Query("SELECT * FROM wishlist WHERE movieId = :movieId AND userId = :userId")
    LiveData<Wishlist> getWishlistForMovie(int movieId, String userId);

    @Delete
    void deleteWishlist(Wishlist wishlist);

    @Update
    void updateWishlist(Wishlist wishlist);
    @Query("DELETE FROM wishlist")
    void deleteAllWishlist();
}
