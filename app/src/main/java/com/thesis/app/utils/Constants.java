package com.thesis.app.utils;

public class Constants {
    // Firebase Database References
    public static final String USERS_NODE = "users";

    // News Categories
    public static final String CATEGORY_FACULTY = "faculty";
    public static final String CATEGORY_EVENTS = "events";
    public static final String CATEGORY_SPORTS = "sports";

    // App Info
    public static final String APP_NAME = "Thesis News";
    public static final String APP_VERSION = "1.0.0";

    // Default Images
    public static final String DEFAULT_PROFILE_IMAGE = "default";
    public static final int DEFAULT_PROFILE_ICON = android.R.drawable.ic_menu_myplaces; // Material icon placeholder

    // Request Codes
    public static final int PICK_IMAGE_REQUEST = 1001;
    public static final int PERMISSION_REQUEST_CODE = 1002;

    // SharedPreferences Keys
    public static final String PREF_NAME = "thesis_news_prefs";
    public static final String KEY_USER_UID = "user_uid";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";

    // Alert Messages
    public static final String SUCCESS_SIGNUP = "Account created successfully!";
    public static final String SUCCESS_LOGIN = "Welcome back!";
    public static final String SUCCESS_PROFILE_UPDATE = "Profile updated successfully!";
    public static final String SUCCESS_LOGOUT = "Logged out successfully!";
    public static final String SUCCESS_ACCOUNT_DELETED = "Account deleted successfully!";

    public static final String ERROR_INVALID_EMAIL = "Please enter a valid email address";
    public static final String ERROR_WEAK_PASSWORD = "Password must be at least 6 characters";
    public static final String ERROR_EMPTY_FIELDS = "Please fill all required fields";
    public static final String ERROR_NETWORK = "Please check your internet connection";
    public static final String ERROR_GENERIC = "Something went wrong. Please try again.";

    public static final String FEATURE_COMING_SOON = "This feature will be updated soon!";
}

