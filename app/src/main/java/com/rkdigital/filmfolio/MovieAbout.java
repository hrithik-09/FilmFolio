package com.rkdigital.filmfolio;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.rkdigital.filmfolio.model.MovieDetail;
import com.rkdigital.filmfolio.model.Reminder;
import com.rkdigital.filmfolio.model.Wishlist;
import com.rkdigital.filmfolio.reminder.ReminderScheduler;
import com.rkdigital.filmfolio.storage.SharedPreferencesHelper;
import com.rkdigital.filmfolio.view.CastAdapter;
import com.rkdigital.filmfolio.viewmodel.MovieAboutViewModel;
import com.rkdigital.filmfolio.viewmodel.ReminderViewModel;
import com.rkdigital.filmfolio.viewmodel.ReminderViewModelFactory;
import com.rkdigital.filmfolio.viewmodel.WishlistViewModel;
import com.rkdigital.filmfolio.viewmodel.WishlistViewModelFactory;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import android.Manifest;

public class MovieAbout extends AppCompatActivity {
    private MovieAboutViewModel viewModel;
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1001;
    private ImageView ivPoster,goBack;
    private TextView tvTitle, tvTagline, tvOverview, tvLanguage, tvBudget, tvRevenue, tvStatus, tvHomepage,tvRating,tvRuntime,tvReleaseDate;
    private ChipGroup chipGenres;
    private LinearLayout layoutProductionCompanies;
    private RecyclerView rvCast;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private MaterialButton btnWishlist, btnSetReminder;
    private ReminderViewModel reminderViewModel;
    private WishlistViewModel wishlistViewModel;
    private String userId;
    private int movieId;
    private Wishlist currentwishlist;
    private Reminder currentReminder;
    private CountDownTimer countDownTimer;
    private String movieTitle;
    ExecutorService executor;
    Handler handler;
    private AnimatorSet animatorSet;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showDateTimePicker(); // Retry now that we have permission
            } else {
                Toast.makeText(this, "Notification permission is required for reminders", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_about);
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);

        //  Used for background database operation
        executor= Executors.newSingleThreadExecutor();

        //  Used for updating the UI
        handler=new Handler(Looper.getMainLooper());

        movieId = getIntent().getIntExtra("movie_id", -1);
        if (movieId == -1) {
            Toast.makeText(this, "Movie ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(MovieAboutViewModel.class);

        setupListeners();
        viewModel.getMovieDetails(movieId).observe(this, movie -> {
            movieTitle = movie.getTitle()!=null?movie.getTitle():"Movie Title"; // store title for reminder
            updateUI(movie);              // update UI
        });
        userId = sharedPreferencesHelper.getString(sharedPreferencesHelper.getUserPrefs(),SharedPrefsKeys.USER_ID,"-1");
        ReminderViewModelFactory factory = new ReminderViewModelFactory(getApplication(), userId);
        reminderViewModel = new ViewModelProvider(this, factory).get(ReminderViewModel.class);

        reminderViewModel.getReminderByMovie(movieId, userId).observe(this, reminder -> {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            currentReminder = reminder;
            updateReminderButtonUI();
        });


        WishlistViewModelFactory wishlistViewModelFactory = new WishlistViewModelFactory(getApplication(),userId);
        wishlistViewModel = new ViewModelProvider(this,wishlistViewModelFactory).get(WishlistViewModel.class);

        wishlistViewModel.getWishlistByMovie(movieId,userId).observe(this,wishlist -> {
            currentwishlist = wishlist;
            updateWishlistButtonUI();
            btnWishlist.setEnabled(true);
            btnWishlist.setIconResource(wishlist != null ?
                    R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
        });


    }
    private void initViews() {
        goBack=findViewById(R.id.goBack);
        ivPoster = findViewById(R.id.ivPoster);
        tvTitle = findViewById(R.id.tvTitle);
        tvTagline = findViewById(R.id.tvTagline);
        tvRating=findViewById(R.id.tvRating);
        tvRuntime=findViewById(R.id.tvRuntime);
        tvReleaseDate=findViewById(R.id.tvReleaseDate);
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
    private void updateWishlistButtonUI() {
        if (currentwishlist != null) {
            // Already in wishlist - show filled heart with animation
            animateWishlistButton(true);
        } else {
            // Not in wishlist - show outline heart
            animateWishlistButton(false);
        }
    }

    private void animateWishlistButton(boolean isLiked) {
        btnWishlist.clearAnimation();

        // Bounce animation
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(btnWishlist, "scaleX", 0.9f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(btnWishlist, "scaleY", 0.9f, 1.1f, 1f);

        if (animatorSet == null) {
            animatorSet = new AnimatorSet();
        }
        else
            animatorSet.cancel();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(200);
        animatorSet.setInterpolator(new OvershootInterpolator());

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                btnWishlist.setIconResource(isLiked ?
                        R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
                btnWishlist.setText(isLiked ? "Liked" : "Like");
            }
        });

        animatorSet.start();

        // Haptic feedback
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnWishlist.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
        }
    }

    private void createOrDeleteWishlist() {
        btnWishlist.setEnabled(false);

        if (currentwishlist != null) {
            // Delete existing wishlist
            wishlistViewModel.deleteWishlist(currentwishlist);
        } else {
            // Create new wishlist
            Wishlist newWishlist = new Wishlist();
            newWishlist.setWishlistId(UUID.randomUUID().toString());
            newWishlist.setUserId(userId);
            newWishlist.setMovieId(movieId);
            wishlistViewModel.addNewWishlist(newWishlist);
        }
    }
    private void updateReminderButtonUI() {
        if (currentReminder != null) {
            long millisUntilReminder = currentReminder.getReminderTimeMillis() - System.currentTimeMillis();
            if (millisUntilReminder > 0) {
                // Cancel any existing timer before starting new one
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                btnSetReminder.setText(formatMillis(millisUntilReminder));
                startCountdown(millisUntilReminder);
            } else {
                btnSetReminder.setText("Reminder Expired");
                // Auto-delete expired reminders
                executor.execute(() -> {
                    reminderViewModel.deleteReminder(currentReminder);
                });
            }
        } else {
            // Ensure complete cleanup when reminder is deleted
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            btnSetReminder.setText("Set Reminder");
        }
    }
    private String formatMillis(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis)%60;
        return String.format(Locale.getDefault(), "%02dh %02dm %02ds", hours, minutes,seconds);
    }

    private void startCountdown(long millis) {
        // Always cancel previous timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(millis, 1000) {
            public void onTick(long millisUntilFinished) {
                // Only update if we still have a valid reminder
                if (currentReminder != null) {
                    btnSetReminder.setText(formatMillis(millisUntilFinished));
                }
            }

            public void onFinish() {
                // Only update if we still have a valid reminder
                if (currentReminder != null) {
                    btnSetReminder.setText("Reminder Expired");
                }
            }
        }.start();
    }


    private void setupListeners() {
        btnWishlist.setOnClickListener(v -> {
            animateWishlistButton(currentwishlist == null);
            createOrDeleteWishlist();
        });
        goBack.setOnClickListener(v ->
        {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        btnSetReminder.setOnClickListener(v -> {
            if (currentReminder != null) {
                showReminderOptionsDialog();
            } else {
                showDateTimePicker();
            }
        });
    }

    private void showReminderOptionsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Reminder")
                .setItems(new String[]{"Edit", "Delete"}, (dialog, which) -> {
                    if (which == 0) {
                        showDateTimePicker(); // Edit
                    } else {
                        btnSetReminder.setText("Deleting...");
                        btnSetReminder.setEnabled(false);

                        // Observe the LiveData to know when deletion is complete
                        reminderViewModel.getReminderByMovie(movieId, userId).observe(this, reminder -> {
                            if (reminder == null) {
                                // Deletion complete
                                currentReminder = null;
                                btnSetReminder.setText("Set Reminder");
                                btnSetReminder.setEnabled(true);
                                if (countDownTimer != null) countDownTimer.cancel();

                                // Remove this temporary observer
                                reminderViewModel.getReminderByMovie(movieId, userId).removeObservers(this);
                            }
                        });

                        reminderViewModel.deleteReminder(currentReminder);

                    }
                }).show();
    }
    private void showDateTimePicker() {
            // Check for notification permission (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                            REQUEST_CODE_POST_NOTIFICATIONS);
                    return;
                }
            }
            // Check for exact alarm permission (Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                if (!alarmManager.canScheduleExactAlarms()) {
                    startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
                    return;
                }
            }
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (timeView, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, 0);

                                long selectedTime = calendar.getTimeInMillis();

                                createOrUpdateReminder(selectedTime);
                            },
                            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),false);

                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
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

    private void createOrUpdateReminder(long selectedTime) {
        if (selectedTime < System.currentTimeMillis()) {
            Toast.makeText(this, "Please select a future time", Toast.LENGTH_SHORT).show();
            return;
        }

        Reminder newReminder = new Reminder();
        if (currentReminder != null) {
            newReminder.setReminderId(currentReminder.getReminderId());
        }
        else
        {
            newReminder.setReminderId(UUID.randomUUID().toString());
        }
        newReminder.setMovieId(movieId);
        newReminder.setMovieTitle(movieTitle);
        newReminder.setReminderTimeMillis(selectedTime);
        newReminder.setUserId(userId);

        if (currentReminder != null) {
            reminderViewModel.updateReminder(newReminder);
        } else {
            reminderViewModel.addNewReminder(newReminder);
        }

        // Schedule the reminders
        ReminderScheduler.scheduleReminder(this, newReminder);

        // Show confirmation
        String timeString = DateFormat.getDateTimeInstance().format(new Date(selectedTime));
        Toast.makeText(this, "Reminder set for " + timeString, Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

