package com.example.tennisbooking.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tennisbooking.BookingActivity;
import com.example.tennisbooking.R;
import com.example.tennisbooking.db.DatabaseHelper;
import com.example.tennisbooking.entity.Booking;

import java.util.Calendar;
import java.util.List;

public class CourtAdapter extends RecyclerView.Adapter<CourtAdapter.CourtViewHolder> {
    private List<Booking> bookingList;
    private Context context;
    private boolean userHasBooking;  // 表示用户是否已经预订

    public CourtAdapter(List<Booking> bookingList, Context context, boolean userHasBooking) {
        this.bookingList = bookingList;
        this.context = context;
        this.userHasBooking = userHasBooking;
    }

    @NonNull
    @Override
    public CourtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.court_item, parent, false);
        return new CourtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourtViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.tvCourtNo.setText("Court No: " + booking.getCourtNo());
        holder.tvCourtType.setText("Court Type: " + booking.getCourtType());

        // 根据场地类型设置可用季节
        if ("Grass".equalsIgnoreCase(booking.getCourtType())) {
            holder.tvAvailableSeason.setText("Available Season: Open in Summer");
        } else {
            holder.tvAvailableSeason.setText("Available Season: All Year" );
        }



        // 点击预订按钮
        holder.btnBookCourt.setOnClickListener(v -> {
            // 实例化 DatabaseHelper
            DatabaseHelper databaseHelper = new DatabaseHelper(context);

            // 获取当前用户的账号 ID
            String accountNo = databaseHelper.getCurrentUserAccountNo();

            if (accountNo == null) {
                Toast.makeText(context, "No logged in user found.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 检查用户是否已有预订
            if (databaseHelper.userHasBooking(accountNo)) {
                Toast.makeText(context, "You can only book one court at a time.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 判断草地场地的月份限制
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH) + 1; // Calendar 月份从 0 开始，需加 1

            if ("Grass".equalsIgnoreCase(booking.getCourtType()) && (month < 7 || month > 9)) {
                Toast.makeText(context, "Grass courts can only be booked in July, August, and September.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 跳转到 BookingActivity 并传递球场信息
            Intent intent = new Intent(context, BookingActivity.class);
            intent.putExtra("courtNo", booking.getCourtNo());
            intent.putExtra("courtType", booking.getCourtType());
            intent.putExtra("availableSeason", booking.getAvailableSeason());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class CourtViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourtNo, tvCourtType, tvAvailableSeason;
        Button btnBookCourt;

        public CourtViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourtNo = itemView.findViewById(R.id.tvCourtNo);
            tvCourtType = itemView.findViewById(R.id.tvCourtType);
            tvAvailableSeason = itemView.findViewById(R.id.tvAvailableSeason);
            btnBookCourt = itemView.findViewById(R.id.btnBookCourt);
        }
    }
}
