package com.rkdigital.filmfolio.reminder;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("reminder_id", 0);
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}