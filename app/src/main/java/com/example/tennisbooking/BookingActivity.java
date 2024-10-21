package com.example.tennisbooking;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tennisbooking.TextWatcher.DateTimeTextWatcher;
import com.example.tennisbooking.db.DatabaseHelper;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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

        // Allow network operations on the main thread (not recommended for production)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Get court information passed from the previous activity
        String courtNo = getIntent().getStringExtra("courtNo");
        String courtType = getIntent().getStringExtra("courtType");
        String availableSeason = getIntent().getStringExtra("availableSeason");

        // Initialize UI components
        tvCourtDetails = findViewById(R.id.courtDetails);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etBookingDate = findViewById(R.id.etBookingDate);
        etDuration = findViewById(R.id.etDuration);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        findViewById(R.id.toolbar_booking).setOnClickListener(v -> finish());

        etBookingDate.addTextChangedListener(new DateTimeTextWatcher(etBookingDate));

        // Display court details
        tvCourtDetails.setText("Court No: " + courtNo + "\nCourt Type: " + courtType + "\nAvailable Season: " + availableSeason);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Set up booking button click event
        btnConfirmBooking.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String phone = etPhoneNumber.getText().toString().trim();
            String bookingDate = etBookingDate.getText().toString().trim();
            String duration = etDuration.getText().toString().trim();

            if (!isValidEmail(email)) {
                Toast.makeText(BookingActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
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

            // Validate booking time within 48 hours
            if (!isWithin48Hours(bookingDate)) {
                Toast.makeText(BookingActivity.this, "Booking time cannot exceed 48 hours from now.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get current user's account number
            String accountNo = databaseHelper.getCurrentUserAccountNo();
            if (accountNo == null) {
                Toast.makeText(BookingActivity.this, "Failed to retrieve user account.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if user already has a booking
            if (databaseHelper.userHasBooking(accountNo)) {
                Toast.makeText(BookingActivity.this, "You already have a booking. Please cancel it first.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert booking information into the database
            long result = databaseHelper.addBooking(accountNo, courtNo, courtType, bookingDate, duration, email, phone);

            if (result == -1) {
                Toast.makeText(BookingActivity.this, "Failed to book court. Please try again.", Toast.LENGTH_SHORT).show();
            } else {
                // Booking successful
                Toast.makeText(BookingActivity.this, "Court booked successfully!", Toast.LENGTH_SHORT).show();

                // Update user booking status
                databaseHelper.updateUserBookingStatus(Integer.parseInt(accountNo), true);

                // Send booking data to API
                sendBookingDataToApi(accountNo, courtNo, courtType, bookingDate, duration, email, phone);

                // Close activity after completion
                finish();
            }
        });
    }

    // Validate email format
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Validate phone number format
    private boolean isValidPhoneNumber(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    // Validate booking time within 48 hours
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

    // Send booking data to API
    private void sendBookingDataToApi(String accountNo, String courtNo, String courtType, String bookingDate, String duration, String email, String phone) {
        try {
            URL url = new URL("https://web.socem.plymouth.ac.uk/COMP2000/ReferralApi/api/Bookings/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = String.format(
                "{\"accountNo\": \"%s\", \"courtNo\": \"%s\", \"courtType\": \"%s\", \"bookingDate\": \"%s\", \"duration\": \"%s\", \"email\": \"%s\", \"phone\": \"%s\"}",
                accountNo, courtNo, courtType, bookingDate, duration, email, phone
            );

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                Toast.makeText(this, "Booking data sent to API successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to send booking data to API.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error sending booking data to API.", Toast.LENGTH_SHORT).show();
        }
    }
}