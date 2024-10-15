package com.example.tennisbooking;


import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.tennisbooking.fragment.CourtsFragment;
import com.example.tennisbooking.fragment.HomeFragment;
import com.example.tennisbooking.fragment.MineFragment;
import com.example.tennisbooking.fragment.WeatherFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private HomeFragment mHomeFragment;
    private CourtsFragment mCourtsFragment;
    private WeatherFragment mWeatherFragment;
    private MineFragment mMineFragment;

    private BottomNavigationView mBottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mBottomNavigationView = findViewById(R.id.bottomNavigationView);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.home) {

                    selectedFragment(0);

                } else if (item.getItemId() == R.id.courts) {

                    selectedFragment(1);

                } else if (item.getItemId() == R.id.weather) {

                    selectedFragment(2);

                } else if (item.getItemId() == R.id.mine) {

                    selectedFragment(3);

                }
                return true;
            }
        });

        selectedFragment(0);

    }

    private void selectedFragment(int position) {

        androidx.fragment.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideFragment(fragmentTransaction);

        if (position == 0) {
            if (mHomeFragment == null) {
                mHomeFragment = new HomeFragment();
                fragmentTransaction.add(R.id.content, mHomeFragment);
            } else {
                fragmentTransaction.show(mHomeFragment);
            }
        } else if (position == 1) {
            if (mCourtsFragment == null) {
                mCourtsFragment = new CourtsFragment();
                fragmentTransaction.add(R.id.content, mCourtsFragment);
            } else {
                fragmentTransaction.show(mCourtsFragment);
            }
        } else if (position == 2) {
            if (mWeatherFragment == null) {
                mWeatherFragment = new WeatherFragment();
                fragmentTransaction.add(R.id.content, mWeatherFragment);
            } else {
                fragmentTransaction.show(mWeatherFragment);
            }
        } else {
            if (mMineFragment == null) {
                mMineFragment = new MineFragment();
                fragmentTransaction.add(R.id.content, mMineFragment);
            } else {
                fragmentTransaction.show(mMineFragment);
            }
        }

        fragmentTransaction.commit();


    }

    private void hideFragment(FragmentTransaction fragmentTransaction) {

        if (mHomeFragment != null) {
            fragmentTransaction.hide(mHomeFragment);
        }
        if (mCourtsFragment != null) {
            fragmentTransaction.hide(mCourtsFragment);
        }
        if (mWeatherFragment != null) {
            fragmentTransaction.hide(mWeatherFragment);
        }
        if (mMineFragment != null) {
            fragmentTransaction.hide(mMineFragment);
        }
    }
}