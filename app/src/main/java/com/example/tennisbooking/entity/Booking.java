package com.example.tennisbooking.entity;

public class Booking {
    private int bookingNo;
    private String accountNo;
    private String courtType;
    private String courtNo;
    private String date;
    private String duration;
    private String email;
    private String phoneNumber;
    private String memberName;
    private int dayOfWeek;


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

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingNo=" + bookingNo +
                ", accountNo='" + accountNo + '\'' +
                ", courtType='" + courtType + '\'' +
                ", courtNo='" + courtNo + '\'' +
                ", date='" + date + '\'' +
                ", duration='" + duration + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", memberName='" + memberName + '\'' +
                ", dayOfWeek=" + dayOfWeek +
                '}';
    }
}