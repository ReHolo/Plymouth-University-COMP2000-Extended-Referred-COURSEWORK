package com.example.tennisbooking.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TennisCourtReservation.db";
    private static final int DATABASE_VERSION = 2;  // 增加版本号以触发 onUpgrade

    // SQL 语句创建各表
    private static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS Users (" +
            "userId INTEGER PRIMARY KEY AUTOINCREMENT," +
            "username TEXT UNIQUE NOT NULL," +
            "password TEXT NOT NULL," +
            "email TEXT UNIQUE NOT NULL," +
            "phoneNumber TEXT," +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP);";

    private static final String CREATE_COURT_TABLE = "CREATE TABLE IF NOT EXISTS Court (" +
            "courtNo INTEGER PRIMARY KEY," +
            "courtType TEXT NOT NULL," +
            "isAvailable BOOLEAN NOT NULL," +
            "availableSeason TEXT DEFAULT 'All');";

    private static final String CREATE_BOOKING_TABLE = "CREATE TABLE IF NOT EXISTS Booking (" +
            "bookingNo INTEGER PRIMARY KEY AUTOINCREMENT," +
            "memberName TEXT," +
            "accountNo TEXT," +
            "email TEXT," +
            "phoneNumber TEXT," +
            "courtType TEXT," +
            "courtNo INTEGER," +
            "date TEXT," +
            "dayOfWeek INTEGER," +
            "duration TEXT," +
            "FOREIGN KEY (dayOfWeek) REFERENCES DayOfWeek(dayOfWeek));";

    private static final String CREATE_DAYOFWEEK_TABLE = "CREATE TABLE IF NOT EXISTS DayOfWeek (" +
            "dayOfWeek INTEGER PRIMARY KEY," +
            "description TEXT);";

    // 插入 DayOfWeek 和 Court 的初始数据
    private static final String INSERT_DAYOFWEEK_VALUES =
            "INSERT INTO DayOfWeek (dayOfWeek, description) VALUES " +
                    "(0, 'Sunday'), (1, 'Monday'), (2, 'Tuesday'), (3, 'Wednesday'), " +
                    "(4, 'Thursday'), (5, 'Friday'), (6, 'Saturday');";

    private static final String INSERT_COURT_VALUES =
            "INSERT INTO Court (courtNo, courtType, isAvailable, availableSeason) VALUES " +
                    "(1, 'Artificial', 1, 'All'), " +
                    "(2, 'Artificial', 1, 'All'), " +
                    "(3, 'Artificial', 1, 'All'), " +
                    "(4, 'Artificial', 1, 'All'), " +
                    "(5, 'Hard', 1, 'All'), " +
                    "(6, 'Hard', 1, 'All'), " +
                    "(7, 'Grass', 1, 'Summer'), " +
                    "(8, 'Grass', 1, 'Summer'), " +
                    "(9, 'Grass', 1, 'Summer'), " +
                    "(10, 'Grass', 1, 'Summer');";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建表
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_COURT_TABLE);
        db.execSQL(CREATE_BOOKING_TABLE);
        db.execSQL(CREATE_DAYOFWEEK_TABLE);

        // 插入初始数据
        db.execSQL(INSERT_DAYOFWEEK_VALUES);
        db.execSQL(INSERT_COURT_VALUES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 删除旧表（用于开发期间测试，如果需要保留数据，可以优化为 ALTER TABLE）
        db.execSQL("DROP TABLE IF EXISTS Users");
        db.execSQL("DROP TABLE IF EXISTS Court");
        db.execSQL("DROP TABLE IF EXISTS Booking");
        db.execSQL("DROP TABLE IF EXISTS DayOfWeek");
        onCreate(db);
    }
}
