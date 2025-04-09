package com.rkdigital.filmfolio.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieDetail {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("overview")
    @Expose
    private String overview;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;

    @SerializedName("release_date")
    @Expose
    private String releaseDate;

    @SerializedName("vote_average")
    @Expose
    private float voteAverage;

    @SerializedName("runtime")
    @Expose
    private int runtime;

    @SerializedName("tagline")
    @Expose
    private String tagline;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("genres")
    @Expose
    private List<Genre> genres;

    @SerializedName("production_companies")
    @Expose
    private List<ProductionCompany> productionCompanies;

    @SerializedName("credits")
    @Expose
    private Credits credits;

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getPosterPath() { return posterPath; }
    public String getBackdropPath() { return backdropPath; }
    public String getReleaseDate() { return releaseDate; }
    public float getVoteAverage() { return voteAverage; }
    public int getRuntime() { return runtime; }
    public String getTagline() { return tagline; }
    public String getStatus() { return status; }
    public List<Genre> getGenres() { return genres; }
    public List<ProductionCompany> getProductionCompanies() { return productionCompanies; }
    public Credits getCredits() { return credits; }

    // Inner Classes
    public static class Genre {
        @SerializedName("id")
        @Expose
        private int id;

        @SerializedName("name")
        @Expose
        private String name;

        public int getId() { return id; }
        public String getName() { return name; }
    }

    public static class ProductionCompany {
        @SerializedName("name")
        @Expose
        private String name;

        @SerializedName("logo_path")
        @Expose
        private String logoPath;

        @SerializedName("origin_country")
        @Expose
        private String originCountry;

        public String getName() { return name; }
        public String getLogoPath() { return logoPath; }
        public String getOriginCountry() { return originCountry; }
    }

    public static class Credits {
        @SerializedName("cast")
        @Expose
        private List<Cast> cast;

        public List<Cast> getCast() { return cast; }

        public static class Cast {
            @SerializedName("name")
            @Expose
            private String name;

            @SerializedName("character")
            @Expose
            private String character;

            @SerializedName("profile_path")
            @Expose
            private String profilePath;

            public String getName() { return name; }
            public String getCharacter() { return character; }
            public String getProfilePath() { return profilePath; }
        }
    }

    @SerializedName("original_language")
    @Expose
    private String originalLanguage;

    @SerializedName("budget")
    @Expose
    private long budget;

    @SerializedName("revenue")
    @Expose
    private long revenue;

    @SerializedName("homepage")
    @Expose
    private String homepage;

    // Getters
    public String getOriginalLanguage() { return originalLanguage; }
    public long getBudget() { return budget; }
    public long getRevenue() { return revenue; }
    public String getHomepage() { return homepage; }

}
