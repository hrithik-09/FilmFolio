package com.rkdigital.filmfolio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovieFilterBottomSheet extends BottomSheetDialogFragment {
    private ChipGroup chipGroupGenre, chipGroupSort;
    private SeekBar seekBarRating;
    private TextView textViewRatingValue;
    private TextView btnApply, btnReset,textViewYearRange;
    private RangeSlider rangeSlider;

    private SharedPreferencesHelper sharedPreferencesHelper;

    public static MovieFilterBottomSheet newInstance() {
        return new MovieFilterBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_filter, container, false);

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext());

        chipGroupGenre = view.findViewById(R.id.chipGroupGenre);
        chipGroupSort = view.findViewById(R.id.chipGroupSort);
        seekBarRating = view.findViewById(R.id.seekBarRating);
        textViewRatingValue = view.findViewById(R.id.textViewRatingValue);
        btnApply = view.findViewById(R.id.btnApply);
        btnReset = view.findViewById(R.id.btnReset);
        rangeSlider = view.findViewById(R.id.rangeSliderReleaseYear);
        textViewYearRange = view.findViewById(R.id.textViewYearRange);

        rangeSlider.setValues(2000f, 2020f);


        rangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            textViewYearRange.setText(String.format("%.0f - %.0f", values.get(0), values.get(1)));
        });

        setupGenreChips();
        setupSortChips();
        setupSeekBar();
        loadSavedFilters();

        btnApply.setOnClickListener(v -> saveFilters());
        btnReset.setOnClickListener(v -> resetFilters());

        return view;
    }

    private void setupGenreChips() {
        String[] genres = {"Action", "Comedy", "Drama", "Horror", "Romance", "Sci-Fi"};
        for (String genre : genres) {
            Chip chip = new Chip(requireContext());
            chip.setText(genre);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.color.primaryBackground);
            chip.setTextColor(getResources().getColor(R.color.black));
            chipGroupGenre.addView(chip);
        }
    }

    private void setupSortChips() {
        String[] sortOptions = {"Popularity", "Release Date", "Rating"};
        for (String option : sortOptions) {
            Chip chip = new Chip(requireContext());
            chip.setText(option);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.color.primaryBackground);
            chip.setTextColor(getResources().getColor(R.color.black));
            chipGroupSort.addView(chip);
        }
    }

    private void setupSeekBar() {
        seekBarRating.setMax(10);
        seekBarRating.setProgress(5);
        textViewRatingValue.setText("5.0");

        seekBarRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewRatingValue.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void saveFilters() {
        Set<String> selectedGenres = new HashSet<>();
        for (int i = 0; i < chipGroupGenre.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupGenre.getChildAt(i);
            if (chip.isChecked()) {
                selectedGenres.add(chip.getText().toString());
            }
        }

        Set<String> selectedSort = new HashSet<>();
        for (int i = 0; i < chipGroupSort.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupSort.getChildAt(i);
            if (chip.isChecked()) {
                selectedSort.add(chip.getText().toString());
            }
        }

//        String releaseYear = editTextReleaseYear.getText().toString();
        int rating = seekBarRating.getProgress();

//        sharedPreferencesHelper.putString(sharedPreferencesHelper.getAppPrefs(),SharedPrefsKeys.FILTER_GENRES, selectedGenres);
//        sharedPreferencesHelper.putStringSet(SharedPrefsKeys.FILTER_SORT, selectedSort);
//        sharedPreferencesHelper.putString(SharedPrefsKeys.FILTER_YEAR, releaseYear);
//        sharedPreferencesHelper.putInt(SharedPrefsKeys.FILTER_RATING, rating);

        dismiss();
    }

    private void resetFilters() {
        chipGroupGenre.clearCheck();
        chipGroupSort.clearCheck();
        rangeSlider.resetPivot();
        seekBarRating.setProgress(5);
        textViewRatingValue.setText("5.0");
    }

    private void loadSavedFilters() {
//        Set<String> savedGenres = sharedPreferencesHelper.getStringSet(SharedPrefsKeys.FILTER_GENRES, new HashSet<>());
//        Set<String> savedSort = sharedPreferencesHelper.getStringSet(SharedPrefsKeys.FILTER_SORT, new HashSet<>());
//        String savedYear = sharedPreferencesHelper.getString(SharedPrefsKeys.FILTER_YEAR, "");
//        int savedRating = sharedPreferencesHelper.getInt(SharedPrefsKeys.FILTER_RATING, 5);

//        for (int i = 0; i < chipGroupGenre.getChildCount(); i++) {
//            Chip chip = (Chip) chipGroupGenre.getChildAt(i);
//            if (savedGenres.contains(chip.getText().toString())) {
//                chip.setChecked(true);
//            }
//        }
//
//        for (int i = 0; i < chipGroupSort.getChildCount(); i++) {
//            Chip chip = (Chip) chipGroupSort.getChildAt(i);
//            if (savedSort.contains(chip.getText().toString())) {
//                chip.setChecked(true);
//            }
//        }
//
//        editTextReleaseYear.setText(savedYear);
//        seekBarRating.setProgress(savedRating);
//        textViewRatingValue.setText(String.valueOf(savedRating));
    }
}
