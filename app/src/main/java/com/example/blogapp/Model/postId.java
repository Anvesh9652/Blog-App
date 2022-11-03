package com.example.blogapp.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class postId {
    @Exclude
    public String postId;

    public <T extends postId> T withId(@NonNull final String id){
        this.postId = id;
        return (T) this;
    }
}
