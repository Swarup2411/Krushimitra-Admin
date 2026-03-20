package com.mountrich.krushimitraadminapp.model;

public class User {

    String userId;
    String name;
    String email;
    String mobile;
    String profileImage;

    public User() {}

    public User(String userId, String name, String email, String mobile, String profileImage) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.profileImage = profileImage;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public String getProfileImage() { return profileImage; }
}