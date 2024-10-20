package com.example.tennisbooking.entity;

import java.io.Serializable;

public class Booking implements Serializable {

    private int bookingNo;
    private String accountNo;
    private String memberName;
    private String courtType;
    private String courtNo;
    private String date;
    private int dayOfWeek;
    private String duration;
    private String availableSeason;
    private String email;
    private String phoneNumber;

    public Booking(int bookingNo,String accountNo, String memberName, String courtType, String courtNo, String email, String phoneNumber, String date, int dayOfWeek, String duration, String availableSeason) {
        this.bookingNo = bookingNo;
        this.accountNo = accountNo;
        this.memberName = memberName;
        this.courtType = courtType;
        this.courtNo = courtNo;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.duration = duration;
        this.availableSeason = availableSeason;
    }

    // Getters and Setters
    public int getBookingNo() {
        return bookingNo;
    }

    public void setBookingNo(int bookingNo) {
        this.bookingNo = bookingNo;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(int AccountNo) {
        this. accountNo= accountNo;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getCourtType() {
        return courtType;
    }

    public void setCourtType(String courtType) {
        this.courtType = courtType;
    }

    public String getCourtNo() {
        return courtNo;
    }

    public void setCourtNo(String courtNo) {
        this.courtNo = courtNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAvailableSeason() {
        return availableSeason;
    }

    public void setAvailableSeason(String availableSeason) {
        this.availableSeason = availableSeason;
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
}