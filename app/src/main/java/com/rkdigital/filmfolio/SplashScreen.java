package com.rkdigital.filmfolio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreen extends AppCompatActivity implements SharedPreferenceObserver.OnPreferenceChangeListener{
    private SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(()->{

            Boolean isLogged = sharedPreferencesHelper.getBoolean(
                    SharedPreferencesHelper.getInstance(this).getAppPrefs(),
                    SharedPrefsKeys.IS_LOGGED_IN, false);
            if (isLogged) {
                Intent startintent=new Intent(SplashScreen.this, MainActivity.class);
                startActivity(startintent);
            }
            else{
                Intent startIntent= new Intent(SplashScreen.this, LoginScreen.class);
                startActivity(startIntent);
            }
            finish();
        },1000);
    }

    @Override
    public void onPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}