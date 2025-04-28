package com.rkdigital.filmfolio.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MainActivityViewModelFactory implements ViewModelProvider.Factory{
    private final Application application;
    private final String userId;

    public MainActivityViewModelFactory(Application application, String userId) {
        this.application = application;
        this.userId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(application, userId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
