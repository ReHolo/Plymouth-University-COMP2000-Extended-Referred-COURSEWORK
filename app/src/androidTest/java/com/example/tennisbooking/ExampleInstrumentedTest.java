package com.example.tennisbooking;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.tennisbooking.Interface.BookingService;
import com.example.tennisbooking.entity.Booking;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.tennisbooking", appContext.getPackageName());
    }
    public void testPostBookingAPI() {
        // 使用 Retrofit 初始化
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://web.socem.plymouth.ac.uk/COMP2000/ReferralApi/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BookingService apiService = retrofit.create(BookingService.class);

        // 创建一个新的 Booking 对象作为测试数据
        Booking booking = new Booking(1, "12345", "Test User", "Hard Court", "Court 1", "test@example.com", "1234567890", "2024-10-21", 0, "2 hours", "Open all year");

        // 调用 API 的 createBooking 方法
        Call<Booking> call = apiService.createBooking(booking);
        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (response.isSuccessful()) {
                    Log.d("API_TEST", "POST请求成功: " + response.body());
                } else {
                    Log.d("API_TEST", "POST请求失败: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Log.e("API_TEST", "POST请求错误: " + t.getMessage());
            }
        });
    }
}