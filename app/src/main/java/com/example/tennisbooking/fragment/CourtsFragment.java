package com.example.tennisbooking.fragment;

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
import com.example.tennisbooking.entity.Court;

import java.util.List;

public class CourtsFragment extends Fragment {
    private RecyclerView recyclerViewCourts;
    private CourtAdapter courtAdapter;
    private List<Court> courtList;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courts, container, false);
        recyclerViewCourts = view.findViewById(R.id.recyclerView);
        recyclerViewCourts.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseHelper = new DatabaseHelper(getContext());
        fetchCourts();
        return view;
    }

    private void fetchCourts() {
        courtList = databaseHelper.getAllCourts();
        courtAdapter = new CourtAdapter(courtList, getContext());
        recyclerViewCourts.setAdapter(courtAdapter);
    }
}