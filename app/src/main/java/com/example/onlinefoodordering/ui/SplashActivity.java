package com.example.onlinefoodordering.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onlinefoodordering.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // SESSION CHECK LOGIC START
                android.content.SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
                boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);

                Intent intent;
                if (isLoggedIn) {
                    // Agar login hai toh seedha Home par
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                } else {
                    // Nahi toh Login page par
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }

                startActivity(intent);
                finish();
                // SESSION CHECK LOGIC END
            }
        }, 3000);
    }
}