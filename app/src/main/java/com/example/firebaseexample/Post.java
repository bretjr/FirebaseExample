package com.example.firebaseexample;

public class Post {

    // Member Variables
    private String title, content;

    // Constructors
    public Post() { }

    Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // Getters and Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
