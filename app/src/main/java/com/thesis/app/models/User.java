package com.thesis.app.models;

public class User {
    private String uid;
    private String username;
    private String email;
    private String profileImageUrl;
    private long createdAt;
    private long updatedAt;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String uid, String username, String email) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.profileImageUrl = "default"; // Placeholder for default image
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    // Setters
    public void setUid(String uid) { this.uid = uid; }
    public void setUsername(String username) {
        this.username = username;
        this.updatedAt = System.currentTimeMillis();
    }
    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = System.currentTimeMillis();
    }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        this.updatedAt = System.currentTimeMillis();
    }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    // Helper method to check if user has custom profile image
    public boolean hasCustomProfileImage() {
        return profileImageUrl != null && !profileImageUrl.equals("default") && !profileImageUrl.isEmpty();
    }
}
