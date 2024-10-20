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

import com.example.tennisbooking.db.DatabaseHelper;

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
        btnCancelBooking.setOnClickListener(v -> showCancelConfirmationDialog(accountNo));
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
                        boolean isUpdated = databaseHelper.updateBooking(bookingNo, newEmail, newPhone);
                        if (isUpdated) {
                            tvEmail.setText(newEmail);
                            tvPhoneNumber.setText(newPhone);
                            Toast.makeText(ManageBookingActivity.this, "Booking updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ManageBookingActivity.this, "Failed to update booking", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ManageBookingActivity.this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    // Show a confirmation dialog to cancel booking
    private void showCancelConfirmationDialog(String accountNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean isDeleted = databaseHelper.deleteBookingByAccountNo(accountNo);
                    if (isDeleted) {
                        Toast.makeText(ManageBookingActivity.this, "Booking cancelled successfully", Toast.LENGTH_SHORT).show();
                        databaseHelper.updateUserBookingStatus(accountNo, false);  // Update user status to allow new booking
                        finish(); // Close activity after cancellation
                    } else {
                        Toast.makeText(ManageBookingActivity.this, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .create()
                .show();
    }
}
