package com.thesis.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.app.utils.AlertHelper;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private static final String PREF_NAME = "UserSession";

    // UI Components
    private TextView tvHeaderTitle;
    private ImageView btnBack;
    private MaterialCardView cardNotifications, cardDisplay, cardEditProfile, cardDeleteAccount;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private SharedPreferences sharedPreferences;

    // User data
    private String currentUserId, currentUsername, currentEmail, currentProfileImageBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeFirebase();
        initializeViews();
        setupHeader();
        loadUserData();
        setupClickListeners();
        configureStatusBar();
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }

    private void initializeViews() {
        // Header components
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        btnBack = findViewById(R.id.btn_back);

        // Settings cards
        cardNotifications = findViewById(R.id.card_notifications);
        cardDisplay = findViewById(R.id.card_display);
        cardEditProfile = findViewById(R.id.card_profile);
        cardDeleteAccount = findViewById(R.id.card_delete_account);
    }

    private void configureStatusBar() {
        // Set status bar color to match your header
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue));

        // Make status bar icons dark for better visibility
        setStatusBarIconsAppearance();
    }

    private void setStatusBarIconsAppearance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                );
            }
        } else {
            View decorView = getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(flags);
        }
    }

    private void setupHeader() {
        tvHeaderTitle.setText("Settings");
    }

    private void loadUserData() {
        currentUsername = sharedPreferences.getString("username", "User");
        currentEmail = sharedPreferences.getString("userEmail", "user@example.com");
        currentUserId = sharedPreferences.getString("userId", "");
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Notifications card
        cardNotifications.setOnClickListener(v ->
                AlertHelper.showInfoSnackbar(findViewById(android.R.id.content),
                        "Notifications feature coming soon!"));

        // Display settings card
        cardDisplay.setOnClickListener(v ->
                AlertHelper.showInfoSnackbar(findViewById(android.R.id.content),
                        "Display settings feature coming soon!"));

        // Edit profile card
        cardEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            intent.putExtra("username", currentUsername);
            intent.putExtra("email", currentEmail);
            intent.putExtra("userId", currentUserId);
            startActivity(intent);
        });

        // Delete account card - Using custom dialog
        cardDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
    }

    private void showDeleteAccountDialog() {
        AlertHelper.showDeleteAccountDialog(this, new AlertHelper.DialogCallback() {
            @Override
            public void onPositiveClick() {
                deleteAccount();
            }

            @Override
            public void onNegativeClick() {
                AlertHelper.showInfoSnackbar(findViewById(android.R.id.content),
                        "Account deletion cancelled");
            }
        });
    }

    private void deleteAccount() {
        if (currentUserId == null || currentUserId.isEmpty()) {
            AlertHelper.showErrorSnackbar(findViewById(android.R.id.content),
                    "User ID not found");
            return;
        }

        // Show loading message
        AlertHelper.showInfoSnackbar(findViewById(android.R.id.content),
                "Deleting account...");

        // Delete user data from Firebase Database
        mDatabase.child("users").child(currentUserId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Delete Firebase Auth account
                        if (mAuth.getCurrentUser() != null) {
                            mAuth.getCurrentUser().delete()
                                    .addOnCompleteListener(deleteTask -> {
                                        if (deleteTask.isSuccessful()) {
                                            // Clear shared preferences
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.clear();
                                            editor.apply();

                                            // Show success and navigate to login
                                            AlertHelper.showSuccessSnackbar(findViewById(android.R.id.content),
                                                    "Account deleted successfully");

                                            // Navigate to login screen after delay
                                            findViewById(android.R.id.content).postDelayed(() -> {
                                                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }, 1500);

                                        } else {
                                            AlertHelper.showErrorSnackbar(findViewById(android.R.id.content),
                                                    "Failed to delete account: " + deleteTask.getException().getMessage());
                                        }
                                    });
                        }
                    } else {
                        AlertHelper.showErrorSnackbar(findViewById(android.R.id.content),
                                "Failed to delete user data: " + task.getException().getMessage());
                    }
                });
    }
}
