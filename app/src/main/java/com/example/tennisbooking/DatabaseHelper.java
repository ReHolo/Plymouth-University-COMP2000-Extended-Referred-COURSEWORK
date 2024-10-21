package com.example.tennisbooking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tennisbooking.entity.Booking;
import com.example.tennisbooking.entity.Court;
import com.google.gson.Gson;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tennisbooking.db";
    private static final int DATABASE_VERSION = 8;

    // Define table and column names
    private static final String TABLE_USERS = "User";
    private static final String TABLE_BOOKINGS = "Booking";
    private static final String TABLE_COURTS = "Court";

    private static final String COLUMN_ACCOUNT_NO = "accountNo";
    private static final String COLUMN_MEMBER_NAME = "memberName";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_HAS_BOOKING = "hasBooking";
    private static final String COLUMN_IS_LOGGED_IN = "isLoggedIn";

    private static final String COLUMN_BOOKING_NO = "bookingNo";
    private static final String COLUMN_ACCOUNT_NO_FK = "accountNo";
    private static final String COLUMN_COURT_NO = "courtNo";
    private static final String COLUMN_COURT_TYPE = "courtType";
    private static final String COLUMN_BOOKING_DATE = "bookingDate";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_EMAIL_BOOKING = "email";
    private static final String COLUMN_PHONE_BOOKING = "phone";
    private static final String COLUMN_DAY_OF_WEEK = "dayOfWeek";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create User, Booking, and Court tables
        db.execSQL("CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ACCOUNT_NO + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MEMBER_NAME + " TEXT NOT NULL UNIQUE,"
                + COLUMN_PASSWORD + " TEXT NOT NULL,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_HAS_BOOKING + " INTEGER DEFAULT 0,"
                + COLUMN_IS_LOGGED_IN + " INTEGER DEFAULT 0"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_BOOKINGS + "("
                + COLUMN_BOOKING_NO + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ACCOUNT_NO_FK + " INTEGER,"
                + COLUMN_COURT_NO + " TEXT,"
                + COLUMN_COURT_TYPE + " TEXT,"
                + COLUMN_BOOKING_DATE + " TEXT,"
                + COLUMN_DURATION + " TEXT,"
                + COLUMN_EMAIL_BOOKING + " TEXT,"
                + COLUMN_PHONE_BOOKING + " TEXT,"
                + COLUMN_DAY_OF_WEEK + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_ACCOUNT_NO_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ACCOUNT_NO + ")"
                + ")");

        db.execSQL("CREATE TABLE " + TABLE_COURTS + "("
                + "courtNo TEXT PRIMARY KEY,"
                + "courtType TEXT NOT NULL"
                + ")");

        insertInitialCourts(db);
    }

    private void insertInitialCourts(SQLiteDatabase db) {
        // Insert initial courts into the Court table
        insertCourt(db, "1", "Artificial Grass");
        insertCourt(db, "2", "Artificial Grass");
        insertCourt(db, "3", "Artificial Grass");
        insertCourt(db, "4", "Artificial Grass");
        insertCourt(db, "5", "Hard Court");
        insertCourt(db, "6", "Hard Court");
        insertCourt(db, "7", "Grass");
        insertCourt(db, "8", "Grass");
        insertCourt(db, "9", "Grass");
        insertCourt(db, "10", "Grass");
    }

    private void insertCourt(SQLiteDatabase db, String courtNo, String courtType) {
        ContentValues values = new ContentValues();
        values.put("courtNo", courtNo);
        values.put("courtType", courtType);
        db.insert("Court", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE " + TABLE_BOOKINGS + " ADD COLUMN " + COLUMN_COURT_NO + " TEXT");
        }

        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE " + TABLE_BOOKINGS + " ADD COLUMN " + COLUMN_EMAIL_BOOKING + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_BOOKINGS + " ADD COLUMN " + COLUMN_PHONE_BOOKING + " TEXT");
        }

        if (oldVersion < 9) {
            db.execSQL("ALTER TABLE " + TABLE_BOOKINGS + " ADD COLUMN " + COLUMN_DAY_OF_WEEK + " INTEGER");
        }
    }

    // Add user to the database
    public long addUser(String memberName, String password, String phone, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_MEMBER_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{memberName});

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return -1;
        }

        cursor.close();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEMBER_NAME, memberName);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_EMAIL, email);

        return db.insert(TABLE_USERS, null, values);
    }

    // Update user booking status
    public void updateUserBookingStatus(String accountNo, boolean hasBooking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HAS_BOOKING, hasBooking ? 1 : 0);

        db.update(TABLE_USERS, values, COLUMN_ACCOUNT_NO + " = ?", new String[]{String.valueOf(accountNo)});
    }

    // Update user login status
    public void updateLoginStatus(String memberName, boolean status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_LOGGED_IN, status ? 1 : 0);
        db.update(TABLE_USERS, values, COLUMN_MEMBER_NAME + " = ?", new String[]{memberName});
    }

    // Add booking to the API
    public long addBookingToApi(Booking booking) {
        Gson gson = new Gson();
        String jsonBooking = gson.toJson(booking);

        try {
            URL url = new URL("https://web.socem.plymouth.ac.uk/COMP2000/ReferralApi/api/Bookings/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBooking.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return 1;
            } else {
                return -1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Check user credentials
    public boolean checkUserCredentials(String memberName, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE memberName = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{memberName, password});

        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        if (cursor != null) {
            cursor.close();
        }
        return false;
    }

    // Get current logged-in user account number
    public String getCurrentUserAccountNo() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT accountNo FROM " + TABLE_USERS + " WHERE isLoggedIn = 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("accountNo");
            if (columnIndex != -1) {
                String accountNo = cursor.getString(columnIndex);
                cursor.close();
                return accountNo;
            }
            cursor.close();
        }
        return null;
    }

    // Get user details by member name
    public Cursor getUserDetails(String memberName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_MEMBER_NAME + " = ?";
        return db.rawQuery(query, new String[]{memberName});
    }

    // Add booking to local database
    public long addBooking(String accountNo, String courtNo, String courtType, String bookingDate, String duration, String email, String phone, int dayOfWeek) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (userHasBooking(accountNo)) {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put("accountNo", accountNo);
        values.put("courtNo", courtNo);
        values.put("courtType", courtType);
        values.put("bookingDate", bookingDate);
        values.put("duration", duration);
        values.put("email", email);
        values.put("phone", phone);
        values.put("dayOfWeek", dayOfWeek);

        long result = db.insert("Booking", null, values);

        if (result != -1) {
            updateUserBookingStatus(accountNo, true);
        }

        return result;
    }

    // Check if user already has a booking
    public boolean userHasBooking(String accountNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Booking WHERE accountNo = ?";
        Cursor cursor = db.rawQuery(query, new String[]{accountNo});

        boolean hasBooking = cursor.getCount() > 0;
        cursor.close();
        return hasBooking;
    }

    // Add booking using Booking object
    public long addBooking(Booking booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("accountNo", booking.getAccountNo());
        values.put("courtNo", booking.getCourtNo());
        values.put("courtType", booking.getCourtType());
        values.put("bookingDate", booking.getDate());
        values.put("duration", booking.getDuration());
        values.put("email", booking.getEmail());
        values.put("phone", booking.getPhoneNumber());
        values.put("dayOfWeek", booking.getDayOfWeek());

        return db.insert("Booking", null, values);
    }

    // Get all courts
    public Cursor getAllCourts() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_COURTS;
        return db.rawQuery(query, null);
    }
}
