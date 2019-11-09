package com.crezyprogrammer.studyliveapp;

public class PostModel {
    String name, image, post_id, text, user_id, category;
    long time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public PostModel(String name, String image, String post_id, String text, String user_id, String category, long time) {
        this.name = name;
        this.image = image;
        this.post_id = post_id;
        this.text = text;
        this.user_id = user_id;
        this.category = category;
        this.time = time;
    }

    public PostModel() {
    }
}
