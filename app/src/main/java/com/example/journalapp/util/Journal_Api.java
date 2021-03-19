package com.example.journalapp.util;

import android.app.Application;

public class Journal_Api extends Application {

    private String username;
    private String userId;
    private static Journal_Api instance;

    public static Journal_Api getInstance() {
        if (instance == null)
            instance = new Journal_Api();
        return instance;

    }

        public Journal_Api(){}

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

