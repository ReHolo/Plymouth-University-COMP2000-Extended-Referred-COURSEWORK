package com.example.tennisbooking.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tennisbooking.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    private Button btnBookNow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize buttons
        btnBookNow = view.findViewById(R.id.btnBookNow);

        // Ensure btnBookNow is not null before setting click listener
        if (btnBookNow != null) {
            // Set click listeners
            btnBookNow.setOnClickListener(v -> navigateToCourtsFragment());
        } else {
            // Handle null case (optional)
            Toast.makeText(getActivity(), "Button not found!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void navigateToCourtsFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.content);

        if (!(currentFragment instanceof CourtsFragment)) {
            CourtsFragment courtsFragment = new CourtsFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Add or replace the fragment and commit
            fragmentTransaction.replace(R.id.content, courtsFragment);
            fragmentTransaction.addToBackStack(null); // Add to backstack for navigation
            fragmentTransaction.commit();

            // Update the BottomNavigationView to select the courts tab
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setSelectedItemId(R.id.courts); // Ensure it selects the courts item
        }
    }
}