package com.rkdigital.filmfolio.storage;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferenceObserver
{
    private static SharedPreferenceObserver instance;
    private final SharedPreferences appPrefs;
    private final SharedPreferences devicePrefs;
    private final SharedPreferences userPrefs;

    private final List<OnPreferenceChangeListener> listeners = new ArrayList<>();

    private SharedPreferenceObserver(Context context) {
        appPrefs = context.getSharedPreferences("appPrefs", Context.MODE_PRIVATE);
        devicePrefs = context.getSharedPreferences("devicePrefs", Context.MODE_PRIVATE);
        userPrefs = context.getSharedPreferences("userPrefs", Context.MODE_PRIVATE);

        // Register listeners
        appPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        devicePrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        userPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    public static SharedPreferenceObserver getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceObserver(context.getApplicationContext());
        }
        return instance;
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener =
            (sharedPreferences, key) -> {
                for (OnPreferenceChangeListener listener : listeners) {
                    listener.onPreferenceChanged(sharedPreferences, key);
                }
            };

    public interface OnPreferenceChangeListener {
        void onPreferenceChanged(SharedPreferences sharedPreferences, String key);
    }

    // Method to register observer
    public void addObserver(OnPreferenceChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    // Method to remove observer
    public void removeObserver(OnPreferenceChangeListener listener) {
        listeners.remove(listener);
    }

    // Cleanup method (call when app is closing)
    public void clear() {
        appPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        devicePrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        userPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        listeners.clear();
    }
}
