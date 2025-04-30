package com.rkdigital.filmfolio;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.rkdigital.filmfolio.databinding.ActivityWishlistBinding;
import com.rkdigital.filmfolio.model.Movie;
import com.rkdigital.filmfolio.model.MovieDetail;
import com.rkdigital.filmfolio.model.Wishlist;
import com.rkdigital.filmfolio.model.WishlistMovie;
import com.rkdigital.filmfolio.storage.SharedPreferencesHelper;
import com.rkdigital.filmfolio.utils.LiveDataUtils;
import com.rkdigital.filmfolio.view.MovieAdapter;
import com.rkdigital.filmfolio.view.MovieWishlistAdapter;
import com.rkdigital.filmfolio.viewmodel.MovieAboutViewModel;
import com.rkdigital.filmfolio.viewmodel.ReminderViewModel;
import com.rkdigital.filmfolio.viewmodel.ReminderViewModelFactory;
import com.rkdigital.filmfolio.viewmodel.WishlistViewModel;
import com.rkdigital.filmfolio.viewmodel.WishlistViewModelFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WishlistActivity extends AppCompatActivity {
    private ArrayList<WishlistMovie>movieArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MovieWishlistAdapter movieAdapter;
    private ImageView toggle;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private ActivityWishlistBinding binding;
    private WishlistViewModel wishlistViewModel;
    private TextView userName,logoutButton;
    private ReminderViewModel reminderViewModel;
    private DrawerLayout drawerLayout;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_wishlist
        );

        WishlistViewModelFactory factory2 = new WishlistViewModelFactory(getApplication(), sharedPreferencesHelper.getString(sharedPreferencesHelper.getUserPrefs(),SharedPrefsKeys.USER_ID,"-1"));
        wishlistViewModel = new ViewModelProvider(this, factory2).get(WishlistViewModel.class);

        initUI();

        ReminderViewModelFactory factory = new ReminderViewModelFactory(getApplication(), sharedPreferencesHelper.getString(sharedPreferencesHelper.getUserPrefs(),SharedPrefsKeys.USER_ID,"-1"));
        reminderViewModel = new ViewModelProvider(this, factory).get(ReminderViewModel.class);
        userId = sharedPreferencesHelper.getString(
                sharedPreferencesHelper.getUserPrefs(),
                SharedPrefsKeys.USER_ID,
                "-1"
        );

        getMovies();

        setupClickListeners();

    }
    private void setupClickListeners(){

        toggle.setOnClickListener(view -> {
            drawerLayout.openDrawer(GravityCompat.START);

        });

        logoutButton.setOnClickListener(view -> logout());

    }
    private void initUI()
    {
        logoutButton = findViewById(R.id.logout);
        toggle=findViewById(R.id.ivHamburger);
        drawerLayout=findViewById(R.id.drawer_layout);
        userName = findViewById(R.id.userName);
        String user = sharedPreferencesHelper.getString(sharedPreferencesHelper.getUserPrefs(),SharedPrefsKeys.USER_NAME,"User Name");
        userName.setText(user);
        recyclerView = binding.recyclerview;
    }

    private void getMovies() {

        movieArrayList.clear();
        initializeAdapterIfNeeded();

        wishlistViewModel.getWishlistByUser(userId).observe(this, wishlists -> {
            if (wishlists == null || wishlists.isEmpty()) {
                movieAdapter.updateList(Collections.emptyList());
                return;
            }

            MediatorLiveData<List<WishlistMovie>> mediator = new MediatorLiveData<>();
            List<WishlistMovie> results = new ArrayList<>();

            for (Wishlist wishlist : wishlists) {
                LiveData<WishlistMovie> movieLiveData = wishlistViewModel.getMovieDetails(wishlist.getMovieId());

                mediator.addSource(movieLiveData, movie -> {
                    if (movie != null) {
                        if (!containsMovieWithId(results, movie.getId())) {
                            results.add(movie);
                        }

                        // Remove this source to prevent multiple triggers
                        mediator.removeSource(movieLiveData);

                        if (results.size() == wishlists.size()) {
                            mediator.setValue(results);
                        }
                    }
                });
            }

            mediator.observe(this, movies -> {
                movieArrayList.clear();
                if (movies != null) {
                    movieArrayList.addAll(movies);
                }

                // Adapter update logic here:
                movieAdapter.updateList(movieArrayList);
            });
        });
    }
    private void initializeAdapterIfNeeded() {
        if (movieAdapter == null) {
            movieAdapter = new MovieWishlistAdapter(this);
            recyclerView.setAdapter(movieAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
    }
    private boolean containsMovieWithId(List<WishlistMovie> list, int id) {
        for (WishlistMovie item : list) {
            if (item.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private void logout(){
        FirebaseAuth.getInstance().signOut();
        // Clear Google sign-in state
        CredentialManager credentialManager = CredentialManager.create(getApplicationContext());
        ClearCredentialStateRequest clearRequest = new ClearCredentialStateRequest(); // ✅ No need for token
        CancellationSignal cancellationSignal = new CancellationSignal();

        credentialManager.clearCredentialStateAsync(
                clearRequest,
                cancellationSignal,
                ContextCompat.getMainExecutor(this),
                new CredentialManagerCallback<Void, ClearCredentialException>() {
                    @Override
                    public void onResult(Void result) {
                        Log.d("Logout", "Credential state cleared");
                    }

                    @Override
                    public void onError(ClearCredentialException e) {
                        Log.e("Logout", "Failed to clear credential state", e);
                    }
                }
        );

        // Clear SharedPrefs
        sharedPreferencesHelper.clearAll(sharedPreferencesHelper.getDevicePrefs());
        sharedPreferencesHelper.clearAll(sharedPreferencesHelper.getUserPrefs());
        sharedPreferencesHelper.clearAll(sharedPreferencesHelper.getAppPrefs());

        // stop sync listener
        reminderViewModel.clearReminderListener();
        reminderViewModel.clearLocalReminder();
        wishlistViewModel.clearLocalWishlist();

        //  Navigate to Login
        Intent intent = new Intent(this, LoginScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}