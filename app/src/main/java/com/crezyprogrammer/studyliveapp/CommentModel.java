package com.crezyprogrammer.studyliveapp;

public class CommentModel {
    String name,profile,text;

    public CommentModel() {
    }

    public CommentModel(String name, String profile, String text) {
        this.name = name;
        this.profile = profile;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
