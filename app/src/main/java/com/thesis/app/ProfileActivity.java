package com.thesis.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thesis.app.utils.AlertHelper;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_ID = "userId";

    // UI Components
    private TextView tvHeaderTitle;
    private ImageView btnBack;
    private CircleImageView profileImage;
    private TextView usernameText, emailText;
    private ImageView btnEditProfile;
    private MaterialButton btnLogoutMain;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private SharedPreferences sharedPreferences;

    // User data
    private String currentUserId, currentUsername, currentEmail;
    private String currentProfileImageBase64;
    private ValueEventListener profileListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        // Profile components
        profileImage = findViewById(R.id.profile_image);
        usernameText = findViewById(R.id.tv_username);
        emailText = findViewById(R.id.tv_email);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnLogoutMain = findViewById(R.id.btn_logout_main);
    }

    private void setupHeader() {
        tvHeaderTitle.setText("My Profile");
    }
    private void configureStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue));

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

    private void loadUserData() {
        Intent intent = getIntent();
        currentUsername = intent.getStringExtra("username");
        currentEmail = intent.getStringExtra("email");
        currentUserId = intent.getStringExtra("userId");

        if (currentUsername == null) {
            currentUsername = sharedPreferences.getString(KEY_USERNAME, "User");
            currentEmail = sharedPreferences.getString(KEY_USER_EMAIL, "user@example.com");
            currentUserId = sharedPreferences.getString(KEY_USER_ID, "");
        }

        usernameText.setText(currentUsername);
        emailText.setText(currentEmail);

        if (currentUserId != null && !currentUserId.isEmpty()) {
            loadUserProfileFromDatabase();
        }
    }

    private void loadUserProfileFromDatabase() {
        profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get updated user data
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String profileImageBase64 = dataSnapshot.child("profileImage").getValue(String.class);

                    // Update UI
                    if (username != null) {
                        usernameText.setText(username);
                        currentUsername = username;
                    }
                    if (email != null) {
                        emailText.setText(email);
                        currentEmail = email;
                    }

                    // Handle profile image with Base64 decoding
                    currentProfileImageBase64 = profileImageBase64;
                    loadProfileImageFromBase64(profileImageBase64);

                    // Update shared preferences with latest data
                    updateSharedPreferences(username, email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load profile data", databaseError.toException());
                AlertHelper.showErrorSnackbar(findViewById(android.R.id.content),
                        "Failed to load profile data");
            }
        };

        mDatabase.child("users").child(currentUserId).addValueEventListener(profileListener);
    }

    private void loadProfileImageFromBase64(String base64Image) {
        if (base64Image != null && !base64Image.isEmpty() && !base64Image.equals("null")) {
            try {
                // Decode Base64 string to bitmap
                byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                if (bitmap != null) {
                    // Set the decoded bitmap to profile image
                    profileImage.setImageBitmap(bitmap);

                    // Add animation for smooth transition
                    profileImage.setAlpha(0f);
                    profileImage.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .start();

                    Log.d(TAG, "Profile image loaded successfully from Base64");
                } else {
                    profileImage.setImageResource(R.drawable.ic_default_profile);
                    Log.w(TAG, "Failed to create bitmap from Base64 data");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error decoding Base64 image", e);
                profileImage.setImageResource(R.drawable.ic_default_profile);
            }
        } else {
            profileImage.setImageResource(R.drawable.ic_default_profile);
            Log.d(TAG, "No profile image data found, using default");
        }
    }

    private void updateSharedPreferences(String username, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (username != null) {
            editor.putString(KEY_USERNAME, username);
        }
        if (email != null) {
            editor.putString(KEY_USER_EMAIL, email);
        }
        editor.apply();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("username", currentUsername);
            intent.putExtra("email", currentEmail);
            intent.putExtra("userId", currentUserId);
            intent.putExtra("profileImageUrl", currentProfileImageBase64); // Pass Base64 data
            startActivityForResult(intent, 100); // Use result to refresh profile
        });

        // Logout button
        btnLogoutMain.setOnClickListener(v -> showLogoutDialog());

        // Profile image click for enlarged view
        profileImage.setOnClickListener(v -> {
            // Animate click
            profileImage.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        profileImage.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100);
                    });

            showImageViewDialog();
        });
    }

    private void showImageViewDialog() {
        if (currentProfileImageBase64 != null && !currentProfileImageBase64.isEmpty()) {
            AlertHelper.showInfoSnackbar(findViewById(android.R.id.content),
                    "Profile image preview - Full viewer coming soon!");
        } else {
            AlertHelper.showInfoSnackbar(findViewById(android.R.id.content),
                    "No profile image to display");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Refresh profile data
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("updated", false)) {
                // Profile was updated, refresh the data
                loadUserProfileFromDatabase();
                AlertHelper.showSuccessSnackbar(findViewById(android.R.id.content),
                        "Profile refreshed successfully!");
            }
        }
    }

    private void showLogoutDialog() {
        AlertHelper.showLogoutDialog(this, new AlertHelper.LogoutCallback() {
            @Override
            public void onConfirm() {
                // Sign out from Firebase
                mAuth.signOut();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                AlertHelper.showSuccessSnackbar(findViewById(android.R.id.content),
                        "Logged out successfully");

                // Navigate to login screen
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {
                AlertHelper.showInfoSnackbar(findViewById(android.R.id.content),
                        "Logout cancelled");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (profileListener != null && currentUserId != null) {
            mDatabase.child("users").child(currentUserId).removeEventListener(profileListener);
        }
    }
}
