package com.example.tennisbooking.entity;

public class Court {
    private String courtNo;
    private String courtType;
    private boolean isAvailable;
    private String availableSeason;

    public Court(String courtNo, String courtType, boolean isAvailable, String availableSeason) {
        this.courtNo = courtNo;
        this.courtType = courtType;
        this.isAvailable = isAvailable;
        this.availableSeason = availableSeason;
    }

    // Getters and setters
    public String getCourtNo() {
        return courtNo;
    }

    public void setCourtNo(String courtNo) {
        this.courtNo = courtNo;
    }

    public String getCourtType() {
        return courtType;
    }

    public void setCourtType(String courtType) {
        this.courtType = courtType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getAvailableSeason() {
        return availableSeason;
    }

    public void setAvailableSeason(String availableSeason) {
        this.availableSeason = availableSeason;
    }
}