package com.example.blogapp.Model;

import java.util.Date;

public class Post extends postId{
    String image, caption, user, description;
    private Date time;

    public String getImage() {
        return image;
    }

    public String getCaption() {
        return caption;
    }

    public String getUser() {
        return user;
    }

    public Date getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }
}
