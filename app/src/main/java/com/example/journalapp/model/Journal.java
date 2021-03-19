package com.example.journalapp.model;

import com.google.firebase.Timestamp;

public class Journal {
    private String username;
    private String userid;
    private String Imageurl;
    private String title;
    private String thought;
    private com.google.firebase.Timestamp timeAdded;

    public Journal(String username, String userid, String imageurl, String title, String thought, Timestamp timeAdded) {
        this.username = username;
        this.userid = userid;
        Imageurl = imageurl;
        this.title = title;
        this.thought = thought;
        this.timeAdded = timeAdded;
    }

    public Journal() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getImageurl() {
        return Imageurl;
    }

    public void setImageurl(String imageurl) {
        Imageurl = imageurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }

    public com.google.firebase.Timestamp getTimeAdded() {
        return timeAdded;
    }



    public void setTimeAdded(com.google.firebase.Timestamp timestamp) {
        this.timeAdded = timestamp;
    }
}
