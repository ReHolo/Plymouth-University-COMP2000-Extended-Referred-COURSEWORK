package com.example.tennisbooking;

import android.accessibilityservice.TouchInteractionController;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tennisbooking.Interface.BookingService;
import com.example.tennisbooking.TextWatcher.DateTimeTextWatcher;
import com.example.tennisbooking.db.DatabaseHelper;
import com.example.tennisbooking.entity.Booking;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BookingActivity extends AppCompatActivity {

    public TouchInteractionController btn;
    private EditText etEmail, etPhoneNumber, etBookingDate, etDuration;
    private TextView tvCourtDetails;
    Button btnConfirmBooking;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // 获取传递过来的球场信息
        String courtNo = getIntent().getStringExtra("courtNo");
        String courtType = getIntent().getStringExtra("courtType");
        String availableSeason = getIntent().getStringExtra("availableSeason");

        // 初始化UI组件
        tvCourtDetails = findViewById(R.id.courtDetails);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etBookingDate = findViewById(R.id.etBookingDate);
        etDuration = findViewById(R.id.etDuration);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        findViewById(R.id.toolbar_booking).setOnClickListener(v -> finish());

        // 显示球场详细信息
        tvCourtDetails.setText("Court No: " + courtNo + "\nCourt Type: " + courtType + "\nAvailable Season: " + availableSeason);

        // 初始化 DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // 使用 DateTimeTextWatcher 格式化日期输入
        etBookingDate.addTextChangedListener(new DateTimeTextWatcher(etBookingDate));

        // 设置预订按钮点击事件
        btnConfirmBooking.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String phone = etPhoneNumber.getText().toString().trim();
            String bookingDate = etBookingDate.getText().toString().trim();
            String duration = etDuration.getText().toString().trim();

            // 校验输入内容
            if (!isValidEmail(email)) {
                Toast.makeText(BookingActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isValidPhoneNumber(phone)) {
                Toast.makeText(BookingActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty() || phone.isEmpty() || bookingDate.isEmpty() || duration.isEmpty()) {
                Toast.makeText(BookingActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // 校验预订时间是否超过48小时
            if (!isWithin48Hours(bookingDate)) {
                Toast.makeText(BookingActivity.this, "Booking time cannot exceed 48 hours from now.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 获取当前登录用户的 accountNo
            String accountNo = databaseHelper.getCurrentUserAccountNo();
            if (accountNo == null) {
                Toast.makeText(BookingActivity.this, "Failed to retrieve user account.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 检查用户是否已有预订
            if (databaseHelper.userHasBooking(Integer.parseInt(accountNo))) {
                Toast.makeText(BookingActivity.this, "You already have a booking. Please cancel it first.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 将预订信息插入到数据库
            String memberName = databaseHelper.getUserDetails(accountNo).toString();
            Booking booking = new Booking(0, accountNo, courtNo, courtType, bookingDate, duration, email, phone, memberName, 0);
            long result = databaseHelper.addBooking(booking);

            if (result == -1) {
                Toast.makeText(BookingActivity.this, "Failed to book court. Please try again.", Toast.LENGTH_SHORT).show();
            } else {
                // 预订成功提示
                Toast.makeText(BookingActivity.this, "Court booked successfully!", Toast.LENGTH_SHORT).show();

                // 更新用户预订状态
                databaseHelper.updateUserBookingStatus(Integer.parseInt(accountNo), true);

                uploadBookingToApi((int) result, accountNo, courtNo, courtType, bookingDate, duration, email, phone);

                // 完成后关闭活动
                finish();
            }
        });

    }

    // 校验邮箱格式
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // 校验电话号码格式
    private boolean isValidPhoneNumber(String phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }

    // 校验预订时间是否在未来48小时内
    private boolean isWithin48Hours(String bookingDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
            Calendar bookingTime = Calendar.getInstance();
            bookingTime.setTime(sdf.parse(bookingDate));
            Calendar currentTime = Calendar.getInstance();
            currentTime.add(Calendar.HOUR, 48);

            return bookingTime.before(currentTime);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getDayOfWeek(String bookingDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(bookingDate));
            // Calendar.SUNDAY = 1, 我们将其调整为0-6表示周日到周六
            return calendar.get(Calendar.DAY_OF_WEEK) - 1;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // 表示解析失败
        }
    }

    private void uploadBookingToApi(int bookingNo, String accountNo, String courtNo, String courtType, String bookingDate, String duration, String email, String phone) {
        // 创建 Booking 对象
        int dayOfWeek = getDayOfWeek(bookingDate);
        String memberName = "";
        Booking booking = new Booking(bookingNo, accountNo, courtNo, courtType, bookingDate, duration, email, phone, memberName, 0);

        // 使用 Retrofit 初始化
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://web.socem.plymouth.ac.uk/COMP2000/ReferralApi/api/") // 替换为您的API基本URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BookingService apiService = retrofit.create(BookingService.class);
        Call<Booking> call = apiService.createBooking(booking);
        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(@NonNull Call<Booking> call, @NonNull Response<Booking> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(BookingActivity.this, "Booking uploaded to server successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BookingActivity.this, "Failed to upload booking to server. Status Code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(BookingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}