package com.rkdigital.filmfolio.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "wishlist")
public class Wishlist {
    @PrimaryKey
    @NonNull
    private String wishlistId;
    private int movieId;
    private String userId;
    public Wishlist() {
        this.wishlistId = UUID.randomUUID().toString();
    }

    @NonNull
    public String getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(@NonNull String wishlistId) {
        this.wishlistId = wishlistId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
