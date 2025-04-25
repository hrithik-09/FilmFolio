package com.rkdigital.filmfolio.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
@Database(entities = {Wishlist.class},version = 1)
public abstract class WishlistDatabase extends RoomDatabase {
    public abstract WishlistDAO getWishlistDAO();
    //Singleton Pattern
    private static WishlistDatabase dbInstance;
    public static synchronized WishlistDatabase getInstance(Context context){
        if (dbInstance==null){
            dbInstance= Room.databaseBuilder(context.getApplicationContext(),WishlistDatabase.class,"wishlist_db").fallbackToDestructiveMigration().build();
        }
        return dbInstance;
    }
}
