package com.example.tennisbooking.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tennisbooking.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class CourtDAO {
    private SQLiteOpenHelper dbHelper;

    public CourtDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // 获取所有球场信息
    public List<Court> getAllCourts() {
        List<Court> courts = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("Court",
                new String[]{"courtNo", "courtType", "isAvailable", "availableSeason"},
                null, null, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        int courtNoIndex = cursor.getColumnIndex("courtNo");
                        int courtTypeIndex = cursor.getColumnIndex("courtType");
                        int isAvailableIndex = cursor.getColumnIndex("isAvailable");
                        int availableSeasonIndex = cursor.getColumnIndex("availableSeason");

                        if (courtNoIndex >= 0 && courtTypeIndex >= 0 && isAvailableIndex >= 0 && availableSeasonIndex >= 0) {
                            Court court = new Court();
                            court.setCourtNo(cursor.getInt(courtNoIndex));
                            court.setCourtType(cursor.getString(courtTypeIndex));
                            court.setAvailable(cursor.getInt(isAvailableIndex) == 1);
                            court.setAvailableSeason(cursor.getString(availableSeasonIndex));

                            courts.add(court);
                        }
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }

        db.close();
        return courts;
    }

    // 获取特定球场的信息
    public Court getCourt(int courtNo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Court court = null;

        Cursor cursor = db.query("Court",
                new String[]{"courtNo", "courtType", "isAvailable", "availableSeason"},
                "courtNo=?",
                new String[]{String.valueOf(courtNo)},
                null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int courtNoIndex = cursor.getColumnIndex("courtNo");
                    int courtTypeIndex = cursor.getColumnIndex("courtType");
                    int isAvailableIndex = cursor.getColumnIndex("isAvailable");
                    int availableSeasonIndex = cursor.getColumnIndex("availableSeason");

                    if (courtNoIndex >= 0 && courtTypeIndex >= 0 && isAvailableIndex >= 0 && availableSeasonIndex >= 0) {
                        court = new Court();
                        court.setCourtNo(cursor.getInt(courtNoIndex));
                        court.setCourtType(cursor.getString(courtTypeIndex));
                        court.setAvailable(cursor.getInt(isAvailableIndex) == 1);
                        court.setAvailableSeason(cursor.getString(availableSeasonIndex));
                    }
                }
            } finally {
                cursor.close();
            }
        }

        db.close();
        return court;
    }

    // 更新球场信息
    public void updateCourt(Court court) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("courtType", court.getCourtType());
        values.put("isAvailable", court.isAvailable() ? 1 : 0);
        values.put("availableSeason", court.getAvailableSeason());

        db.update("Court", values, "courtNo=?", new String[]{String.valueOf(court.getCourtNo())});
        db.close();
    }
}

