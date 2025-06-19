package com.thesis.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thesis.app.utils.Constants;
import com.thesis.app.models.User;

public class SignupActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnSignup;
    private TextView tvLogin;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeViews();
        initializeFirebase();
        setupClickListeners();
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.et_username);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSignup = findViewById(R.id.btn_signup);
        tvLogin = findViewById(R.id.tv_login);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void setupClickListeners() {
        btnSignup.setOnClickListener(v -> performSignup());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void performSignup() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!validateInputs(username, email, password, confirmPassword)) {
            return;
        }

        showLoading(true);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            saveUserToDatabase(firebaseUser.getUid(), username, email);
                        }
                    } else {
                        showLoading(false);
                        showErrorMessage(getErrorMessage(task.getException()));
                    }
                });
    }

    private boolean validateInputs(String username, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            showErrorMessage(Constants.ERROR_EMPTY_FIELDS);
            return false;
        }

        if (username.length() < 3) {
            showErrorMessage("Username must be at least 3 characters");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showErrorMessage(Constants.ERROR_INVALID_EMAIL);
            return false;
        }

        if (password.length() < 6) {
            showErrorMessage(Constants.ERROR_WEAK_PASSWORD);
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showErrorMessage("Passwords do not match");
            return false;
        }
        if (!password.matches(passwordPattern)) {
            showErrorMessage("Password must contain at least one uppercase letter, one lowercase letter, and one number");
            return false;
        }

        return true;
    }

    private void saveUserToDatabase(String uid, String username, String email) {
        User user = new User(uid, username, email);
        // Set profile image as "image" placeholder text
        user.setProfileImageUrl("null");

        mDatabase.child(Constants.USERS_NODE).child(uid).setValue(user)
                .addOnCompleteListener(task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        showSuccessMessage();
                        // Navigate to main activity
                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                        finish();
                    } else {
                        showErrorMessage("Failed to save user data. Please try again.");
                    }
                });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSignup.setEnabled(!show);
        btnSignup.setText(show ? "" : "Sign Up");
    }

    private void showSuccessMessage() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), Constants.SUCCESS_SIGNUP, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.success_green));
        snackbar.setTextColor(ContextCompat.getColor(this, R.color.white));
        snackbar.show();
    }

    private void showErrorMessage(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.error_red));
        snackbar.setTextColor(ContextCompat.getColor(this, R.color.white));
        snackbar.show();
    }

    private String getErrorMessage(Exception exception) {
        if (exception == null) return Constants.ERROR_GENERIC;

        String message = exception.getMessage();
        if (message != null) {
            if (message.contains("email address is already in use")) {
                return "This email is already registered. Please use a different email.";
            } else if (message.contains("network error")) {
                return Constants.ERROR_NETWORK;
            }
        }
        return Constants.ERROR_GENERIC;
    }
}
