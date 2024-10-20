// CourtsFragment.java
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
import com.example.tennisbooking.Interface.BookingService;
import com.example.tennisbooking.entity.Booking;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CourtsFragment extends Fragment {
    private RecyclerView recyclerViewCourts;
    private CourtAdapter courtAdapter;
    private List<Booking> BookingList;
    private boolean userHasBooking = false; // This should be set based on actual user data

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courts, container, false);
        recyclerViewCourts = view.findViewById(R.id.recyclerView);
        recyclerViewCourts.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchBookings();
        return view;
    }

    private void fetchBookings() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://web.socem.plymouth.ac.uk/COMP2000/ReferralApi/api/Bookings/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BookingService bookingService = retrofit.create(BookingService.class);
        Call<List<Booking>> call = bookingService.getAllBookings();
        call.enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(@NonNull Call<List<Booking>> call, @NonNull Response<List<Booking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    courtAdapter = new CourtAdapter(response.body(), getContext(), userHasBooking);
                    recyclerViewCourts.setAdapter(courtAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Booking>> call, @NonNull Throwable t) {
                // Handle failure
            }
        });

        BookingList = getCourts();
        List<Booking> grassCourts = new ArrayList<>();
        for (Booking court : BookingList) {
            if ("Grass".equalsIgnoreCase(court.getCourtType())) {
                court.setAvailableSeason("Open in Summer");
                grassCourts.add(court);
            }else {
                court.setAvailableSeason("Open all year");
            }
        }

        courtAdapter = new CourtAdapter(grassCourts, getContext(), userHasBooking);
        recyclerViewCourts.setAdapter(courtAdapter);
    }

    private List<Booking> getCourts() {
        return new ArrayList<>();
    }
}