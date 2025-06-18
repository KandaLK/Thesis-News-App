package com.thesis.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_LOGIN_TIME = "loginTime";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Save user login session
     */
    public void createLoginSession(String userId, String username, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get user details from session
     */
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, "");
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "User");
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "user@example.com");
    }

    public long getLoginTime() {
        return sharedPreferences.getLong(KEY_LOGIN_TIME, 0);
    }

    /**
     * Check if session is still valid (24 hours)
     */
    public boolean isSessionValid() {
        if (!isLoggedIn()) return false;

        long loginTime = getLoginTime();
        long currentTime = System.currentTimeMillis();
        long sessionDuration = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

        return (currentTime - loginTime) < sessionDuration;
    }

    /**
     * Clear session data (logout)
     */
    public void logoutUser() {
        editor.clear();
        editor.apply();
    }

    /**
     * Update user information
     */
    public void updateUserInfo(String username, String email) {
        if (isLoggedIn()) {
            editor.putString(KEY_USERNAME, username);
            editor.putString(KEY_USER_EMAIL, email);
            editor.apply();
        }
    }
}
