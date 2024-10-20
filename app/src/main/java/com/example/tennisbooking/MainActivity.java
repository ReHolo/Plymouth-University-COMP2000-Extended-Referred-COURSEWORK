package com.example.tennisbooking;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.tennisbooking.Interface.BookingService;
import com.example.tennisbooking.db.DatabaseHelper;
import com.example.tennisbooking.entity.User;
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

    private HomeFragment mHomeFragment;
    private CourtsFragment mCourtsFragment;
    private WeatherFragment mWeatherFragment;
    private MineFragment mMineFragment;

    private BottomNavigationView mBottomNavigationView;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 设置Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化数据库助手类
        databaseHelper = new DatabaseHelper(this);

        // 初始化Retrofit客户端
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://web.socem.plymouth.ac.uk/COMP2000/ReferralApi/api/Bookings/")  // 替换为API的基础URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BookingService apiService = retrofit.create(BookingService.class);

        // 获取所有用户数据并存储到数据库
        fetchAndStoreUsers(apiService);

        // 获取所有预订数据并存储到数据库
        fetchAndStoreBookings(apiService);

        // 设置底部导航
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

        // 默认选择HomeFragment
        selectedFragment(0);
    }

    // 获取用户数据并存储到数据库
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

    // 获取预订数据并存储到数据库
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

    private void selectedFragment(int position) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
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

            // 获取当前登录用户信息
            String loggedInUsername = getIntent().getStringExtra("memberName");
            Cursor userCursor = databaseHelper.getUserDetails(loggedInUsername);
            String memberName = "";
            String accountNo = "";
            if (userCursor != null && userCursor.moveToFirst()) {
                int memberNameIndex = userCursor.getColumnIndex("memberName");
                int accountNoIndex = userCursor.getColumnIndex("accountNo");

                if (memberNameIndex != -1) {
                    memberName = userCursor.getString(memberNameIndex);
                }
                if (accountNoIndex != -1) {
                    accountNo = userCursor.getString(accountNoIndex);
                }
                userCursor.close();
            }

            // 传递用户数据到 MineFragment
            Bundle bundle = new Bundle();
            bundle.putString("memberName", memberName);
            bundle.putString("accountNo", accountNo);
            mMineFragment.setArguments(bundle);
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