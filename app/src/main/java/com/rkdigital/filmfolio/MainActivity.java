package com.rkdigital.filmfolio;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.rkdigital.filmfolio.databinding.ActivityMainBinding;
import com.rkdigital.filmfolio.model.Movie;
import com.rkdigital.filmfolio.view.MovieAdapter;
import com.rkdigital.filmfolio.viewmodel.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Movie> movies;
    private Switch darkModeSwitch;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private EditText etSearch;
    private ImageView ivSearch,toggle,filterIcon;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;


    private boolean[] selectedFilters;
    private ArrayList<String> chosenFilters = new ArrayList<>();
    private String[] filterOptions;
    private DrawerLayout drawerLayout;

    private void updateSwitchText(boolean isDarkMode) {
        darkModeSwitch.setText(isDarkMode ? "Light Mode" : "Dark Mode");
    }


    private boolean isSystemDarkModeEnabled() {
        int currentNightMode = getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        boolean isDarkMode = sharedPreferencesHelper.getBoolean(sharedPreferencesHelper.getAppPrefs(),SharedPrefsKeys.THEME,isSystemDarkModeEnabled());

        int currentMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (isDarkMode && currentMode != Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (!isDarkMode && currentMode != Configuration.UI_MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_main
        );

        viewModel = new ViewModelProvider(this)
                .get(MainActivityViewModel.class);

        getMovies();

        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        darkModeSwitch.setChecked(isDarkMode);
        updateSwitchText(isDarkMode);
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Save preference
            sharedPreferencesHelper.putBoolean(sharedPreferencesHelper.getAppPrefs(),SharedPrefsKeys.THEME,isChecked);

            // Switch theme only if there's a change
            drawerLayout.closeDrawer(GravityCompat.START);

            // Delay theme change slightly to allow drawer to close
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                AppCompatDelegate.setDefaultNightMode(
                        isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
                );
            }, 250); // adjust delay if needed
        });

        swipeRefreshLayout = binding.swipeLayout;
        swipeRefreshLayout.setColorSchemeResources(R.color.black);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMovies();
            }
        });

        etSearch = findViewById(R.id.etSearch);
        ivSearch = findViewById(R.id.ivSearch);
        filterIcon =  findViewById(R.id.ivFilter);
        filterOptions = getResources().getStringArray(R.array.filter_options);
        selectedFilters = new boolean[filterOptions.length];

        filterIcon.setOnClickListener(v -> {
            if (getSupportFragmentManager().findFragmentByTag("FilterBottomSheet") != null) {
                return;
            }
            MovieFilterBottomSheet filterDialog = MovieFilterBottomSheet.newInstance(() -> {
                viewModel.refreshMovies(); // <— This triggers filtered movie refresh
            });
            filterDialog.show(getSupportFragmentManager(), "FilterBottomSheet");
        });



        ivSearch.setOnClickListener(v -> {
            if (etSearch.getVisibility() == View.GONE) {
                etSearch.setVisibility(View.VISIBLE);
                etSearch.requestFocus();


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
                }
            } else {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                }

                etSearch.setVisibility(View.GONE);
            }
        });

        setupLiveSearch();
        toggle=findViewById(R.id.ivHamburger);
        drawerLayout=findViewById(R.id.drawer_layout);
        toggle.setOnClickListener(view -> {
            drawerLayout.openDrawer(GravityCompat.START);

        });

    }
    private void setupLiveSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();

                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> {
                    if (!query.isEmpty()) {
                        viewModel.searchMovies(query);
                    } else {
                        viewModel.clearSearch();
                    }
                    recyclerView.scrollToPosition(0); // reset scroll
                };
                searchHandler.postDelayed(searchRunnable, 500); // debounce 500ms
            }
        });
    }
    private void getMovies() {
        if (movies != null && !movies.isEmpty()) {
            displayMoviesInRecyclerView(); // use cached list
            return;
        }
        viewModel.getAllMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> moviesFromLiveData) {
                movies = (ArrayList<Movie>) moviesFromLiveData;
                displayMoviesInRecyclerView();
                recyclerView.scrollToPosition(0);
            }
        });

    }


    private void displayMoviesInRecyclerView() {
        recyclerView = binding.recyclerview;

        // Initialize adapter only once
        if (movieAdapter == null) {
            movieAdapter = new MovieAdapter(this, movies);
            recyclerView.setAdapter(movieAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == movies.size() - 1) {
                        viewModel.loadMoreMovies();
                    }
                }
            });
        } else {
            movieAdapter.notifyDataSetChanged();
        }
    }
}