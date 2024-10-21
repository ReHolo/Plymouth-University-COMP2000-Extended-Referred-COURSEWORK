package com.example.tennisbooking.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.example.tennisbooking.entity.Booking;
import com.example.tennisbooking.entity.Court;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tennisbooking.db";
    private static final int DATABASE_VERSION = 13;

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
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "("
                + COLUMN_ACCOUNT_NO + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MEMBER_NAME + " TEXT NOT NULL UNIQUE,"
                + COLUMN_PASSWORD + " TEXT NOT NULL,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_HAS_BOOKING + " INTEGER DEFAULT 0,"
                + COLUMN_IS_LOGGED_IN + " INTEGER DEFAULT 0"
                + ")");

        // 创建预订表
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BOOKINGS + "("
                + COLUMN_BOOKING_NO + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ACCOUNT_NO_FK + " INTEGER,"
                + COLUMN_COURT_NO + " TEXT,"
                + COLUMN_COURT_TYPE + " TEXT,"
                + COLUMN_BOOKING_DATE + " TEXT,"
                + COLUMN_DURATION + " TEXT,"
                + COLUMN_EMAIL_BOOKING + " TEXT,"
                + COLUMN_PHONE_BOOKING + " TEXT,"
                + COLUMN_DAY_OF_WEEK + " INTEGER,"
                + COLUMN_MEMBER_NAME + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_ACCOUNT_NO_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ACCOUNT_NO + ")"
                + ")");
        // 创建球场表
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_COURTS + "("
                + "courtNo TEXT PRIMARY KEY,"
                + "courtType TEXT NOT NULL"
                + ")");



        // 更新触发器：同步更新 memberName
        db.execSQL("CREATE TRIGGER after_user_update "
                + "AFTER UPDATE ON " + TABLE_USERS + " "
                + "FOR EACH ROW "
                + "BEGIN "
                + "UPDATE " + TABLE_BOOKINGS + " SET " + COLUMN_MEMBER_NAME + " = NEW." + COLUMN_MEMBER_NAME + " "
                + "WHERE " + COLUMN_ACCOUNT_NO_FK + " = OLD." + COLUMN_ACCOUNT_NO + "; "
                + "END;");

        // 删除触发器：同步删除 memberName
        db.execSQL("CREATE TRIGGER after_user_delete "
                + "AFTER DELETE ON " + TABLE_USERS + " "
                + "FOR EACH ROW "
                + "BEGIN "
                + "UPDATE " + TABLE_BOOKINGS + " SET " + COLUMN_MEMBER_NAME + " = NULL "
                + "WHERE " + COLUMN_ACCOUNT_NO_FK + " = OLD." + COLUMN_ACCOUNT_NO + "; "
                + "END;");

        insertInitialCourts(db);
    }

    private void insertInitialCourts(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        // Four artificial grass courts
        for (int i = 1; i <= 4; i++) {
            values.put("courtNo", "ArtificialGrass-" + i);
            values.put("courtType", "Artificial Grass");
            db.insert("Court", null, values);
        }

        // Two hard courts
        for (int i = 1; i <= 2; i++) {
            values.put("courtNo", "Hard-" + i);
            values.put("courtType", "Hard");
            db.insert("Court", null, values);
        }

        // Four grass courts
        for (int i = 1; i <= 4; i++) {
            values.put("courtNo", "Grass-" + i);
            values.put("courtType", "Grass");
            db.insert("Court", null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        // 可以添加更多表的删除语句（如果有其他表格）

        // 重新创建所有表格
        onCreate(db);


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
    public void updateUserBookingStatus(int accountNo, boolean hasBooking) {
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




    public Booking getBookingByAccountNo(int accountNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BOOKINGS + " WHERE " + COLUMN_ACCOUNT_NO_FK + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(accountNo)});

        if (cursor != null && cursor.moveToFirst()) {
            int bookingNo = cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKING_NO));
            String courtNo = cursor.getString(cursor.getColumnIndex(COLUMN_COURT_NO));
            String courtType = cursor.getString(cursor.getColumnIndex(COLUMN_COURT_TYPE));
            String bookingDate = cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_DATE));
            String duration = cursor.getString(cursor.getColumnIndex(COLUMN_DURATION));
            String email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL_BOOKING));
            String phone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_BOOKING));
            String memberName = cursor.getString(cursor.getColumnIndex(COLUMN_MEMBER_NAME));
            int dayOfWeek = cursor.getInt(cursor.getColumnIndex(COLUMN_DAY_OF_WEEK));

            cursor.close();
            return new Booking(bookingNo, String.valueOf(accountNo), courtNo, courtType, bookingDate, duration, email, phone, memberName, dayOfWeek);
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }


    private static class AddBookingTask extends AsyncTask<Booking, Void, Long> {
        private BookingCallback callback;

        AddBookingTask(BookingCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Long doInBackground(Booking... bookings) {
            Booking booking = bookings[0];
            Gson gson = new Gson();
            String jsonBooking = gson.toJson(booking);
            Log.d("BookingActivity", "JSON to be sent: " + jsonBooking);

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
                Log.d("BookingActivity", "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return 1L;
                } else {
                    InputStream errorStream = conn.getErrorStream();
                    if (errorStream != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream, "utf-8"));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line.trim());
                        }
                        Log.e("BookingActivity", "Error Response: " + response.toString());
                    }
                    return -1L;
                }

            } catch (Exception e) {
                Log.e("BookingActivity", "Exception: " + e.getMessage(), e);
                return -1L;
            }
        }

        @Override
        protected void onPostExecute(Long result) {
            if (callback != null) {
                callback.onBookingResult(result);
            }
        }
    }

    public interface BookingCallback {
        void onBookingResult(long result);
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

    // Check if member name exists
    public boolean checkMemberNameExists(String memberName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_MEMBER_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{memberName});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Register user
    public boolean registerUser(String memberName, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEMBER_NAME, memberName);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean updateBooking(String bookingNo, String newEmail, String newPhone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", newEmail);
        values.put("phone", newPhone);

        int rowsAffected = db.update("Booking", values, "bookingNo = ?", new String[]{bookingNo});
        return rowsAffected > 0;
    }

    public boolean userHasBooking(int accountNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_HAS_BOOKING + " FROM " + TABLE_USERS + " WHERE " + COLUMN_ACCOUNT_NO + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(accountNo)});

        if (cursor != null && cursor.moveToFirst()) {
            int hasBooking = cursor.getInt(cursor.getColumnIndex(COLUMN_HAS_BOOKING));
            cursor.close();
            return hasBooking == 1;
        }
        if (cursor != null) {
            cursor.close();
        }
        return false;
    }


    public boolean deleteBookingByBookingNo(String bookingNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_BOOKINGS, COLUMN_BOOKING_NO + " = ?", new String[]{bookingNo});
        return rowsDeleted > 0;
    }



    public Cursor getBookingsByUser(int accountNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Booking WHERE accountNo = ?";
        return db.rawQuery(query, new String[]{String.valueOf(accountNo)});
    }

    public List<Court> getAllCourts() {
    List<Court> courts = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.query("Court", null, null, null, null, null, null);

    if (cursor != null && cursor.moveToFirst()) {
        do {
            int courtNoIndex = cursor.getColumnIndex("courtNo");
            int courtTypeIndex = cursor.getColumnIndex("courtType");
            if (courtNoIndex != -1 && courtTypeIndex != -1) {
                String courtNo = cursor.getString(courtNoIndex);
                String courtType = cursor.getString(courtTypeIndex);
                boolean isAvailable = true; // Assuming availability is true for simplicity
                String availableSeason = "All Year"; // Assuming available season is "All Year" for simplicity
                Court court = new Court(courtNo, courtType, isAvailable, availableSeason);
                courts.add(court);
            }
        } while (cursor.moveToNext());
        cursor.close();
    }
    return courts;
}

    public long addBooking(Booking booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ACCOUNT_NO, booking.getAccountNo());
            values.put(COLUMN_COURT_NO, booking.getCourtNo());
            values.put(COLUMN_COURT_TYPE, booking.getCourtType());
            values.put(COLUMN_BOOKING_DATE, booking.getDate());
            values.put(COLUMN_DURATION, booking.getDuration());
            values.put(COLUMN_EMAIL, booking.getEmail());
            values.put(COLUMN_PHONE, booking.getPhoneNumber());

            // 如果 memberName 不为 null，则添加它
            if (booking.getMemberName() != null) {
                values.put(COLUMN_MEMBER_NAME, booking.getMemberName());
            }

            values.put(COLUMN_DAY_OF_WEEK, booking.getDayOfWeek());

            // 执行插入操作
            result = db.insert(TABLE_BOOKINGS, null, values);

            if (result != -1) {
                db.setTransactionSuccessful();
            } else {
                Log.e("Database Error", "Booking insertion failed.");
            }
        } catch (Exception e) {
            Log.e("Database Error", "Error while inserting booking: " + e.getMessage());
        } finally {
            db.endTransaction();
            db.close();
        }

        return result;
    }


}