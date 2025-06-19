package com.thesis.app;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thesis.app.utils.Constants;
import com.thesis.app.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private SessionManager sessionManager;
    private Button btnLogin;
    private TextView tvSignup;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        initializeFirebase();
        setupClickListeners();
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignup = findViewById(R.id.tv_signup);
        progressBar = findViewById(R.id.progress_bar);
    }



    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> performLogin());

        tvSignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });
    }
    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInputs(email, password)) return;

        showLoading(true);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserSession(user);
                            showSuccessMessage(Constants.SUCCESS_LOGIN);
                            redirectToMainActivity(); // Use the new method
                        }
                    } else {
                        showErrorMessage(getErrorMessage(task.getException()));
                    }
                });
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showErrorMessage(Constants.ERROR_EMPTY_FIELDS);
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showErrorMessage(Constants.ERROR_INVALID_EMAIL);
            return false;
        }

        return true;
    }
    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);

        // Check if user is already logged in with valid session
        if (sessionManager.isSessionValid()) {
            redirectToMainActivity();
        }
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void saveUserSession(FirebaseUser user) {
        String username = user.getDisplayName();
        if (username == null || username.isEmpty()) {
            // Extract username from email if displayName is not set
            username = user.getEmail().split("@")[0];
        }

        sessionManager.createLoginSession(
                user.getUid(),
                username,
                user.getEmail()
        );
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        btnLogin.setText(show ? "" : "Login");
    }

    private void showSuccessMessage(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(getResources().getColor(R.color.success_green));
        snackbar.setTextColor(getResources().getColor(R.color.white));
        snackbar.show();
    }

    private void showErrorMessage(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(getResources().getColor(R.color.error_red));
        snackbar.setTextColor(getResources().getColor(R.color.white));
        snackbar.show();
    }

    private String getErrorMessage(Exception exception) {
        if (exception == null) return Constants.ERROR_GENERIC;

        String message = exception.getMessage();
        if (message != null) {
            if (message.contains("no user record")) {
                return "No account found with this email. Please sign up first.";
            } else if (message.contains("password is invalid")) {
                return "Incorrect password. Please try again.";
            } else if (message.contains("network error")) {
                return Constants.ERROR_NETWORK;
            }
        }
        return Constants.ERROR_GENERIC;
    }
}
