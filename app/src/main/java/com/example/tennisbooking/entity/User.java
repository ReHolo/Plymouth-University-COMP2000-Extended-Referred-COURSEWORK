package com.example.tennisbooking.entity;

import java.io.Serializable;

public class User implements Serializable {

    private int userId;
    private String memberName;
    private String password;
    private String accountNo;
    private String email;
    private String phoneNumber;
    private boolean isLoggedIn;
    private String lastLoginTime;

    public User(int userId, String memberName, String password, String accountNo, String email, String phoneNumber, boolean isLoggedIn, String lastLoginTime) {
        this.userId = userId;
        this.memberName = memberName;
        this.password = password;
        this.accountNo = accountNo;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isLoggedIn = isLoggedIn;
        this.lastLoginTime = lastLoginTime;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
}
