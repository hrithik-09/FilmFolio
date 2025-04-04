package com.rkdigital.filmfolio;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
public class SplashScreen extends AppCompatActivity{
    private SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(()->{

            Boolean isLogged = sharedPreferencesHelper.getBoolean(
                    sharedPreferencesHelper.getAppPrefs(),
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

}