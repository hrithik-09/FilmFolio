package com.rkdigital.filmfolio;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.rkdigital.filmfolio.model.Reminder;

import java.util.concurrent.TimeUnit;

public class ReminderScheduler {
    private static final String CHANNEL_ID = "movie_reminder_channel";
    private static final int NOTIFICATION_ID = 100;
    private static final int ALARM_ID = 101;

    public static void scheduleReminder(Context context, Reminder reminder) {
        createNotificationChannel(context);

        // Schedule exact time alarm
        scheduleExactTimeAlarm(context, reminder);

        // Schedule 1-hour prior notification
        if (reminder.getReminderTimeMillis() - System.currentTimeMillis() > 3600000) {
            scheduleOneHourPriorNotification(context, reminder);
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Movie Reminders",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for movie reminders");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private static void scheduleExactTimeAlarm(Context context, Reminder reminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("reminder_id", reminder.getId());
        intent.putExtra("movie_title", reminder.getMovieTitle());
        intent.putExtra("is_exact_time", true);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_ID + reminder.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminder.getReminderTimeMillis(),
                    pendingIntent);
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    reminder.getReminderTimeMillis(),
                    pendingIntent);
        }
    }

    private static void scheduleOneHourPriorNotification(Context context, Reminder reminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("reminder_id", reminder.getId());
        intent.putExtra("movie_title", reminder.getMovieTitle());
        intent.putExtra("is_exact_time", false);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                NOTIFICATION_ID + reminder.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long triggerAtMillis = reminder.getReminderTimeMillis() - 3600000; // 1 hour before

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent);
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent);
        }
    }
}


