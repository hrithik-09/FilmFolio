package com.rkdigital.filmfolio.model;

import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WishlistMovie{
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @SerializedName("vote_average")
    @Expose
    private float voteAverage;

    // Getters
    public int getId() { return id; }

    public String getTitle() { return title; }

    public String getPosterPath() { return posterPath; }

    public float getVoteAverage() { return voteAverage; }

    @BindingAdapter("posterPath")
    public static void setImageUrl(ImageView imageView, String url) {
        if (url != null && !url.isEmpty()) {
            Glide.with(imageView.getContext())
                    .load("https://image.tmdb.org/t/p/w500" + url)
                    .into(imageView);
        }
    }

}
