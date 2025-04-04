package com.rkdigital.filmfolio;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Pair;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MovieFilterBottomSheet extends BottomSheetDialogFragment {
    private ChipGroup chipGroupGenre, chipGroupSort;
    private RangeSlider rangeSliderReleaseYear;
    private TextView textViewMinYear, textViewMaxYear;
    private SeekBar seekBarRating;
    private TextView textViewRatingValue;
    private Runnable onApplyCallback;
    private Button btnApply, btnReset;

    private SharedPreferencesHelper sharedPreferencesHelper;

    public static MovieFilterBottomSheet newInstance(Runnable onApplyCallback) {
        MovieFilterBottomSheet sheet = new MovieFilterBottomSheet();
        sheet.onApplyCallback = onApplyCallback;
        return sheet;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_filter, container, false);

        // Initialize UI elements
        chipGroupGenre = view.findViewById(R.id.chipGroupGenre);
        chipGroupSort = view.findViewById(R.id.chipGroupSort);
        rangeSliderReleaseYear = view.findViewById(R.id.rangeSliderReleaseYear);
        textViewMinYear = view.findViewById(R.id.textViewMinYear);
        textViewMaxYear = view.findViewById(R.id.textViewMaxYear);
        seekBarRating = view.findViewById(R.id.seekBarRating);
        textViewRatingValue = view.findViewById(R.id.textViewRatingValue);
        btnApply = view.findViewById(R.id.btnApply);
        btnReset = view.findViewById(R.id.btnReset);

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext());

        String[] genres = {"Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Thriller", "Romance"};
        populateChipGroup(chipGroupGenre, genres, false); // Multi-select

        // Populate Sort By options
        String[] sortByOptions = {"Popularity", "Release Date", "Rating"};
        populateChipGroup(chipGroupSort, sortByOptions, true);

        loadSavedPreferences();

        setupListeners();

        return view;
    }

    private void populateChipGroup(ChipGroup chipGroup, String[] options, boolean singleSelection) {
        chipGroup.setSingleSelection(singleSelection);
        chipGroup.removeAllViews(); // Clear old data

        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            Chip chip = new Chip(requireContext());
            chip.setText(option);
            chip.setCheckable(true);
            if (!singleSelection) {
                chip.setTag(i);
            }

            chip.setChecked(false);
            updateChipColor(chip, false);

            chipGroup.addView(chip);
        }
    }
    private void setupChipGroupListeners() {
        for (int i = 0; i < chipGroupGenre.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupGenre.getChildAt(i);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateChipColor(chip, isChecked);
            });

        }

        for (int i = 0; i < chipGroupSort.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupSort.getChildAt(i);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateChipColor(chip, isChecked);
            });

        }
    }
    private void updateChipColor(Chip chip, boolean isChecked) {
        if (isChecked) {
            chip.setChipBackgroundColorResource(R.color.chip_selected);
            chip.setTextColor(getResources().getColor(R.color.chip_text_selected));
        } else {
            chip.setChipBackgroundColorResource(R.color.chip_unselected);
            chip.setTextColor(getResources().getColor(R.color.chip_text_unselected));
        }
    }

    private void setupListeners() {
        // RangeSlider for Release Year
        rangeSliderReleaseYear.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            int minYear = values.get(0).intValue();
            int maxYear = values.get(1).intValue();
            textViewMinYear.setText(String.valueOf(minYear));
            textViewMaxYear.setText(String.valueOf(maxYear));
        });

        // SeekBar for Rating
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
        setupChipGroupListeners();

        // Apply Button
        btnApply.setOnClickListener(v -> {
            savePreferences();
            if (onApplyCallback != null) onApplyCallback.run();
            dismiss();
        });

        // Reset Button
        btnReset.setOnClickListener(v -> resetFilters());
    }

    private void savePreferences() {
        List<Integer> selectedGenres = new ArrayList<>();
        for (int i = 0; i < chipGroupGenre.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupGenre.getChildAt(i);
            if (chip.isChecked()) {
                selectedGenres.add((int) chip.getTag());
            }
        }

        int selectedSortId = chipGroupSort.getCheckedChipId();
        String selectedSort = selectedSortId != View.NO_ID ? ((Chip) chipGroupSort.findViewById(selectedSortId)).getText().toString() : "";

        int minYear = rangeSliderReleaseYear.getValues().get(0).intValue();
        int maxYear = rangeSliderReleaseYear.getValues().get(1).intValue();
        int minRating = seekBarRating.getProgress();

        sharedPreferencesHelper.saveGenres(selectedGenres);
        sharedPreferencesHelper.saveSortOption(selectedSort);
        sharedPreferencesHelper.saveYearRange(minYear, maxYear);
        sharedPreferencesHelper.saveMinRating(minRating);

        dismiss();
    }

    private void resetFilters() {
        loadSavedPreferences();
    }

    private void loadSavedPreferences() {
        List<Integer> savedGenres = sharedPreferencesHelper.getGenres();
        if (savedGenres == null) savedGenres = new ArrayList<>();

        for (int i = 0; i < chipGroupGenre.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupGenre.getChildAt(i);
            if (chip.getTag() != null) {
                boolean isSelected = savedGenres.contains((int) chip.getTag());
                chip.setChecked(isSelected);
                updateChipColor(chip, isSelected);
                chip.jumpDrawablesToCurrentState();
            }
        }

        String savedSort = sharedPreferencesHelper.getSortOption();
        if (savedSort != null) {
            for (int i = 0; i < chipGroupSort.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupSort.getChildAt(i);
                boolean isSelected = chip.getText() != null && chip.getText().toString().equals(savedSort);
                chip.setChecked(isSelected);
                updateChipColor(chip, isSelected);
                chip.jumpDrawablesToCurrentState();
            }
        }

        // Load Year Range
        Pair<Integer, Integer> yearRange = sharedPreferencesHelper.getYearRange();
        rangeSliderReleaseYear.setValues((float) yearRange.first, (float) yearRange.second);
        textViewMinYear.setText(String.valueOf(yearRange.first));
        textViewMaxYear.setText(String.valueOf(yearRange.second));

        // Load Min Rating
        int savedMinRating = sharedPreferencesHelper.getMinRating();
        seekBarRating.setProgress(savedMinRating);
        textViewRatingValue.setText(String.valueOf(savedMinRating));
    }
}
