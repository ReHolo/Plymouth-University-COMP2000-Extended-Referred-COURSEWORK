package com.example.tennisbooking.entity;

public class Court {
    private int courtNo;
    private String courtType;
    private boolean isAvailable;
    private String availableSeason;

    // Getters and setters
    public int getCourtNo() {
        return courtNo;
    }

    public void setCourtNo(int courtNo) {
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