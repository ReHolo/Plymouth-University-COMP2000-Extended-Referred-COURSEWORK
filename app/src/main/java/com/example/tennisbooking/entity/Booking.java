package com.example.tennisbooking.entity;

public class Booking {
    private int bookingNo;
    private String accountNo;
    private String courtNo;
    private String courtType;
    private String date;
    private String duration;
    private String email;
    private String phoneNumber;
    private String memberName;
    private int dayOfWeek;

    // 构造函数
    public Booking(int bookingNo, String accountNo, String courtNo, String courtType, String date, String duration, String email, String phoneNumber, String memberName, int dayOfWeek) {
        this.bookingNo = bookingNo;
        this.accountNo = accountNo;
        this.courtNo = courtNo;
        this.courtType = courtType;
        this.date = date;
        this.duration = duration;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.memberName = memberName;
        this.dayOfWeek = dayOfWeek;
    }

    // getter 和 setter 方法
    public int getBookingNo() { return bookingNo; }
    public String getAccountNo() { return accountNo; }
    public String getCourtNo() { return courtNo; }
    public String getCourtType() { return courtType; }
    public String getDate() { return date; }
    public String getDuration() { return duration; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getMemberName() { return memberName; }
    public int getDayOfWeek() { return dayOfWeek; }

    public void setBookingNo(int bookingNo) { this.bookingNo = bookingNo; }
    public void setAccountNo(String accountNo) { this.accountNo = accountNo; }
    public void setCourtNo(String courtNo) { this.courtNo = courtNo; }
    public void setCourtType(String courtType) { this.courtType = courtType; }
    public void setDate(String date) { this.date = date; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public void setDayOfWeek(int dayOfWeek) { this.dayOfWeek = dayOfWeek; }
}
