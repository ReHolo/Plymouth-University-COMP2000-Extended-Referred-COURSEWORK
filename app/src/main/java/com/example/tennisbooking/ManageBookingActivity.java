package com.example.tennisbooking;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tennisbooking.Interface.BookingService;
import com.example.tennisbooking.db.DatabaseHelper;
import com.example.tennisbooking.entity.Booking;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ManageBookingActivity extends AppCompatActivity {

    private TextView tvCourtType, tvCourtNo, tvBookingDate, tvDuration, tvEmail, tvPhoneNumber;
    private Button btnUpdateBooking, btnCancelBooking;
    private DatabaseHelper databaseHelper;
    private String bookingNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_booking);

        // Initialize UI components
        tvCourtType = findViewById(R.id.tvCourtType);
        tvCourtNo = findViewById(R.id.tvCourtNo);
        tvBookingDate = findViewById(R.id.tvBookingDate);
        tvDuration = findViewById(R.id.tvDuration);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        btnUpdateBooking = findViewById(R.id.btnUpdateBooking);
        btnCancelBooking = findViewById(R.id.btnCancelBooking);

        findViewById(R.id.toolbar_booking_management).setOnClickListener(v -> finish());

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Get the current user's account number
        String accountNo = databaseHelper.getCurrentUserAccountNo();
        if (accountNo != null) {
            loadBookingDetails(accountNo);  // Load booking details if user has an account
        } else {
            Toast.makeText(this, "No logged-in user found.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set up Update Booking button click event
        btnUpdateBooking.setOnClickListener(v -> showUpdateDialog());

        // Set up Cancel Booking button click event
        btnCancelBooking.setOnClickListener(v -> showCancelConfirmationDialog());
    }

    // Load booking details from the database
    private void loadBookingDetails(String accountNo) {
        Cursor cursor = databaseHelper.getBookingsByUser(Integer.parseInt(accountNo));

        if (cursor != null && cursor.moveToFirst()) {
            int bookingNoIndex = cursor.getColumnIndex("bookingNo");
            int courtTypeIndex = cursor.getColumnIndex("courtType");
            int courtNoIndex = cursor.getColumnIndex("courtNo");
            int bookingDateIndex = cursor.getColumnIndex("bookingDate");
            int durationIndex = cursor.getColumnIndex("duration");
            int emailIndex = cursor.getColumnIndex("email");
            int phoneIndex = cursor.getColumnIndex("phone");

            if (bookingNoIndex != -1) {
                // Load booking details
                bookingNo = cursor.getString(bookingNoIndex);
                String courtType = cursor.getString(courtTypeIndex);
                String courtNo = cursor.getString(courtNoIndex);
                String bookingDate = cursor.getString(bookingDateIndex);
                String duration = cursor.getString(durationIndex);
                String email = cursor.getString(emailIndex);
                String phone = cursor.getString(phoneIndex);

                // Display details in the UI
                tvCourtType.setText("Court Type: " + courtType);
                tvCourtNo.setText("Court No: " + courtNo);
                tvBookingDate.setText("Booking Date: " + bookingDate);
                tvDuration.setText("Duration: " + duration);
                tvEmail.setText("Email: " + email);
                tvPhoneNumber.setText("Phone Number: " + phone);
            }
            cursor.close();
        } else {
            Toast.makeText(this, "No booking found", Toast.LENGTH_SHORT).show();
        }
    }

    // Show a dialog to update email and phone number
    private void showUpdateDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_booking, null);
        EditText etNewEmail = dialogView.findViewById(R.id.etNewEmail);
        EditText etNewPhoneNumber = dialogView.findViewById(R.id.etNewPhoneNumber);

        // Pre-fill with current values
        etNewEmail.setText(tvEmail.getText().toString());
        etNewPhoneNumber.setText(tvPhoneNumber.getText().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Booking")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newEmail = etNewEmail.getText().toString().trim();
                    String newPhone = etNewPhoneNumber.getText().toString().trim();

                    if (!TextUtils.isEmpty(newEmail) && !TextUtils.isEmpty(newPhone)) {
                        // 优先更新本地数据库
                        boolean isUpdated = databaseHelper.updateBooking(bookingNo, newEmail, newPhone);
                        if (isUpdated) {
                            tvEmail.setText(newEmail);
                            tvPhoneNumber.setText(newPhone);
                            Toast.makeText(ManageBookingActivity.this, "Booking updated locally!", Toast.LENGTH_SHORT).show();
                            // 然后调用 API 更新
                            updateBookingApi(newEmail, newPhone);
                        } else {
                            Toast.makeText(ManageBookingActivity.this, "Failed to update booking locally.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ManageBookingActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    // Update booking using API
    private void updateBookingApi(String newEmail, String newPhone) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://web.socem.plymouth.ac.uk/COMP2000/ReferralApi/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BookingService apiService = retrofit.create(BookingService.class);

        // Create a new Booking object with updated fields
        Booking booking = new Booking(Integer.parseInt(bookingNo), null, null, tvCourtType.getText().toString(), tvCourtNo.getText().toString(), newEmail, newPhone, tvBookingDate.getText().toString(), 0, tvDuration.getText().toString(), null);

        Call<Booking> call = apiService.updateBooking(Integer.parseInt(bookingNo), booking);
        call.enqueue(new Callback<Booking>() {
            @Override
            public void onResponse(Call<Booking> call, Response<Booking> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageBookingActivity.this, "Booking updated successfully on server!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManageBookingActivity.this, "Failed to update booking on server.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Booking> call, Throwable t) {
                Toast.makeText(ManageBookingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show a confirmation dialog to cancel booking
    private void showCancelConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // 优先删除本地数据库中的预订信息
                    boolean isDeleted = databaseHelper.deleteBooking(Integer.parseInt(bookingNo));
                    if (isDeleted) {
                        Toast.makeText(ManageBookingActivity.this, "Booking cancelled locally!", Toast.LENGTH_SHORT).show();
                        databaseHelper.updateUserBookingStatus(Integer.parseInt(bookingNo), false);  // 更新用户状态，允许新的预订
                        deleteBookingApi(); // 然后调用 API 删除预订
                    } else {
                        Toast.makeText(ManageBookingActivity.this, "Failed to cancel booking locally.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .create()
                .show();
    }

    // Delete booking using API
    private void deleteBookingApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://web.socem.plymouth.ac.uk/COMP2000/ReferralApi/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BookingService apiService = retrofit.create(BookingService.class);

        Call<Void> call = apiService.deleteBooking(Integer.parseInt(bookingNo));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ManageBookingActivity.this, "Booking cancelled successfully on server!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after successful cancellation
                } else {
                    Toast.makeText(ManageBookingActivity.this, "Failed to cancel booking on server.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ManageBookingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}