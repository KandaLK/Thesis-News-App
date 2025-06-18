package com.thesis.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    // Splash screen display duration in milliseconds
    private static final int SPLASH_DURATION = 2000;

    // UI Components
    private ImageView logoImageView;
    private TextView appNameTextView;
    private TextView taglineTextView;
    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize UI components
        initializeViews();

        // Configure splash screen
        configureSplashScreen();

        // Start main activity after delay
        startMainActivityWithDelay();
    }


    private void initializeViews() {
        logoImageView = findViewById(R.id.logo_imageview);
        appNameTextView = findViewById(R.id.app_name_textview);
        taglineTextView = findViewById(R.id.app_tagline_textview);
        loadingProgressBar = findViewById(R.id.loading_progressbar);
    }

    private void configureSplashScreen() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        if (logoImageView != null) {
            logoImageView.setImageResource(R.drawable.main_logo);
        }

        if (appNameTextView != null) {
            appNameTextView.setText(getString(R.string.app_name));
        }
    }

    private void startMainActivityWithDelay() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create intent
                Intent mainActivityIntent = new Intent(SplashActivity.this, MainActivity.class);


                startActivity(mainActivityIntent);

                // Apply transition
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, SPLASH_DURATION);
    }

    private void startMainActivityImmediately() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void performInitializationTasks() {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
