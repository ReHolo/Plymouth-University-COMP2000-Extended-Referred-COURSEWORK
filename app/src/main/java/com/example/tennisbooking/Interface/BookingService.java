package com.example.tennisbooking.Interface;

import com.example.tennisbooking.entity.Booking;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BookingService {

    // 获取所有预订
    @GET("Bookings")
    Call<List<Booking>> getAllBookings();

    // 创建新预订
    @POST("Bookings")
    Call<Booking> createBooking(@Body Booking booking);

    // 根据预订编号获取预订
    @GET("Bookings/{bookingNo}")
    Call<Booking> getBooking(@Path("bookingNo") int bookingNo);

    // 更新指定的预订
    @PUT("Bookings/{bookingNo}")
    Call<Booking> updateBooking(@Path("bookingNo") int bookingNo, @Body Booking booking);

    // 删除指定的预订
    @DELETE("Bookings/{bookingNo}")
    Call<Void> deleteBooking(@Path("bookingNo") int bookingNo);


}
