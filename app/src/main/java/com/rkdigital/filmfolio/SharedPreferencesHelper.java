package com.rkdigital.filmfolio;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import android.util.Pair;

public class SharedPreferencesHelper {
    private static SharedPreferencesHelper instance;
    private SharedPreferences appPrefs;
    private SharedPreferences devicePrefs;
    private SharedPreferences userPrefs;
    private static final String APP_PREFS_NAME="appPrefs";
    private static final String USER_PREFS_NAME="userPrefs";
    private static final String DEVICE_PREFS_NAME="devicePrefs";

    private SharedPreferencesHelper(Context context){
        appPrefs=context.getSharedPreferences(APP_PREFS_NAME,Context.MODE_PRIVATE);
        devicePrefs=context.getSharedPreferences(DEVICE_PREFS_NAME,Context.MODE_PRIVATE);
        userPrefs=context.getSharedPreferences(USER_PREFS_NAME,Context.MODE_PRIVATE);

    }

    public static SharedPreferencesHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesHelper(context.getApplicationContext());
        }
        return instance;
    }

    public SharedPreferences getAppPrefs() {
        return appPrefs;
    }

    public SharedPreferences getDevicePrefs() {
        return devicePrefs;
    }

    public SharedPreferences getUserPrefs() {
        return userPrefs;
    }

    public boolean getBoolean(SharedPreferences prefs, String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    public String getString(SharedPreferences prefs, String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    public int getInt(SharedPreferences prefs, String key, int defaultValue) {
        return prefs.getInt(key, defaultValue);


    }

    public void putString(SharedPreferences prefs, String key, String value) {
        prefs.edit().putString(key, value).apply();

    }
    public void putBoolean(SharedPreferences prefs, String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public void putInt(SharedPreferences prefs, String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }

    public void clearKey(SharedPreferences prefs, String key) {
        prefs.edit().remove(key).apply();
    }

    public void clearAll(SharedPreferences prefs) {
        prefs.edit().clear().apply();
    }

    public void saveGenres(List<Integer> genres) {
        Set<String> genreSet = new HashSet<>();
        for (Integer genre : genres) {
            genreSet.add(String.valueOf(genre));
        }
        userPrefs.edit().putStringSet(SharedPrefsKeys.KEY_GENRES, genreSet).apply();
    }

    public List<Integer> getGenres() {
        Set<String> genreSet = userPrefs.getStringSet(SharedPrefsKeys.KEY_GENRES, new HashSet<>());
        List<Integer> genres = new ArrayList<>();
        for (String genre : genreSet) {
            genres.add(Integer.parseInt(genre));
        }

        return genres;
    }

    public String getSortOption() {
        return userPrefs.getString(SharedPrefsKeys.KEY_SORT, "");
    }
    public void saveSortOption(String sortOption) {
        userPrefs.edit().putString(SharedPrefsKeys.KEY_SORT, sortOption).apply();
    }


    public void saveYearRange(int minYear, int maxYear) {
        userPrefs.edit().putInt(SharedPrefsKeys.KEY_MIN_YEAR, minYear).putInt(SharedPrefsKeys.KEY_MAX_YEAR, maxYear).apply();
    }

    public Pair<Integer, Integer> getYearRange() {
        int minYear = userPrefs.getInt(SharedPrefsKeys.KEY_MIN_YEAR, 1900);
        int maxYear = userPrefs.getInt(SharedPrefsKeys.KEY_MAX_YEAR, 2025);
        return new Pair<>(minYear, maxYear);
    }

    public void saveMinRating(int rating) {
        userPrefs.edit().putInt(SharedPrefsKeys.KEY_MIN_RATING, rating).apply();
    }

    public int getMinRating() {
        return userPrefs.getInt(SharedPrefsKeys.KEY_MIN_RATING, 5);
    }

    public void clearFilters() {
        userPrefs.edit().remove(SharedPrefsKeys.KEY_GENRES)
                .remove(SharedPrefsKeys.KEY_SORT)
                .remove(SharedPrefsKeys.KEY_MIN_YEAR)
                .remove(SharedPrefsKeys.KEY_MAX_YEAR)
                .remove(SharedPrefsKeys.KEY_MIN_RATING)
                .apply();
    }
}
