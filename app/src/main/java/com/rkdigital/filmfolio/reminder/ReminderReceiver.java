package com.rkdigital.filmfolio.reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.rkdigital.filmfolio.R;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String movieTitle = intent.getStringExtra("movie_title");
        boolean isExactTime = intent.getBooleanExtra("is_exact_time", false);
        int reminderId = intent.getIntExtra("reminder_id", 0);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (isExactTime) {
            // Exact time alarm notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "movie_reminder_channel")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Movie Time!")
                    .setContentText("It's time for: " + movieTitle)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setOnlyAlertOnce(true)
                    .addAction(R.drawable.ic_launcher_foreground, "DISMISS",
                            getDismissPendingIntent(context, reminderId));

            notificationManager.notify(reminderId, builder.build());
        } else {
            // 1-hour prior notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "movie_reminder_channel")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Upcoming Movie Reminder")
                    .setContentText(movieTitle + " starts in 1 hour")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND);

            notificationManager.notify(reminderId + 1000, builder.build()); // Different ID
        }
    }

    private PendingIntent getDismissPendingIntent(Context context, int reminderId) {
        Intent dismissIntent = new Intent(context, DismissReceiver.class);
        dismissIntent.putExtra("reminder_id", reminderId);
        return PendingIntent.getBroadcast(
                context,
                reminderId,
                dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}

