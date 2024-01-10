package com.example.josechat.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phone;
    private String userName;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;

    public UserModel() {
    }

    public UserModel(String phone, String userName, Timestamp createdTimestamp, String userId) {
        this.phone = phone;
        this.userName = userName;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
