package com.rkdigital.filmfolio.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;

import com.rkdigital.filmfolio.model.Wishlist;
import com.rkdigital.filmfolio.dao.WishlistDAO;
import com.rkdigital.filmfolio.database.WishlistDatabase;
import com.rkdigital.filmfolio.storage.FirebaseWishlistHelper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WishlistRepository {

    private final WishlistDAO wishlistDAO;
    ExecutorService executor;
    Handler handler;
    private ListenerRegistration firebaseListener;
    private final String currentUserId;

    private final Application application;
    public WishlistRepository(Application application,String userId) {
        WishlistDatabase wishlistDatabase = WishlistDatabase.getInstance(application);
        this.wishlistDAO = wishlistDatabase.getWishlistDAO();
        this.application = application;
        //  Used for background database operation
        executor= Executors.newSingleThreadExecutor();

        //  Used for updating the UI
        handler=new Handler(Looper.getMainLooper());

        this.currentUserId = userId;

        // Start real-time sync listener
        listenToFirebaseUpdates();
    }

    private void listenToFirebaseUpdates() {
        firebaseListener = FirebaseWishlistHelper.listenToWishlistForUser(currentUserId, (snapshots, error) -> {
            executor.execute(() -> {
                for (DocumentChange change : snapshots.getDocumentChanges()) {
                    Wishlist cloudWishlist = change.getDocument().toObject(Wishlist.class);

                    switch (change.getType()) {
                        case ADDED:
                        case MODIFIED:

                            wishlistDAO.insertWishlist(cloudWishlist);
                            break;
                        case REMOVED:
                            wishlistDAO.deleteWishlist(cloudWishlist);
                            break;
                    }
                }
            });
        });
    }
    public void clearListener() {
        if (firebaseListener != null) {
            firebaseListener.remove();
            firebaseListener = null;
        }
    }


    public void syncWishlist(Wishlist wishlist) {
        executor.execute(() -> {
            wishlistDAO.insertWishlist(wishlist);
            FirebaseWishlistHelper.uploadWishlistToFirebase(wishlist);
        });
    }

    public void deleteWishlist(Wishlist wishlist) {
        executor.execute(() -> {
            // 1. Delete from local DB
            wishlistDAO.deleteWishlist(wishlist);
            // 2. Delete from Firebase (which will trigger the listener)
            FirebaseWishlistHelper.deleteWishlistFromFirebase(
                    wishlist.getUserId(),
                    wishlist.getWishlistId()
            );
        });
    }

    public LiveData<List<Wishlist>> getWishlistByUser(String userId) {
        Log.d("WishlistDAO", "Querying wishlist for user: " + userId);
        return wishlistDAO.getWishlistByUser(userId);
    }

    public LiveData<Wishlist> getWishlistForMovie(int movieId, String userId) {
        return wishlistDAO.getWishlistForMovie(movieId,userId);
    }

    public void clearLocalWishlist(){
        executor.execute(() -> {
            wishlistDAO.deleteAllWishlist();
        });
    }


}
