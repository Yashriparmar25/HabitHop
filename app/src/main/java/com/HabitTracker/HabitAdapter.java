package com.HabitTracker;

import android.content.Context;
import android.content.SharedPreferences;
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
    private final SharedPreferences prefs;
    private final String currentUserEmail;

    public interface OnHabitUpdateListener {
        void onHabitUpdated();
    }

    private final OnHabitUpdateListener listener;

    public HabitAdapter(Context context, List<Habit> habitList, OnHabitUpdateListener listener) {
        this.context = context;
        this.habitList = habitList;
        this.dbHelper = new DatabaseHelper(context);
        this.prefs = context.getSharedPreferences("HabitKit", Context.MODE_PRIVATE);
        this.currentUserEmail = prefs.getString("current_user_email", "");
        this.listener = listener;
    }

    public HabitAdapter(Context context, List<Habit> habitList) {
        this.context = context;
        this.habitList = habitList;
        this.dbHelper = new DatabaseHelper(context);
        this.prefs = context.getSharedPreferences("HabitKit", Context.MODE_PRIVATE);
        this.currentUserEmail = prefs.getString("current_user_email", "");
        this.listener = null;
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

        int streak = 0;
        if (currentUserEmail != null && !currentUserEmail.isEmpty()) {
            streak = dbHelper.getStreak(currentUserEmail, habit.getId());
        }
        holder.tvStreak.setText("🔥 " + streak);

        boolean isDone = false;
        if (currentUserEmail != null && !currentUserEmail.isEmpty()) {
            isDone = dbHelper.isHabitDoneToday(currentUserEmail, habit.getId(), today);
        }
        setDoneState(holder, isDone);

        switch (habit.getCategory()) {
            case "Health":
                holder.tvCategory.setTextColor(Color.parseColor("#4A7C59"));
                break;
            case "Study":
                holder.tvCategory.setTextColor(Color.parseColor("#8C916C"));
                break;
            case "Mind":
                holder.tvCategory.setTextColor(Color.parseColor("#7B6F72"));
                break;
            case "Soul":
                holder.tvCategory.setTextColor(Color.parseColor("#A0856C"));
                break;
            case "Work":
                holder.tvCategory.setTextColor(Color.parseColor("#B69C85"));
                break;
            default:
                holder.tvCategory.setTextColor(Color.parseColor("#888888"));
                break;
        }

        holder.btnDone.setOnClickListener(v -> {
            if (currentUserEmail == null || currentUserEmail.isEmpty()) return;

            boolean currentlyDone = dbHelper.isHabitDoneToday(currentUserEmail, habit.getId(), today);

            if (!currentlyDone) {
                dbHelper.updateHabitLog(currentUserEmail, habit.getId(), today, true);
                setDoneState(holder, true);
            } else {
                dbHelper.unlogHabit(currentUserEmail, habit.getId(), today);
                setDoneState(holder, false);
            }

            int newStreak = dbHelper.getStreak(currentUserEmail, habit.getId());
            holder.tvStreak.setText("🔥 " + newStreak);

            if (listener != null) listener.onHabitUpdated();

            holder.btnDone.animate()
                    .scaleX(1.3f).scaleY(1.3f).setDuration(120)
                    .withEndAction(() ->
                            holder.btnDone.animate()
                                    .scaleX(1f).scaleY(1f)
                                    .setDuration(100).start()
                    ).start();
        });
    }

    private void setDoneState(HabitViewHolder holder, boolean done) {
        if (done) {
            holder.btnDone.setBackgroundResource(R.drawable.card_category_selected);
            holder.btnDone.setTextColor(Color.WHITE);
            holder.tvName.setPaintFlags(
                    holder.tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
            holder.tvName.setTextColor(Color.parseColor("#A29A94"));
        } else {
            holder.btnDone.setBackgroundResource(R.drawable.card_category_unselected);
            holder.btnDone.setTextColor(Color.parseColor("#C0B8B0"));
            holder.tvName.setPaintFlags(
                    holder.tvName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG
            );
            holder.tvName.setTextColor(Color.parseColor("#1F1A17"));
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