package com.example.tennisbooking.Interface;

import com.example.tennisbooking.entity.Booking;
import com.example.tennisbooking.entity.WeatherForecast;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BookingService {

    @GET("#/components/schemas/Booking")
    Call<List<Booking>> getAllBookings();

    @POST("#/components/schemas/Booking")
    Call<Booking> createBooking(@Body Booking booking);

    @GET("#/components/schemas/Booking/{bookingNo}")
    Call<Booking> getBooking();

    @PUT("#/components/schemas/Booking/{bookingNo}")
    Call<Booking> updateBooking(@Path("bookingNo") int bookingNo, @Body Booking booking);

    @DELETE("#/components/schemas/Booking/{bookingNo}")
    Call<Void> deleteBooking(@Path("bookingNo") int bookingNo);

    @GET("#/components/schemas/Booking/WeatherForecast")
    Call<List<WeatherForecast>> getWeatherForecast();





}