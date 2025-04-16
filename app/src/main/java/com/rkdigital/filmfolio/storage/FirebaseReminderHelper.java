package com.rkdigital.filmfolio.storage;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.rkdigital.filmfolio.model.Reminder;

import java.util.ArrayList;
import java.util.List;

public class FirebaseReminderHelper {

    private static final String ROOT_COLLECTION = "users";
    private static final String REMINDER_SUBCOLLECTION = "reminders";

    public static void uploadReminderToFirebase(Reminder reminder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(ROOT_COLLECTION)
                .document(reminder.getUserId())
                .collection(REMINDER_SUBCOLLECTION)
                .document(reminder.getReminderId())
                .set(reminder)
                .addOnSuccessListener(unused -> Log.d("Firebase", "Reminder synced"))
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to sync", e));
    }

    public static void deleteReminderFromFirebase(String userId, String reminderId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(ROOT_COLLECTION)
                .document(userId)
                .collection(REMINDER_SUBCOLLECTION)
                .document(reminderId)
                .delete()
                .addOnSuccessListener(unused -> Log.d("Firebase", "Reminder deleted from cloud"))
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to delete from cloud", e));
    }

    public static void fetchRemindersForUser(String userId, OnSuccessListener<List<Reminder>> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(ROOT_COLLECTION)
                .document(userId)
                .collection(REMINDER_SUBCOLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Reminder> reminders = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        reminders.add(doc.toObject(Reminder.class));
                    }
                    listener.onSuccess(reminders);
                });
    }

    // NEW: Real-time sync listener
    public static ListenerRegistration listenToRemindersForUser(String userId, EventListener<QuerySnapshot> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(ROOT_COLLECTION)
                .document(userId)
                .collection(REMINDER_SUBCOLLECTION)
                .addSnapshotListener(listener);
    }
}
