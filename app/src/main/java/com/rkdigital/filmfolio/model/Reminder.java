package com.rkdigital.filmfolio.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders")
public class Reminder {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int movieId;
    private String movieTitle;
    private long reminderTimeMillis;
    private String userId;

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public long getReminderTimeMillis() { return reminderTimeMillis; }
    public void setReminderTimeMillis(long reminderTimeMillis) { this.reminderTimeMillis = reminderTimeMillis; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}


