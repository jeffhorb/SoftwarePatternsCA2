package com.ecom.softwarepatternsca2.ModelClasses;


public class Users {
    private String userName;
    private String userEmail;
    private String userId;
    private String userRole;

    // Default constructor (needed for Firestore deserialization)
    public Users() {
    }

    public Users(String userName, String userEmail, String userId,String userRole) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.userId = userId;
    }

    // Getters and setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

