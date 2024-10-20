package com.example.tennisbooking.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tennisbooking.R;

public class HomeFragment extends Fragment {

    private Button btNBookNow ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize buttons
        Button btnBookNow = view.findViewById(R.id.btnBookNow);

        // Set click listeners
        btnBookNow.setOnClickListener(v -> {
            // Navigate to CourtsFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content, new CourtsFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        return view;
    }
}
