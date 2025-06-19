package com.thesis.app.models;

public class NewsItem {
    private String id;
    private String title;
    private String description;
    private String imageResource; // drawable resource name
    private String category;
    private long createdDate;
    private String formattedDate;

    public NewsItem() {
    }

    public NewsItem(String id, String title, String description, String imageResource,
                    String category, long createdDate, String formattedDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageResource = imageResource;
        this.category = category;
        this.createdDate = createdDate;
        this.formattedDate = formattedDate;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageResource() { return imageResource; }
    public String getCategory() { return category; }
    public long getCreatedDate() { return createdDate; }
    public String getFormattedDate() { return formattedDate; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setImageResource(String imageResource) { this.imageResource = imageResource; }
    public void setCategory(String category) { this.category = category; }
    public void setCreatedDate(long createdDate) { this.createdDate = createdDate; }
    public void setFormattedDate(String formattedDate) { this.formattedDate = formattedDate; }
}
