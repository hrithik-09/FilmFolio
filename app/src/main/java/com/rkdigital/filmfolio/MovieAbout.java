package com.rkdigital.filmfolio;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.rkdigital.filmfolio.model.MovieDetail;
import com.rkdigital.filmfolio.view.CastAdapter;
import com.rkdigital.filmfolio.viewmodel.MovieAboutViewModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MovieAbout extends AppCompatActivity {

    private MovieAboutViewModel viewModel;

    private ImageView ivPoster;
    private TextView tvTitle, tvTagline, tvMeta, tvOverview, tvLanguage, tvBudget, tvRevenue, tvStatus, tvHomepage,tvRating,tvRuntime,tvReleaseDate;
    private ChipGroup chipGenres;
    private LinearLayout layoutProductionCompanies;
    private RecyclerView rvCast;
    private MaterialButton btnWishlist, btnSetReminder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_about);

        int movieId = getIntent().getIntExtra("movie_id", -1);
        if (movieId == -1) {
            Toast.makeText(this, "Movie ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(MovieAboutViewModel.class);

        viewModel.getMovieDetails(movieId).observe(this, this::updateUI);

        setupListeners();
    }

    private void initViews() {
        ivPoster = findViewById(R.id.ivPoster);
        tvTitle = findViewById(R.id.tvTitle);
        tvTagline = findViewById(R.id.tvTagline);
        tvRating=findViewById(R.id.tvRating);
        tvRuntime=findViewById(R.id.tvRuntime);
        tvReleaseDate=findViewById(R.id.tvReleaseDate);
//        tvMeta = findViewById(R.id.tvMeta);
        tvOverview = findViewById(R.id.tvOverview);
        tvLanguage = findViewById(R.id.tvLanguage);
        tvBudget = findViewById(R.id.tvBudget);
        tvRevenue = findViewById(R.id.tvRevenue);
        tvStatus = findViewById(R.id.tvStatus);
        tvHomepage = findViewById(R.id.tvHomepage);
        chipGenres = findViewById(R.id.chipGenres);
        layoutProductionCompanies = findViewById(R.id.layoutProductionCompanies);
        rvCast = findViewById(R.id.rvCast);
        btnWishlist = findViewById(R.id.btnWishlist);
        btnSetReminder = findViewById(R.id.btnSetReminder);
    }

    private void setupListeners() {
        btnWishlist.setOnClickListener(v ->
                Toast.makeText(this, "Added to Wishlist (functionality coming soon)", Toast.LENGTH_SHORT).show());

        btnSetReminder.setOnClickListener(v ->
                Toast.makeText(this, "Reminder Set (functionality coming soon)", Toast.LENGTH_SHORT).show());
    }

    private String formatCurrency(long amount) {
        return amount > 0 ? NumberFormat.getCurrencyInstance(Locale.US).format(amount) : "N/A";
    }

    private void updateUI(MovieDetail detail) {
        if (detail == null) {
            Toast.makeText(this, "Failed to load details", Toast.LENGTH_SHORT).show();
            return;
        }

        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500" + detail.getPosterPath())
                .into(ivPoster);

        tvTitle.setText(detail.getTitle());
        tvTagline.setText(detail.getTagline());
        tvRuntime.setText(String.format("Runtime: %d min", detail.getRuntime()));
        tvRating.setText(String.format("Rating: %.1f",detail.getVoteAverage()));
        tvReleaseDate.setText(String.format("Release: %s", detail.getReleaseDate()));
        tvOverview.setText(detail.getOverview());
        tvLanguage.setText("Language: " + detail.getOriginalLanguage().toUpperCase(Locale.ROOT));
        tvBudget.setText("Budget: " + formatCurrency(detail.getBudget()));
        tvRevenue.setText("Revenue: " + formatCurrency(detail.getRevenue()));
        tvStatus.setText("Status: " + detail.getStatus());

        if (detail.getHomepage() != null && !detail.getHomepage().isEmpty()) {
            tvHomepage.setText(detail.getHomepage());
            tvHomepage.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(detail.getHomepage()));
                startActivity(browserIntent);
            });
        } else {
            tvHomepage.setVisibility(View.GONE);
        }

        // Genres
        chipGenres.removeAllViews();
        List<MovieDetail.Genre> genres = detail.getGenres();
        if (genres != null) {
            for (MovieDetail.Genre genre : genres) {
                Chip chip = new Chip(this);
                chip.setText(genre.getName());
                chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#2A0F0F")));
                chip.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#FF4D4D")));
                chip.setChipStrokeWidth(1.5f);
                chip.setTextColor(Color.parseColor("#E6E6E6"));
                chip.setClickable(false);
                chip.setCheckable(false);
                chipGenres.addView(chip);
            }
        }

        // Production Companies
        layoutProductionCompanies.removeAllViews();
        List<MovieDetail.ProductionCompany> companies = detail.getProductionCompanies();
        if (companies != null) {
            for (MovieDetail.ProductionCompany company : companies) {
                if (company.getLogoPath() != null) {
                    ImageView logo = new ImageView(this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
                    params.setMargins(8, 0, 8, 0);
                    logo.setLayoutParams(params);
                    Glide.with(this)
                            .load("https://image.tmdb.org/t/p/w500" + company.getLogoPath())
                            .into(logo);
                    layoutProductionCompanies.addView(logo);
                }
            }
        }

        // Cast
        List<MovieDetail.Credits.Cast> castList = detail.getCredits().getCast();
        if (castList != null && !castList.isEmpty()) {
            CastAdapter adapter = new CastAdapter(this,castList);
            rvCast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvCast.setAdapter(adapter);
        }
    }
}

