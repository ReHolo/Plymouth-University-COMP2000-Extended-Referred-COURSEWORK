package com.example.tennisbooking.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tennisbooking.R;
import com.example.tennisbooking.entity.Court;
import com.example.tennisbooking.entity.CourtDAO;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class CourtsFragment extends Fragment {

    private LinearLayout courtsContainer;
    private CourtDAO courtDAO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courts, container, false);

        // 设置 Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        courtsContainer = view.findViewById(R.id.courtsContainer);
        courtDAO = new CourtDAO(getActivity());

        // 加载球场信息
        loadCourtData();

        return view;
    }

    private void loadCourtData() {
        List<Court> courts = courtDAO.getAllCourts();

        for (Court court : courts) {
            // 创建一个新的 TextView 来显示每个球场的详细信息
            TextView courtInfoTextView = new TextView(getActivity());
            String courtInfo = "球场编号: " + court.getCourtNo() + "\n" +
                    "球场类型: " + court.getCourtType() + "\n" +
                    "是否可用: " + (court.isAvailable() ? "是" : "否") + "\n" +
                    "开放季节: " + court.getAvailableSeason() + "\n";
            courtInfoTextView.setText(courtInfo);
            courtInfoTextView.setTextSize(16);
            courtInfoTextView.setPadding(0, 0, 0, 16);

            // 将每个 TextView 添加到 courtsContainer 中
            courtsContainer.addView(courtInfoTextView);
        }
    }
}
