// BookingActivity.java
package com.example.tennisbooking;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tennisbooking.TextWatcher.DateTimeTextWatcher;
import com.example.tennisbooking.db.DatabaseHelper;
import com.example.tennisbooking.entity.Booking;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String TAG = "BookingActivity";

    private EditText etEmail, etPhoneNumber, etBookingDate, etDuration, etMemberName;
    private TextView tvCourtDetails;
    private Button btnConfirmBooking;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        initializeUI();
        displayCourtDetails();
        setupListeners();
    }

    private void initializeUI() {
        tvCourtDetails = findViewById(R.id.courtDetails);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etBookingDate = findViewById(R.id.etBookingDate);
        etDuration = findViewById(R.id.etDuration);
        etMemberName = findViewById(R.id.etMemberName);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        findViewById(R.id.toolbar_booking).setOnClickListener(v -> finish());

        databaseHelper = new DatabaseHelper(this);
        etBookingDate.addTextChangedListener(new DateTimeTextWatcher(etBookingDate));
    }

    private void displayCourtDetails() {
        String courtNo = getIntent().getStringExtra("courtNo");
        String courtType = getIntent().getStringExtra("courtType");
        String availableSeason = getIntent().getStringExtra("availableSeason");

        tvCourtDetails.setText("Court No: " + courtNo + "\nCourt Type: " + courtType + "\nAvailable Season: " + availableSeason);
    }

    private void setupListeners() {
        btnConfirmBooking.setOnClickListener(v -> handleBooking());
    }

    private void handleBooking() {
        String email = etEmail.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();
        String bookingDate = etBookingDate.getText().toString().trim();
        String duration = etDuration.getText().toString().trim();
        String memberName = etMemberName.getText().toString().trim();

        if (!validateInput(email, phone, bookingDate, duration, memberName)) return;

        String accountNo = databaseHelper.getCurrentUserAccountNo();
        if (accountNo == null) {
            showToast("Failed to retrieve user account.");
            return;
        }

        if (databaseHelper.userHasBooking(Integer.parseInt(accountNo))) {
            showToast("You already have a booking. Please cancel it first.");
            return;
        }

        int dayOfWeek = getDayOfWeek(bookingDate);
        if (dayOfWeek == -1) {
            showToast("Invalid booking date.");
            return;
        }

        Booking booking = createBooking(accountNo, bookingDate, duration, email, phone, memberName, dayOfWeek);
        submitBooking(booking);
    }

    private boolean validateInput(String email, String phone, String bookingDate, String duration, String memberName) {
        if (!isValidEmail(email)) {
            showToast("Invalid email format");
            return false;
        }
        if (!isValidPhoneNumber(phone)) {
            showToast("Invalid phone number");
            return false;
        }
        if (email.isEmpty() || phone.isEmpty() || bookingDate.isEmpty() || duration.isEmpty() || memberName.isEmpty()) {
            showToast("Please fill in all fields");
            return false;
        }
        if (!isWithin48Hours(bookingDate)) {
            showToast("Booking time cannot exceed 48 hours from now.");
            return false;
        }
        return true;
    }

    private Booking createBooking(String accountNo, String Date, String duration, String email, String phoneNumber, String memberName, int dayOfWeek) {
        Booking booking = new Booking();
        booking.setBookingNo(0);
        booking.setAccountNo(accountNo);
        booking.setCourtNo(getIntent().getStringExtra("courtNo"));
        booking.setCourtType(getIntent().getStringExtra("courtType"));
        booking.setDate(Date);
        booking.setDuration(duration);
        booking.setEmail(email);
        booking.setPhoneNumber(phoneNumber);
        booking.setMemberName(memberName);
        booking.setDayOfWeek(dayOfWeek);
        Log.d(TAG, "Created booking: " + booking.toString());
        return booking;
    }

    private void submitBooking(Booking booking) {
        // Store booking data in the local database
        long result = databaseHelper.addBooking(booking);
        if (result == -1) {
            showToast("Failed to store booking data locally. Please try again.");
            return;
        }

        // Submit booking data to the API
        databaseHelper.addBookingToApi(booking, apiResult -> {
            if (apiResult != 1L) {
                showToast("Failed to book court. Please try again.");
            } else {
                showToast("Court booked successfully!");
                databaseHelper.updateUserBookingStatus(Integer.parseInt(booking.getAccountNo()), true);
                finish();
            }
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhoneNumber(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    private boolean isWithin48Hours(String bookingDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Calendar bookingTime = Calendar.getInstance();
            bookingTime.setTime(sdf.parse(bookingDate));
            Calendar currentTime = Calendar.getInstance();
            Calendar maxBookingTime = (Calendar) currentTime.clone();
            maxBookingTime.add(Calendar.HOUR, 48);

            Log.d(TAG, "Booking time: " + bookingTime.getTime() + ", Current time: " + currentTime.getTime() + ", Max booking time: " + maxBookingTime.getTime());

            return bookingTime.before(maxBookingTime) && bookingTime.after(currentTime);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getDayOfWeek(String bookingDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Calendar bookingTime = Calendar.getInstance();
            bookingTime.setTime(sdf.parse(bookingDate));
            return bookingTime.get(Calendar.DAY_OF_WEEK);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void showToast(String message) {
        Toast.makeText(BookingActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}