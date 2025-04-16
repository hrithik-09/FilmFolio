package com.rkdigital.filmfolio;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.rkdigital.filmfolio.model.Reminder;

import java.util.Objects;
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

    @SuppressLint("ScheduleExactAlarm")
    private static void scheduleExactTimeAlarm(Context context, Reminder reminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("reminder_id", reminder.getReminderId());
        intent.putExtra("movie_title", reminder.getMovieTitle());
        intent.putExtra("is_exact_time", true);

        // Alternative more robust request code generation
        int requestCode = Objects.hash(ALARM_ID, reminder.getReminderId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
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

    @SuppressLint("ScheduleExactAlarm")
    private static void scheduleOneHourPriorNotification(Context context, Reminder reminder) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("reminder_id", reminder.getReminderId());
        intent.putExtra("movie_title", reminder.getMovieTitle());
        intent.putExtra("is_exact_time", false);
        int requestCode = Objects.hash(NOTIFICATION_ID, reminder.getReminderId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
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
    public static void cancelReminder(Context context, String reminderId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Cancel exact time alarm
        int alarmRequestCode = ALARM_ID + reminderId.hashCode();
        Intent alarmIntent = new Intent(context, ReminderReceiver.class);
        PendingIntent.getBroadcast(context, alarmRequestCode, alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE).cancel();

        // Cancel 1-hour prior notification
        int notificationRequestCode = NOTIFICATION_ID + reminderId.hashCode();
        Intent notificationIntent = new Intent(context, ReminderReceiver.class);
        PendingIntent.getBroadcast(context, notificationRequestCode, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE).cancel();
    }

}


