package com.example.tennisbooking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tennisbooking.TextWatcher.DateTimeTextWatcher;
import com.example.tennisbooking.db.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private EditText etEmail, etPhoneNumber, etBookingDate, etDuration;
    private TextView tvCourtDetails;
    private Button btnConfirmBooking;
    private DatabaseHelper databaseHelper;

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
            if (databaseHelper.userHasBooking(accountNo)) {
                Toast.makeText(BookingActivity.this, "You already have a booking. Please cancel it first.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 将预订信息插入到数据库
            long result = databaseHelper.addBooking(accountNo, courtNo, courtType, bookingDate, duration, email, phone);

            if (result == -1) {
                Toast.makeText(BookingActivity.this, "Failed to book court. Please try again.", Toast.LENGTH_SHORT).show();
            } else {
                // 预订成功提示
                Toast.makeText(BookingActivity.this, "Court booked successfully!", Toast.LENGTH_SHORT).show();

                // 更新用户预订状态
                databaseHelper.updateUserBookingStatus(Integer.parseInt(accountNo), true);

                // 完成后关闭活动
                finish();
            }
        });
    }

    // 校验邮箱格式
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // 校验电话号码格式
    private boolean isValidPhoneNumber(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    // 校验预订时间是否在未来48小时内
    private boolean isWithin48Hours(String bookingDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
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
}
