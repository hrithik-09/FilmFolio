package com.rkdigital.filmfolio.model;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReminderRepository {
    private final ReminderDAO reminderDao;
    ExecutorService executor;
    Handler handler;

    public ReminderRepository(Application application) {
        ReminderDatabase reminderDatabase = ReminderDatabase.getInstance(application);
        this.reminderDao = reminderDatabase.getReminderDAO();
        //  Used for background database operation
        executor= Executors.newSingleThreadExecutor();

        //  Used for updating the UI
        handler=new Handler(Looper.getMainLooper());
    }

    public void insertReminder(Reminder reminder) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                reminderDao.insertReminder(reminder);
            }
        });
    }

    public void updateReminder(Reminder reminder) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                reminderDao.updateReminder(reminder);
            }
        });
    }

    public void deleteReminder(Reminder reminder) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                reminderDao.deleteReminder(reminder);
            }
        });
    }

    public LiveData<List<Reminder>> getRemindersByUser(String userId) {
        return reminderDao.getRemindersByUser(userId);
    }

    public LiveData<Reminder> getReminderForMovie(int movieId, String userId) {
        return reminderDao.getReminderForMovie(movieId,userId);
    }
}
