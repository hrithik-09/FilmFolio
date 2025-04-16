package com.rkdigital.filmfolio.model;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;
import com.rkdigital.filmfolio.ReminderScheduler;
import com.rkdigital.filmfolio.storage.FirebaseReminderHelper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReminderRepository {
    private final ReminderDAO reminderDao;
    ExecutorService executor;
    Handler handler;
    private ListenerRegistration firebaseListener;
    private final String currentUserId; // Store the current userId

    private final Application application;
    public ReminderRepository(Application application,String userId) {
        ReminderDatabase reminderDatabase = ReminderDatabase.getInstance(application);
        this.reminderDao = reminderDatabase.getReminderDAO();
        this.application = application;
        //  Used for background database operation
        executor= Executors.newSingleThreadExecutor();

        //  Used for updating the UI
        handler=new Handler(Looper.getMainLooper());

        this.currentUserId = userId;

        // Start real-time sync listener
        listenToFirebaseUpdates();
    }
    // In ReminderRepository.java
    private void listenToFirebaseUpdates() {
        firebaseListener = FirebaseReminderHelper.listenToRemindersForUser(currentUserId, (snapshots, error) -> {
            executor.execute(() -> {
                for (DocumentChange change : snapshots.getDocumentChanges()) {
                    Reminder cloudReminder = change.getDocument().toObject(Reminder.class);

                    switch (change.getType()) {
                        case ADDED:
                        case MODIFIED:
                            // Only schedule if it's a new reminder or modified time
                            if (reminderDao.getReminderById(cloudReminder.getReminderId()) == null ||
                                    cloudReminder.getReminderTimeMillis() != reminderDao.getReminderById(cloudReminder.getReminderId()).getReminderTimeMillis()) {
                                handler.post(() -> {
                                    Context context = application.getApplicationContext();
                                    ReminderScheduler.scheduleReminder(context, cloudReminder);
                                });
                            }
                            reminderDao.insertReminder(cloudReminder);
                            break;
                        case REMOVED:
                            handler.post(() -> {
                                Context context = application.getApplicationContext();
                                ReminderScheduler.cancelReminder(context, cloudReminder.getReminderId());
                            });
                            reminderDao.deleteReminder(cloudReminder);
                            break;
                    }
                }
            });
        });
    }
    public void clearListener() {
        if (firebaseListener != null) {
            firebaseListener.remove();
        }
    }


    public void syncReminder(Reminder reminder) {
        executor.execute(() -> {
            if(reminderDao.getReminderById(reminder.getReminderId()) != null) {
                reminderDao.updateReminder(reminder);
            } else {
                reminderDao.insertReminder(reminder);
            }
            FirebaseReminderHelper.uploadReminderToFirebase(reminder);
        });
    }
    // In ReminderRepository.java
    // In ReminderRepository
    public void deleteReminder(Reminder reminder) {
        executor.execute(() -> {
            // 1. Cancel alarms first
            handler.post(() -> {
                Context context = application.getApplicationContext();
                ReminderScheduler.cancelReminder(context, reminder.getReminderId());
            });

            // 2. Delete from local DB
            reminderDao.deleteReminder(reminder);

            // 3. Delete from Firebase (which will trigger the listener)
            FirebaseReminderHelper.deleteReminderFromFirebase(
                    reminder.getUserId(),
                    reminder.getReminderId()
            );
        });
    }

    public LiveData<List<Reminder>> getRemindersByUser(String userId) {
        return reminderDao.getRemindersByUser(userId);
    }

    public LiveData<Reminder> getReminderForMovie(int movieId, String userId) {
        return reminderDao.getReminderForMovie(movieId,userId);
    }


}
