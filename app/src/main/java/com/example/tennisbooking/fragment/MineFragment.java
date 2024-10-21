// MineFragment.java
package com.example.tennisbooking.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.tennisbooking.LoginActivity;

import com.example.tennisbooking.ManageBookingActivity;
import com.example.tennisbooking.R;

public class MineFragment extends Fragment {

    private TextView memberNameTextView, accountNoTextView;
    private Button logoutButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mine, container, false);

        // Initialize TextViews and Button
        memberNameTextView = root.findViewById(R.id.memberNameTextView);
        accountNoTextView = root.findViewById(R.id.accountNoTextView);
        logoutButton = root.findViewById(R.id.logoutButton);

        // Retrieve user data from arguments
        Bundle args = getArguments();
        if (args != null) {
            String memberName = args.getString("memberName");
            String accountNo = args.getString("accountNo");
            if (memberName != null && accountNo != null) {
                memberNameTextView.setText("Welcome! " + memberName);
                accountNoTextView.setText("Account No: " + accountNo);
            }
        }

        // Set logout button click listener
        logoutButton.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Clear user info and redirect to login activity
                        clearUserInfo();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Initialize and set click listener for manage bookings button
        Button manageBookingsButton = root.findViewById(R.id.btnManageBookings);
        manageBookingsButton.setOnClickListener(v -> {
            // Redirect to manage bookings activity
            Intent intent = new Intent(getActivity(), ManageBookingActivity.class);
            startActivity(intent);
        });


        return root;
    }

    private void clearUserInfo() {
        // Implement user info clearing logic if needed
        // e.g., clearing shared preferences or logging out the user
    }
}
