package com.example.tennisbooking.entity;

import com.example.tennisbooking.entity.Booking;

public class BookingManager {

    private static BookingManager instance;
    private Booking currentBooking;

    private BookingManager() {
        // 私有构造函数，确保外部无法直接实例化
    }

    public static BookingManager getInstance() {
        if (instance == null) {
            instance = new BookingManager();
        }
        return instance;
    }

    public void setCurrentBooking(Booking booking) {
        this.currentBooking = booking;
    }

    public Booking getCurrentBooking() {
        return currentBooking;
    }

    public boolean hasBooking() {
        return currentBooking != null;
    }
}
