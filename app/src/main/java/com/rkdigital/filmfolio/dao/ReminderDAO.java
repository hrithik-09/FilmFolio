package com.rkdigital.filmfolio.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.rkdigital.filmfolio.model.Reminder;

import java.util.List;
@Dao
public interface ReminderDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertReminder(Reminder reminder);

    @Query("SELECT * FROM reminders WHERE userId = :userId")
    LiveData<List<Reminder>> getRemindersByUser(String userId);

    @Query("SELECT * FROM reminders WHERE reminderId = :reminderId")
    Reminder getReminderById(String reminderId);

    @Query("SELECT * FROM reminders WHERE movieId = :movieId AND userId = :userId")
    LiveData<Reminder> getReminderForMovie(int movieId, String userId);

    @Delete
    void deleteReminder(Reminder reminder);

    @Update
    void updateReminder(Reminder reminder);

    @Query("DELETE FROM reminders")
    void deleteAllReminder();
}
