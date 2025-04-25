package com.rkdigital.filmfolio.storage;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.rkdigital.filmfolio.model.Wishlist;
import java.util.ArrayList;
import java.util.List;

public class FirebaseWishlistHelper {
    private static final String ROOT_COLLECTION = "users";
    private static final String WISHLIST_SUBCOLLECTION = "wishlist";

    public static void uploadWishlistToFirebase(Wishlist wishlist) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(ROOT_COLLECTION)
                .document(wishlist.getUserId())
                .collection(WISHLIST_SUBCOLLECTION)
                .document(wishlist.getWishlistId())
                .set(wishlist)
                .addOnSuccessListener(unused -> Log.d("Firebase", "Wishlist synced"))
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to sync", e));
    }

    public static void deleteWishlistFromFirebase(String userId, String wishlistId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(ROOT_COLLECTION)
                .document(userId)
                .collection(WISHLIST_SUBCOLLECTION)
                .document(wishlistId)
                .delete()
                .addOnSuccessListener(unused -> Log.d("Firebase", "Wishlist deleted from cloud"))
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to delete from cloud", e));
    }

    public static void fetchWishlistForUser(String userId, OnSuccessListener<List<Wishlist>> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(ROOT_COLLECTION)
                .document(userId)
                .collection(WISHLIST_SUBCOLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Wishlist> wishlist = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        wishlist.add(doc.toObject(Wishlist.class));
                    }
                    listener.onSuccess(wishlist);
                });
    }

    // Real-time sync listener
    public static ListenerRegistration listenToWishlistForUser(String userId, EventListener<QuerySnapshot> listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection(ROOT_COLLECTION)
                .document(userId)
                .collection(WISHLIST_SUBCOLLECTION)
                .addSnapshotListener(listener);
    }
}
