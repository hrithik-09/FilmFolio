package com.rkdigital.filmfolio.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ReminderViewModelFactory implements ViewModelProvider.Factory{
    private final Application application;
    private final String userId;
    public ReminderViewModelFactory(Application application, String userId) {
        this.application = application;
        this.userId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ReminderViewModel.class)) {
            return (T) new ReminderViewModel(application, userId);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
