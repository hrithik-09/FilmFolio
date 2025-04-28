package com.rkdigital.filmfolio.model;
import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;

import java.util.List;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rkdigital.filmfolio.BR;

public class Movie extends BaseObservable {
    @SerializedName("adult")
    @Expose
    private Boolean adult;
    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;
    @SerializedName("genre_ids")
    @Expose
    private List<Integer> genreIds;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("original_language")
    @Expose
    private String originalLanguage;
    @SerializedName("original_title")
    @Expose
    private String originalTitle;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("popularity")
    @Expose
    private Double popularity;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("video")
    @Expose
    private Boolean video;
    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;
    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;
    private boolean wishlisted;
    private boolean reminderSet;

    public boolean isWishlisted() {
        return wishlisted;
    }

    public void setWishlisted(boolean wishlisted) {
        this.wishlisted = wishlisted;
    }

    public boolean hasReminder() {
        return reminderSet;
    }

    public void setReminderSet(boolean reminderSet) {
        this.reminderSet = reminderSet;
    }

    @BindingAdapter({"posterPath"})
    public static void loadImage(ImageView imageView, String imageUrl){
        // Basic Url: "https://image.tmdb.org/t/p/w500/"
        String imagePath = "https://image.tmdb.org/t/p/w500/"+imageUrl;

        Glide.with(imageView.getContext())
                .load(imagePath)
                .into(imageView);
    }
    public Boolean getAdult() {return adult;}
    public void setAdult(Boolean adult) {this.adult = adult;}
    public String getBackdropPath() {return backdropPath;}
    public void setBackdropPath(String backdropPath) {this.backdropPath = backdropPath;}
    public List<Integer> getGenreIds() {return genreIds;}
    public void setGenreIds(List<Integer> genreIds) {this.genreIds = genreIds;}
    @Bindable
    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}
    public String getOriginalLanguage() {return originalLanguage;}
    public void setOriginalLanguage(String originalLanguage) {this.originalLanguage = originalLanguage;}
//    public String getOriginalTitle() {return originalTitle;}
//    public void setOriginalTitle(String originalTitle) {this.originalTitle = originalTitle;}
    @Bindable
    public String getOverview() {return overview;}
    public void setOverview(String overview) {this.overview = overview;
        notifyPropertyChanged(BR.overview);}
    public Double getPopularity() {return popularity;}
    public void setPopularity(Double popularity) {this.popularity = popularity;}
    @Bindable
    public String getPosterPath() {return posterPath;}
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        notifyPropertyChanged(BR.posterPath);
    }
    @Bindable
    public String getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(String releaseDate) {this.releaseDate = releaseDate;notifyPropertyChanged(BR.releaseDate);}
    @Bindable
    public String getTitle() {return title;}
    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }
    public Boolean getVideo() {return video;}
    public void setVideo(Boolean video) {this.video = video;}
    public Double getVoteAverage() {return voteAverage;}
    public void setVoteAverage(Double voteAverage) {this.voteAverage = voteAverage;}
    public Integer getVoteCount() {return voteCount;}
    public void setVoteCount(Integer voteCount) {this.voteCount = voteCount;}

}