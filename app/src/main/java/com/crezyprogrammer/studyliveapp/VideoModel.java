package com.crezyprogrammer.studyliveapp;

public class VideoModel {
    String id,title,category;
    public VideoModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public VideoModel(String id, String title, String category) {
        this.id = id;
        this.title = title;
        this.category = category;
    }
}
