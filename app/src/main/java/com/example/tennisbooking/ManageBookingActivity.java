package com.example.tennisbooking;

import static android.provider.Settings.System.DATE_FORMAT;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tennisbooking.db.DatabaseHelper;
import com.example.tennisbooking.entity.Booking;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ManageBookingActivity extends AppCompatActivity {

    private static final String TAG = "ManageBookingActivity";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

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

        Booking booking = databaseHelper.getBookingByAccountNo(Integer.parseInt(accountNo));
        if (booking != null) {
            bookingNo = String.valueOf(booking.getBookingNo());
            tvCourtType.setText(booking.getCourtType());
            tvCourtNo.setText(booking.getCourtNo());
            tvBookingDate.setText(booking.getDate());
            tvDuration.setText(booking.getDuration());
            tvEmail.setText(booking.getEmail());
            tvPhoneNumber.setText(booking.getPhoneNumber());
        } else {
            Toast.makeText(this, "Failed to fetch booking details", Toast.LENGTH_SHORT).show();
        }

        // Set up Update Booking button click event
        btnUpdateBooking.setOnClickListener(v -> showUpdateDialog());

        // Set up Cancel Booking button click event
        btnCancelBooking.setOnClickListener(v -> showCancelConfirmationDialog(accountNo));
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
                            sendUpdatedBookingDataToApi(bookingNo, newEmail, newPhone);
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
                    boolean isDeleted = databaseHelper.deleteBookingByBookingNo(bookingNo);
                    if (isDeleted) {
                        Toast.makeText(ManageBookingActivity.this, "Booking cancelled successfully", Toast.LENGTH_SHORT).show();
                        databaseHelper.updateUserBookingStatus(Integer.parseInt(accountNo), false);  // Update user status to allow new booking
                        sendCancelBookingDataToApi(bookingNo);
                        finish(); // Close activity after cancellation
                    } else {
                        Toast.makeText(ManageBookingActivity.this, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .create()
                .show();
    }

    // Send updated booking data to API
    private void sendUpdatedBookingDataToApi(String bookingNo, String newEmail, String newPhone) {
        new Thread(() -> {
            try {
                URL url = new URL("https://web.socem.plymouth.ac.uk/COMP2000/ReferralApi/api/Bookings/" + bookingNo);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                String jsonInputString = String.format(
                        "{\"email\": \"%s\", \"phone\": \"%s\"}",
                        newEmail, newPhone
                );

                Log.d(TAG, "Sending PUT request to URL: " + url);
                Log.d(TAG, "Payload: " + jsonInputString);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = conn.getResponseCode();
                Log.d(TAG, "Response Code: " + code);

                if (code == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "Booking data updated in API successfully!");
                    runOnUiThread(() -> Toast.makeText(this, "Booking data updated in API successfully!", Toast.LENGTH_SHORT).show());
                } else {
                    Log.e(TAG, "Failed to update booking data in API. Response Code: " + code);
                    runOnUiThread(() -> Toast.makeText(this, "Failed to update booking data in API.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error updating booking data in API", e);
                runOnUiThread(() -> Toast.makeText(this, "Error updating booking data in API.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // Send cancel booking data to API
    private void sendCancelBookingDataToApi(String bookingNo) {
        new Thread(() -> {
            try {
                URL url = new URL("https://web.socem.plymouth.ac.uk/COMP2000/ReferralApi/api/Bookings/" + bookingNo);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Accept", "application/json");

                Log.d(TAG, "Sending DELETE request to URL: " + url);

                int code = conn.getResponseCode();
                Log.d(TAG, "Response Code: " + code);

                if (code == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "Booking data deleted in API successfully!");
                    runOnUiThread(() -> Toast.makeText(this, "Booking data deleted in API successfully!", Toast.LENGTH_SHORT).show());
                } else {
                    Log.e(TAG, "Failed to delete booking data in API. Response Code: " + code);
                    runOnUiThread(() -> Toast.makeText(this, "Failed to delete booking data in API.", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error deleting booking data in API", e);
                runOnUiThread(() -> Toast.makeText(this, "Error deleting booking data in API.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}