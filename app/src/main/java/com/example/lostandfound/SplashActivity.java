package com.example.lostandfound;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;

import android.os.Handler;
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        int SPLASH_DISPLAY_LENGTH = 4000; // Splash screen delay time in milliseconds
        new Handler().postDelayed(() -> {
//            startActivity(new Intent(this, MainActivity.class));
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("IsLoggedIn", false);

            if (isLoggedIn) {
                // If the user is already logged in, redirect to HomeActivity
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else {
                // If the user is not logged in, redirect to LogInActivity
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }

//            SplashActivity.this.finish();
        }, SPLASH_DISPLAY_LENGTH);
    }
}