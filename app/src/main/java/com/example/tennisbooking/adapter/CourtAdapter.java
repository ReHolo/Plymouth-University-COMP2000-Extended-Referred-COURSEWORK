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
import com.example.tennisbooking.entity.Court;

import java.util.Calendar;
import java.util.List;

public class CourtAdapter extends RecyclerView.Adapter<CourtAdapter.CourtViewHolder> {
    private List<Court> courtList;
    private Context context;
    private boolean userHasBooking;

    public CourtAdapter(List<Court> courtList, Context context, boolean userHasBooking) {
        this.courtList = courtList;
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
        Court court = courtList.get(position);
        holder.tvCourtNo.setText("Court No: " + court.getCourtNo());
        holder.tvCourtType.setText("Court Type: " + court.getCourtType());

        if ("Grass".equalsIgnoreCase(court.getCourtType())) {
            holder.tvAvailableSeason.setText("Available Season: Open in Summer");
        } else {
            holder.tvAvailableSeason.setText("Available Season: All Year");
        }

        holder.btnBookCourt.setOnClickListener(v -> {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            String accountNo = databaseHelper.getCurrentUserAccountNo();

            if (accountNo == null) {
                Toast.makeText(context, "No logged in user found.", Toast.LENGTH_SHORT).show();
                return;
            }

            int accountNoInt = Integer.parseInt(accountNo);
            if (databaseHelper.userHasBooking(accountNoInt)) {
                Toast.makeText(context, "You can only book one court at a time.", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH) + 1;

            if ("Grass".equalsIgnoreCase(court.getCourtType()) && (month < 7 || month > 9)) {
                Toast.makeText(context, "Grass courts can only be booked in July, August, and September.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(context, BookingActivity.class);
            intent.putExtra("courtNo", court.getCourtNo());
            intent.putExtra("courtType", court.getCourtType());
            intent.putExtra("availableSeason", "All Year");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return courtList.size();
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