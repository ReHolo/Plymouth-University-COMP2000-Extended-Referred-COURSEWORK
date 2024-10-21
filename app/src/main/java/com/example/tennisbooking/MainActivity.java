package com.example.tennisbooking;

import android.database.Cursor;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.tennisbooking.Interface.BookingService;
import com.example.tennisbooking.R;
import com.example.tennisbooking.db.DatabaseHelper;
import com.example.tennisbooking.entity.Booking;
import com.example.tennisbooking.fragment.CourtsFragment;
import com.example.tennisbooking.fragment.HomeFragment;
import com.example.tennisbooking.fragment.MineFragment;
import com.example.tennisbooking.fragment.WeatherFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private SparseArray<Fragment> fragmentArray = new SparseArray<>();
    private BottomNavigationView mBottomNavigationView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Initialize Retrofit client
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://web.socem.plymouth.ac.uk/COMP2000/ReferralApi/api/Bookings/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BookingService apiService = retrofit.create(BookingService.class);

        // Fetch and store user and booking data
        fetchAndStoreUsers(apiService);
        fetchAndStoreBookings(apiService);

        // Initialize fragments
        fragmentArray.put(R.id.home, new HomeFragment());
        fragmentArray.put(R.id.courts, new CourtsFragment());
        fragmentArray.put(R.id.weather, new WeatherFragment());
        fragmentArray.put(R.id.mine, new MineFragment());

        // Set up bottom navigation
        mBottomNavigationView = findViewById(R.id.bottomNavigationView);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Default to HomeFragment
        selectedFragment(R.id.home);
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        selectedFragment(item.getItemId());
        return true;
    }

    private void selectedFragment(int itemId) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideAllFragments(fragmentTransaction);

        Fragment selectedFragment = fragmentArray.get(itemId);
        if (selectedFragment != null) {
            if (!selectedFragment.isAdded()) {
                fragmentTransaction.add(R.id.content, selectedFragment);
            } else {
                fragmentTransaction.show(selectedFragment);
            }

            if (itemId == R.id.mine) {
                // Pass user data to MineFragment
                String loggedInUsername = getIntent().getStringExtra("memberName");
                Cursor userCursor = databaseHelper.getUserDetails(loggedInUsername);
                if (userCursor != null && userCursor.moveToFirst()) {
                    String memberName = userCursor.getString(userCursor.getColumnIndex("memberName"));
                    String accountNo = userCursor.getString(userCursor.getColumnIndex("accountNo"));
                    userCursor.close();

                    Bundle bundle = new Bundle();
                    bundle.putString("memberName", memberName);
                    bundle.putString("accountNo", accountNo);
                    selectedFragment.setArguments(bundle);
                }
            }
        }

        fragmentTransaction.commit();
    }

    private void hideAllFragments(FragmentTransaction fragmentTransaction) {
        for (int i = 0; i < fragmentArray.size(); i++) {
            Fragment fragment = fragmentArray.valueAt(i);
            if (fragment.isAdded()) {
                fragmentTransaction.hide(fragment);
            }
        }
    }

    private void fetchAndStoreUsers(BookingService apiService) {
        Call<List<Booking>> call = apiService.getAllBookings();
        call.enqueue(new Callback<List<Booking>>() {
            public void onResponse(@NonNull Call<List<Booking>> call, @NonNull Response<List<Booking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Booking> users = response.body();
                    for (Booking user : users) {
                        databaseHelper.addUser(user.getMemberName(), "defaultPassword", user.getPhoneNumber(), user.getEmail());
                    }
                }
            }

            public void onFailure(@NonNull Call<List<Booking>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "API request failed - user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAndStoreBookings(BookingService apiService) {
        Call<List<Booking>> call = apiService.getAllBookings();
        call.enqueue(new Callback<List<Booking>>() {
            public void onResponse(@NonNull Call<List<Booking>> call, @NonNull Response<List<Booking>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Booking> bookings = response.body();
                    for (Booking booking : bookings) {
                        databaseHelper.addBooking(booking);
                    }
                }
            }

            public void onFailure(Call<List<Booking>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "API request failed - booking data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}