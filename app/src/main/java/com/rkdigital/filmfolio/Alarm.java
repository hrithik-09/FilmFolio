package com.rkdigital.filmfolio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.TimeUnit;

public class Alarm extends AppCompatActivity {
    private TextView tvAlarmText;
    private Button btnDismiss, btnSnooze;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        tvAlarmText = findViewById(R.id.tvAlarmText);
        btnDismiss = findViewById(R.id.btnDismiss);
        btnSnooze = findViewById(R.id.btnSnooze);

        String title = getIntent().getStringExtra("movieTitle");
        TextView titleView = findViewById(R.id.tvAlarmText);
        titleView.setText("Time to Watch: " + title);

        findViewById(R.id.btnDismiss).setOnClickListener(v -> finish());
        findViewById(R.id.btnSnooze).setOnClickListener(v -> {
            // reschedule after 10 minutes
            long snoozeTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10);
            // Assuming you pass reminder ID etc.
            // You can directly reschedule using ReminderScheduler with updated time
            finish();
        });

    }
    public static final String ACTION_DISMISS_ALARM = "com.rkdigital.filmfolio.alarm.ACTION_DISMISS_ALARM";

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(dismissReceiver, new IntentFilter(ACTION_DISMISS_ALARM));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(dismissReceiver);
    }

    private final BroadcastReceiver dismissReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish(); // Closes the alarm activity
        }
    };

}