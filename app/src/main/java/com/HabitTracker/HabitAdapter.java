package com.HabitTracker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private final Context context;
    private List<Habit> habitList;
    private final DatabaseHelper dbHelper;
    private final String userEmail; // Standardized variable

    // Interface for the Dashboard to listen for changes
    public interface OnHabitChangeListener {
        void onChanged();
    }

    private final OnHabitChangeListener listener;

    // Standard Constructor used by DashboardActivity
    public HabitAdapter(Context context, List<Habit> habitList, String email, OnHabitChangeListener listener) {
        this.context = context;
        this.habitList = habitList;
        this.dbHelper = new DatabaseHelper(context);
        this.userEmail = email; // Received directly from Dashboard
        this.listener = listener;
    }

    public void updateHabits(List<Habit> newHabits) {
        this.habitList = newHabits;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);

        holder.tvName.setText(habit.getName());
        holder.tvCategory.setText(habit.getCategory());
        holder.tvFrequency.setText(habit.getFrequency());

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Get Streak
        int streak = 0;
        if (userEmail != null && !userEmail.isEmpty()) {
            streak = dbHelper.getStreak(userEmail, habit.getId());
        }
        holder.tvStreak.setText("🔥 " + streak);

        // Check if habit is done today
        boolean isDone = false;
        if (userEmail != null && !userEmail.isEmpty()) {
            isDone = dbHelper.isHabitDoneToday(userEmail, habit.getId(), today);
        }

        // Update UI look based on done status
        setDoneState(holder, isDone);

        // Color coding for categories
        switch (habit.getCategory()) {
            case "Health": holder.tvCategory.setTextColor(Color.parseColor("#4A7C59")); break;
            case "Study":  holder.tvCategory.setTextColor(Color.parseColor("#4A6FA5")); break;
            case "Mind":   holder.tvCategory.setTextColor(Color.parseColor("#7B5EA7")); break;
            case "Soul":   holder.tvCategory.setTextColor(Color.parseColor("#C26D8A")); break;
            case "Work":   holder.tvCategory.setTextColor(Color.parseColor("#D4813A")); break;
            default:       holder.tvCategory.setTextColor(Color.parseColor("#888888")); break;
        }

        // Logic for clicking the "Done" button/text
        holder.btnDone.setOnClickListener(v -> {
            if (userEmail == null || userEmail.isEmpty()) return;

            boolean currentlyDone = dbHelper.isHabitDoneToday(userEmail, habit.getId(), today);

            // Toggle logic
            if (!currentlyDone) {
                dbHelper.updateHabitLog(userEmail, habit.getId(), today, true);
                setDoneState(holder, true);
            } else {
                dbHelper.unlogHabit(userEmail, habit.getId(), today);
                setDoneState(holder, false);
            }

            // Refresh the streak count immediately
            int newStreak = dbHelper.getStreak(userEmail, habit.getId());
            holder.tvStreak.setText("🔥 " + newStreak);

            // Tell Dashboard to update the progress bar and check for Panda reward!
            if (listener != null) listener.onChanged();

            // Simple "Pop" animation
            holder.btnDone.animate()
                    .scaleX(1.2f).scaleY(1.2f).setDuration(100)
                    .withEndAction(() -> holder.btnDone.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                    .start();
        });
    }

    private void setDoneState(HabitViewHolder holder, boolean done) {
        if (done) {
            holder.btnDone.setBackgroundResource(R.drawable.card_category_selected); // Make sure this exists!
            holder.btnDone.setTextColor(Color.WHITE);
            holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvName.setAlpha(0.5f);
        } else {
            holder.btnDone.setBackgroundResource(R.drawable.card_category_unselected); // Make sure this exists!
            holder.btnDone.setTextColor(Color.parseColor("#1F1A17"));
            holder.tvName.setPaintFlags(holder.tvName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvName.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        return habitList != null ? habitList.size() : 0;
    }

    static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvFrequency, tvStreak, btnDone;

        HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvHabitName);
            tvCategory = itemView.findViewById(R.id.tvHabitCategory);
            tvFrequency = itemView.findViewById(R.id.tvHabitFrequency);
            tvStreak = itemView.findViewById(R.id.tvStreak);
            btnDone = itemView.findViewById(R.id.btnDone);
        }
    }
}