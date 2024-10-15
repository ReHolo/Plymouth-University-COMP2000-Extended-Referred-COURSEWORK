package com.example.tennisbooking.API;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService<Court> {

    @GET("Bookings")
    Call<List<Court>> getCourts();

    @POST("Bookings")
    Call<Void> bookCourt(@Body BookingRequest bookingRequest);

    // 其他API调用
}
