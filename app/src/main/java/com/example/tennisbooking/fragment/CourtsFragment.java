// CourtsFragment.java
package com.example.tennisbooking.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tennisbooking.R;
import com.example.tennisbooking.adapter.CourtAdapter;
import com.example.tennisbooking.db.DatabaseHelper;
import com.example.tennisbooking.entity.Booking;

import java.util.ArrayList;
import java.util.List;

public class CourtsFragment extends Fragment {

    private RecyclerView recyclerViewCourts;
    private CourtAdapter courtAdapter;
    private DatabaseHelper databaseHelper;
    private boolean userHasBooking = false; // This should be set based on actual user data

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courts, container, false);

        // 初始化RecyclerView
        recyclerViewCourts = view.findViewById(R.id.recyclerView);
        recyclerViewCourts.setLayoutManager(new LinearLayoutManager(getContext()));

        // 初始化数据库帮助类
        databaseHelper = new DatabaseHelper(getContext());

        // 获取球场信息并设置到适配器中
        fetchCourts();

        return view;
    }

    // 从数据库获取球场信息
    private void fetchCourts() {
        List<Booking> courtList = getCourtsFromDatabase();

        // 设置草地球场的可用季节
        for (Booking court : courtList) {
            if ("Grass".equalsIgnoreCase(court.getCourtType())) {
                court.setAvailableSeason("Open in Summer");
            } else {
                court.setAvailableSeason("Open all year");
            }
        }

        // 创建适配器并设置给RecyclerView
        courtAdapter = new CourtAdapter(courtList, getContext(), userHasBooking);
        recyclerViewCourts.setAdapter(courtAdapter);
    }

    // 获取所有球场信息
    private List<Booking> getCourtsFromDatabase() {
        List<Booking> courtList = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllCourts();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Booking court = new Booking(
                        0, // bookingNo
                        null, // accountNo
                        null, // memberName
                        cursor.getString(cursor.getColumnIndex("courtType")),
                        cursor.getString(cursor.getColumnIndex("courtNo")),
                        null, // email
                        null, // phoneNumber
                        null, // date
                        0, // dayOfWeek
                        null, // duration
                        null  // availableSeason
                );
                courtList.add(court);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return courtList;
    }
}
