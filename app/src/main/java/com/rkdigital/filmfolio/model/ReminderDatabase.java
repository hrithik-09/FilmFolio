package com.rkdigital.filmfolio.model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Reminder.class},version = 1)
public abstract class ReminderDatabase extends RoomDatabase {
    public abstract ReminderDAO getReminderDAO();

    //Singleton Pattern
    private static ReminderDatabase dbInstance;
    public static synchronized ReminderDatabase getInstance(Context context){
        if (dbInstance==null){
            dbInstance= Room.databaseBuilder(context.getApplicationContext(),ReminderDatabase.class,"reminder_db").fallbackToDestructiveMigration().build();
        }
        return dbInstance;
    }
}
