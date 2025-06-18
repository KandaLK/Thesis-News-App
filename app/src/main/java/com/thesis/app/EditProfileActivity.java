package com.thesis.app;



import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.thesis.app.utils.AlertHelper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";
    private static final long MAX_FILE_SIZE = 1536000; // 1.5 MB

    // UI Components
    private TextView tvHeaderTitle;
    private ImageView btnBack;
    private CircleImageView profileImage;
    private TextInputEditText etUsername, etEmail, etCurrentPassword, etNewPassword;
    private TextInputLayout tilUsername, tilEmail, tilCurrentPassword, tilNewPassword;
    private MaterialButton btnSave, btnCancel, btnUploadImage;
    private LinearProgressIndicator progressIndicator;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String currentUserId, originalUsername, originalEmail;
    private Bitmap selectedImageBitmap;
    private boolean isImageChanged = false;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeFirebase();
        initializeViews();
        setupHeader();
        setupEnhancedStyling();
        setupImageLaunchers();
        loadUserData();
        setupClickListeners();
        configureStatusBar();
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void initializeViews() {
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        btnBack = findViewById(R.id.btn_back);

        // Form components
        profileImage = findViewById(R.id.profile_image);
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        tilUsername = findViewById(R.id.til_username);
        tilEmail = findViewById(R.id.til_email);
        tilCurrentPassword = findViewById(R.id.til_current_password);
        tilNewPassword = findViewById(R.id.til_new_password);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        btnUploadImage = findViewById(R.id.btn_upload_image);
        progressIndicator = findViewById(R.id.progress_indicator);
    }

    private void setupHeader() {
        tvHeaderTitle.setText("Edit Profile");
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
            // Legacy for API 23-29
            View decorView = getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(flags);
        }
    }

    private void setupEnhancedStyling() {
        // Enhanced Cancel button styling
        btnCancel.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.error_red)));
        btnCancel.setTextColor(ContextCompat.getColor(this, R.color.white));
        btnCancel.setIconTint(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.white)));


        btnSave.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.primary_blue)));
        btnSave.setTextColor(ContextCompat.getColor(this, R.color.white));
        btnSave.setIconTint(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.white)));


        btnUploadImage.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.warning_orange)));
        btnUploadImage.setTextColor(ContextCompat.getColor(this, R.color.primary_blue));
        btnUploadImage.setIconTint(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.primary_blue)));
        btnUploadImage.setStrokeColor(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.primary_blue_light)));
        btnUploadImage.setStrokeWidth(4);

        btnCancel.setCornerRadius(48);
        btnSave.setCornerRadius(48);
        btnUploadImage.setCornerRadius(78);
    }

    private void setupImageLaunchers() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            processSelectedImage(imageUri);
                        }
                    }
                });

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            if (imageBitmap != null) {
                                selectedImageBitmap = compressImage(imageBitmap);
                                updateImagePreview();
                                isImageChanged = true;
                            }
                        }
                    }
                });
    }

    private void loadUserData() {
        Intent intent = getIntent();
        originalUsername = intent.getStringExtra("username");
        originalEmail = intent.getStringExtra("email");
        currentUserId = intent.getStringExtra("userId");
        String profileImageUrl = intent.getStringExtra("profileImageUrl");

        if (originalUsername != null) {
            etUsername.setText(originalUsername);
        }
        if (originalEmail != null) {
            etEmail.setText(originalEmail);
        }

        loadExistingProfileImage(profileImageUrl);
    }

    private void loadExistingProfileImage(String base64Image) {
        if (base64Image != null && !base64Image.isEmpty() && !base64Image.equals("null")) {
            try {
                byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                if (bitmap != null) {
                    profileImage.setImageBitmap(bitmap);
                } else {
                    profileImage.setImageResource(R.drawable.ic_default_profile);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading profile image", e);
                profileImage.setImageResource(R.drawable.ic_default_profile);
            }
        } else {
            profileImage.setImageResource(R.drawable.ic_default_profile);
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            animateButton(btnSave);
            showUpdateConfirmationDialog();
        });

        btnCancel.setOnClickListener(v -> {
            animateButton(btnCancel);
            showCancelConfirmationDialog();
        });

        btnUploadImage.setOnClickListener(v -> {
            animateButton(btnUploadImage);
            showImageSelectionDialog();
        });

        profileImage.setOnClickListener(v -> {
            animateImageView(profileImage);
            showImageSelectionDialog();
        });
    }

    private void showImageSelectionDialog() {
        String[] options = {"Gallery", "Camera"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openGallery();
                    } else {
                        openCamera();
                    }
                })
                .show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void processSelectedImage(Uri imageUri) {
        try {
            // Check file size
            @SuppressLint("Recycle") InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream != null && inputStream.available() > MAX_FILE_SIZE) {
                Toast.makeText(this, "Image size must be less than 1.5MB", Toast.LENGTH_SHORT).show();
                return;
            }

            inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

            if (originalBitmap != null) {
                selectedImageBitmap = compressImage(originalBitmap);
                updateImagePreview();
                isImageChanged = true;
                Toast.makeText(this, "Image selected. Click Update to save changes.",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error processing selected image", e);
            Toast.makeText(this, "Error processing selected image", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap compressImage(Bitmap originalBitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 90;

        originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

         while (baos.toByteArray().length > MAX_FILE_SIZE && quality > 10) {
            baos.reset();
            quality -= 10;
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }

        if (baos.toByteArray().length > MAX_FILE_SIZE) {
            return resizeBitmap(originalBitmap);
        }

        byte[] compressedData = baos.toByteArray();
        return BitmapFactory.decodeByteArray(compressedData, 0, compressedData.length);
    }

    private Bitmap resizeBitmap(Bitmap originalBitmap) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        float scaleFactor = 0.8f;
        int newWidth = (int) (width * scaleFactor);
        int newHeight = (int) (height * scaleFactor);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

        if (baos.toByteArray().length > MAX_FILE_SIZE) {
            return resizeBitmap(resizedBitmap);
        }

        return resizedBitmap;
    }

    private void updateImagePreview() {
        if (selectedImageBitmap != null) {
            profileImage.setImageBitmap(selectedImageBitmap);
            animateImageView(profileImage);
        }
    }

    private void animateButton(MaterialButton button) {
        button.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> button.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(100));
    }

    private void animateImageView(CircleImageView imageView) {
        imageView.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(150)
                .withEndAction(() -> imageView.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(150));
    }

    private void showCancelConfirmationDialog() {
        AlertHelper.showCancelConfirmationDialog(this, new AlertHelper.DialogCallback() {
            @Override
            public void onPositiveClick() {
                finish();
            }

            @Override
            public void onNegativeClick() {
                btnSave.setEnabled(true);
                btnSave.setText("Update Profile");
            }
        });
    }

    private void showUpdateConfirmationDialog() {
        String newUsername = etUsername.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String currentPassword = etCurrentPassword.getText().toString().trim();

        if (!validateInputs(newUsername, newEmail, currentPassword, newPassword)) {
            return;
        }

        StringBuilder changesSummary = new StringBuilder();
        if (!newUsername.equals(originalUsername)) {
            changesSummary.append("Username: ").append(originalUsername).append(" → ").append(newUsername).append("\n");
        }
        if (!newEmail.equals(originalEmail)) {
            changesSummary.append("Email: ").append(originalEmail).append(" → ").append(newEmail).append("\n");
        }
        if (!TextUtils.isEmpty(newPassword)) {
            changesSummary.append("Password: Will be updated\n");
        }
        if (isImageChanged) {
            changesSummary.append("Profile Image: Will be updated\n");
        }

        if (changesSummary.length() == 0) {
            Toast.makeText(this, "No changes detected", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertHelper.showEditProfileDialog(this, changesSummary.toString(), new AlertHelper.DialogCallback() {
            @Override
            public void onPositiveClick() {
                saveProfile();
            }

            @Override
            public void onNegativeClick() {
                Toast.makeText(EditProfileActivity.this, "Update cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String newUsername = etUsername.getText().toString().trim();
        String newEmail = etEmail.getText().toString().trim();
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        showEnhancedLoading(true);

        boolean emailChanged = !newEmail.equals(originalEmail);
        boolean passwordChanged = !TextUtils.isEmpty(newPassword);

        if (emailChanged || passwordChanged) {
            reAuthenticateAndUpdate(newUsername, newEmail, currentPassword, newPassword);
        } else {
            updateProfile(newUsername, newEmail, null);
        }
    }

    private boolean validateInputs(String username, String email, String currentPassword, String newPassword) {
        boolean isValid = true;

        // Clear previous errors
        tilUsername.setError(null);
        tilEmail.setError(null);
        tilCurrentPassword.setError(null);
        tilNewPassword.setError(null);

        // Username validation
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Username cannot be empty");
            etUsername.requestFocus();
            isValid = false;
        } else if (username.length() < 3) {
            tilUsername.setError("Username must be at least 3 characters");
            etUsername.requestFocus();
            isValid = false;
        }

        // Email validation
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email cannot be empty");
            if (isValid) etEmail.requestFocus();
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email");
            if (isValid) etEmail.requestFocus();
            isValid = false;
        }

        // Password validation
        boolean emailChanged = !email.equals(originalEmail);
        boolean passwordChanged = !TextUtils.isEmpty(newPassword);

        if ((emailChanged || passwordChanged) && TextUtils.isEmpty(currentPassword)) {
            tilCurrentPassword.setError("Current password required for email/password changes");
            if (isValid) etCurrentPassword.requestFocus();
            isValid = false;
        }

        if (passwordChanged && newPassword.length() < 6) {
            tilNewPassword.setError("New password must be at least 6 characters");
            if (isValid) etNewPassword.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private void reAuthenticateAndUpdate(String newUsername, String newEmail, String currentPassword, String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            showError("User not authenticated");
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(originalEmail, currentPassword);
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateEmailAndPassword(user, newUsername, newEmail, newPassword);
                    } else {
                        showEnhancedLoading(false);
                        tilCurrentPassword.setError("Current password is incorrect");
                        etCurrentPassword.requestFocus();
                    }
                });
    }

    private void updateEmailAndPassword(FirebaseUser user, String newUsername, String newEmail, String newPassword) {
        if (!newEmail.equals(originalEmail)) {
            // Use verifyBeforeUpdateEmail instead of updateEmail
            user.verifyBeforeUpdateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Email verification sent
                            showError("Verification email sent to " + newEmail + ". Please verify to complete update.");
                            if (!TextUtils.isEmpty(newPassword)) {
                                updatePassword(user, newUsername, newEmail, newPassword);
                            } else {
                                updateProfile(newUsername, originalEmail, null); // Keep original until verified
                            }
                        } else {
                            showEnhancedLoading(false);
                            Exception exception = task.getException();
                            if (exception != null && exception.getMessage() != null) {
                                if (exception.getMessage().contains("auth/requires-recent-login")) {
                                    tilCurrentPassword.setError("Please re-enter your current password");
                                    etCurrentPassword.requestFocus();
                                } else {
                                    showError("Email update failed: " + exception.getMessage());
                                }
                            }
                        }
                    });
        } else if (!TextUtils.isEmpty(newPassword)) {
            updatePassword(user, newUsername, newEmail, newPassword);
        } else {
            updateProfile(newUsername, newEmail, null);
        }
    }

    private void updatePassword(FirebaseUser user, String newUsername, String newEmail, String newPassword) {
        user.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updateProfile(newUsername, newEmail, null);
                    } else {
                        showEnhancedLoading(false);
                        showError("Failed to update password: " + getErrorMessage(task.getException()));
                    }
                });
    }

    private void updateProfile(String newUsername, String newEmail, String imageUrl) {
        if (currentUserId == null) {
            showEnhancedLoading(false);
            showError("User ID not found");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("username", newUsername);
        updates.put("email", newEmail);
        updates.put("updatedAt", ServerValue.TIMESTAMP);

        // Handle image update
        if (isImageChanged && selectedImageBitmap != null) {
            String base64Image = convertBitmapToBase64(selectedImageBitmap);
            if (base64Image != null) {
                updates.put("profileImage", base64Image);
                updates.put("profileImageTimestamp", ServerValue.TIMESTAMP);
            }
        }

        mDatabase.child("users").child(currentUserId).updateChildren(updates)
                .addOnCompleteListener(task -> {
                    showEnhancedLoading(false);
                    if (task.isSuccessful()) {
                        showSuccessAlert();
                    } else {
                        showError("Failed to update profile: " + getErrorMessage(task.getException()));
                    }
                });
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "Error converting bitmap to base64", e);
            return null;
        }
    }

    private void showEnhancedLoading(boolean show) {
        progressIndicator.setVisibility(show ? View.VISIBLE : View.GONE);

        btnSave.setEnabled(!show);
        btnCancel.setEnabled(!show);
        btnUploadImage.setEnabled(!show);
        profileImage.setClickable(!show);

        if (show) {
            btnSave.setText("Saving...");
            btnSave.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.text_secondary)));
        } else {
            btnSave.setText("Update Profile");
            btnSave.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.primary_blue)));
        }
    }

    private void showError(String message) {
        AlertHelper.showErrorSnackbar(findViewById(android.R.id.content), message);
    }

    private void showSuccessAlert() {
        AlertHelper.showSuccessSnackbar(findViewById(android.R.id.content), "Profile updated successfully!");

        // Return to profile activity with updated data
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated", true);
        setResult(RESULT_OK, resultIntent);

        new Handler(Looper.getMainLooper()).postDelayed(this::finish, 1500);
    }


    private String getErrorMessage(Exception exception) {
        if (exception == null) return "Unknown error occurred";
        String message = exception.getMessage();
        return message != null ? message : "Unknown error occurred";
    }
}
