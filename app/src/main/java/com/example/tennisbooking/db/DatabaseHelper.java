package com.example.tennisbooking.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tennisbooking.entity.Booking;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tennisbooking.db";
    private static final int DATABASE_VERSION = 7;  // 更新版本号

    // 定义表名和列名
    private static final String TABLE_USERS = "User";
    private static final String TABLE_BOOKINGS = "Booking";

    private static final String COLUMN_ACCOUNT_NO = "accountNo";
    private static final String COLUMN_MEMBER_NAME = "memberName";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_HAS_BOOKING = "hasBooking";

    private static final String COLUMN_BOOKING_NO = "bookingNo";
    private static final String COLUMN_ACCOUNT_NO_FK = "accountNo";  // 外键
    private static final String COLUMN_COURT_NO = "courtNo";  // courtNo 列
    private static final String COLUMN_COURT_TYPE = "courtType";
    private static final String COLUMN_BOOKING_DATE = "bookingDate";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_EMAIL_BOOKING = "email";  // Booking 表中的 email 列
    private static final String COLUMN_PHONE_BOOKING = "phone";  // Booking 表中的 phone 列

    // SQL statements to create tables
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ACCOUNT_NO + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_MEMBER_NAME + " TEXT NOT NULL UNIQUE,"
            + COLUMN_PASSWORD + " TEXT NOT NULL,"
            + COLUMN_PHONE + " TEXT,"
            + COLUMN_EMAIL + " TEXT,"
            + COLUMN_HAS_BOOKING + " INTEGER DEFAULT 0,"
            + "isLoggedIn INTEGER DEFAULT 0"
            + ")";

    private static final String CREATE_TABLE_BOOKINGS = "CREATE TABLE " + TABLE_BOOKINGS + "("
            + COLUMN_BOOKING_NO + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ACCOUNT_NO_FK + " INTEGER,"
            + COLUMN_COURT_NO + " TEXT,"  // 添加 courtNo 列
            + COLUMN_COURT_TYPE + " TEXT,"
            + COLUMN_BOOKING_DATE + " TEXT,"
            + COLUMN_DURATION + " TEXT,"
            + COLUMN_EMAIL_BOOKING + " TEXT,"
            + COLUMN_PHONE_BOOKING + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_ACCOUNT_NO_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ACCOUNT_NO + ")"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);  // 创建 Users 表
        db.execSQL(CREATE_TABLE_BOOKINGS);  // 创建 Bookings 表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 6) {
            // 如果版本小于 6，添加 courtNo 列
            db.execSQL("ALTER TABLE " + TABLE_BOOKINGS + " ADD COLUMN " + COLUMN_COURT_NO + " TEXT");
        }

        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE " + TABLE_BOOKINGS + " ADD COLUMN " + COLUMN_EMAIL_BOOKING + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_BOOKINGS + " ADD COLUMN " + COLUMN_PHONE_BOOKING + " TEXT");
        }
    }
    // 插入用户数据
    public long addUser(String memberName, String password, String phone, String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 查询是否已有相同 memberName 的用户
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_MEMBER_NAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{memberName});

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return -1;  // 表示用户已存在，插入失败
        }

        cursor.close();

        // 用户不存在，插入新用户
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEMBER_NAME, memberName);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_EMAIL, email);

        return db.insert(TABLE_USERS, null, values);
    }



    // 更新用户的预订状态
    public void updateUserBookingStatus(int accountNo, boolean hasBooking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HAS_BOOKING, hasBooking ? 1 : 0);

        db.update(TABLE_USERS, values, COLUMN_ACCOUNT_NO + " = ?", new String[]{String.valueOf(accountNo)});
    }

    // 查询所有用户
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS;
        return db.rawQuery(query, null);
    }

    // 根据用户查询预订数据
    public Cursor getBookingsByUser(int accountNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BOOKINGS + " WHERE " + COLUMN_ACCOUNT_NO_FK + "=?";
        return db.rawQuery(query, new String[]{String.valueOf(accountNo)});
    }

    // 更新用户信息
    public int updateUser(int accountNo, String memberName, String phone, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEMBER_NAME, memberName);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_EMAIL, email);

        return db.update(TABLE_USERS, values, COLUMN_ACCOUNT_NO + "=?", new String[]{String.valueOf(accountNo)});
    }

    // 删除用户
    public int deleteUser(int accountNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USERS, COLUMN_ACCOUNT_NO + "=?", new String[]{String.valueOf(accountNo)});
    }

    // 删除预订
    public boolean updateBooking(String bookingNo, String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("phone", phone);

        int result = db.update("Booking", values, "bookingNo = ?", new String[]{bookingNo});
        return result > 0;
    }

    // 删除预订
    public boolean deleteBooking(int bookingNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("Booking", "bookingNo = ?", new String[]{String.valueOf(bookingNo)});
        return result > 0;
    }

    // 获取用户详细信息
    public Cursor getUserDetails(String memberName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_MEMBER_NAME + " = ?";
        return db.rawQuery(query, new String[]{memberName});
    }

    // 检查用户凭证
    public boolean checkUserCredentials(String memberName, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE memberName = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{memberName, password});

        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;  // 用户凭证匹配
        }
        if (cursor != null) {
            cursor.close();
        }
        return false;  // 用户凭证不匹配
    }

    // 更新登录状态
    public void updateLoginStatus(String memberName, boolean status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isLoggedIn", status ? 1 : 0);  // 1 表示已登录，0 表示未登录
        db.update(TABLE_USERS, values, "memberName = ?", new String[]{memberName});
    }

    // 检查用户名是否存在
    public boolean checkMemberNameExists(String memberName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE memberName = ?";
        Cursor cursor = db.rawQuery(query, new String[]{memberName});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // 注册用户
    public boolean registerUser(String memberName, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("memberName", memberName);
        values.put("password", password);  // 假设密码是以明文存储，实际中应该加密存储

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;  // 如果插入成功，返回 true，否则返回 false
    }

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
        return null; // 如果没有找到已登录的用户
    }

    // 插入预订数据
    public long addBooking(String accountNo, String courtNo, String courtType, String bookingDate, String duration, String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 检查用户是否已有预订
        if (userHasBooking(accountNo)) {
            return -1; // 表示用户已有预订，不能再预订
        }

        // 插入预订数据
        ContentValues values = new ContentValues();
        values.put("accountNo", accountNo);
        values.put("courtNo", courtNo);
        values.put("courtType", courtType);
        values.put("bookingDate", bookingDate);
        values.put("duration", duration);
        values.put("email", email);
        values.put("phone", phone);

        long result = db.insert("Booking", null, values);

        // 更新用户的预订状态为已预订
        if (result != -1) {
            updateUserBookingStatus(accountNo, true);
        }

        return result;
    }

    // 更新用户的预订状态
    public void updateUserBookingStatus(String accountNo, boolean hasBooking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hasBooking", hasBooking ? 1 : 0);

        db.update("User", values, "accountNo = ?", new String[]{accountNo});
    }

    // 检查用户是否已有预订
    public boolean userHasBooking(String accountNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT hasBooking FROM User WHERE accountNo = ?";
        Cursor cursor = db.rawQuery(query, new String[]{accountNo});

        if (cursor != null && cursor.moveToFirst()) {
            int hasBooking = cursor.getInt(cursor.getColumnIndex("hasBooking"));
            cursor.close();
            return hasBooking == 1; // 1 表示用户已有预订
        }

        return false; // 用户没有预订记录
    }

    public long addBooking(Booking booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // 从 Booking 对象中获取所需数据
        values.put("accountNo", booking.getAccountNo());
        values.put("courtNo", booking.getCourtNo());
        values.put("courtType", booking.getCourtType());
        values.put("bookingDate", booking.getDate());
        values.put("duration", booking.getDuration());
        values.put("email", booking.getEmail());
        values.put("phone", booking.getPhoneNumber());

        // 插入数据到 Booking 表
        return db.insert("Booking", null, values);
    }






}

