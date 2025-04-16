package com.rkdigital.filmfolio.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "reminders")
public class Reminder {
    @PrimaryKey@NonNull
    private String reminderId;

    private int movieId;

    private String movieTitle;
    private long reminderTimeMillis;
    private String userId;
    private long lastModified;

    public Reminder() {
        this.reminderId = UUID.randomUUID().toString();
        this.lastModified = System.currentTimeMillis();
    }


    // Getters & Setters
    public String getReminderId() { return reminderId; }

    public void setReminderId(String reminderId) {
        this.reminderId = reminderId;
    }
    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public long getReminderTimeMillis() { return reminderTimeMillis; }
    public void setReminderTimeMillis(long reminderTimeMillis) { this.reminderTimeMillis = reminderTimeMillis; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

}


