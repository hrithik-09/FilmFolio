package com.rkdigital.filmfolio.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.rkdigital.filmfolio.model.Reminder;
import com.rkdigital.filmfolio.model.ReminderRepository;

import java.util.List;

public class MyViewModel extends AndroidViewModel {
    private ReminderRepository reminderRepository;

    public MyViewModel(@NonNull Application application, String userId) {
        super(application);
        this.reminderRepository = new ReminderRepository(application, userId);
    }


    public LiveData<List<Reminder>>getReminderByUser(String userId)
    {
        return reminderRepository.getRemindersByUser(userId);
    }
    public void addNewReminder(Reminder reminder)
    {
        reminderRepository.syncReminder(reminder);
    }
    public void updateReminder(Reminder reminder){
        reminderRepository.syncReminder(reminder);
    }

    public void deleteReminder(Reminder reminder)
    {
        reminderRepository.deleteReminder(reminder);
    }
    public LiveData<Reminder>getReminderByMovie(int movieId,String userid){
        return reminderRepository.getReminderForMovie(movieId,userid);
    }
}
