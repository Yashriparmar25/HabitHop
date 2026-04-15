package com.HabitTracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RewardManager {

    private final Context context;
    private final DatabaseHelper dbHelper;
    private final SharedPreferences prefs;
    private final Random random = new Random();

    public RewardManager(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
        this.prefs = context.getSharedPreferences("HabitKit", Context.MODE_PRIVATE);
    }

    public void checkAndShowReward(String userEmail) {
        if (TextUtils.isEmpty(userEmail)) return;

        List<Habit> habits = dbHelper.getAllHabitsList(userEmail);
        if (habits == null || habits.isEmpty()) return;

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        int totalHabits = habits.size();
        int completedHabits = 0;

        for (Habit habit : habits) {
            if (habit != null && dbHelper.isHabitDoneToday(userEmail, habit.getId(), today)) {
                completedHabits++;
            }
        }

        if (completedHabits > 0 && completedHabits == totalHabits) {
            String lastRewardDate = prefs.getString("reward_last_shown_date_" + userEmail, "");
            if (today.equals(lastRewardDate)) return;

            showRewardDialog();
            prefs.edit().putString("reward_last_shown_date_" + userEmail, today).apply();
        }
    }

    private void showRewardDialog() {
        if (!(context instanceof Activity)) return;
        Activity activity = (Activity) context;
        if (activity.isFinishing() || activity.isDestroyed()) return;

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(24), dp(24), dp(24));
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(Color.parseColor("#FFF8F2"));

        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(dp(140), dp(140));
        imgParams.bottomMargin = dp(16);
        imageView.setLayoutParams(imgParams);

        boolean showDuck = random.nextBoolean();
        imageView.setImageResource(showDuck ? R.drawable.duck : R.drawable.dinoyawwr);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        TextView title = new TextView(context);
        title.setText("🎉 Reward unlocked!");
        title.setTextColor(Color.parseColor("#1F1A17"));
        title.setTextSize(22f);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(8));

        TextView message = new TextView(context);
        message.setText("You completed 100% of your habits today. Amazing work!");
        message.setTextColor(Color.parseColor("#6F5B5B"));
        message.setTextSize(14f);
        message.setGravity(Gravity.CENTER);
        message.setPadding(0, 0, 0, dp(20));

        Button ok = new Button(context);
        ok.setText("Yay!");
        ok.setTextColor(Color.WHITE);
        ok.setBackgroundColor(Color.parseColor("#2D6A4F"));
        ok.setAllCaps(false);
        ok.setOnClickListener(v -> dialog.dismiss());

        root.addView(imageView);
        root.addView(title);
        root.addView(message);
        root.addView(ok);

        dialog.setContentView(root);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }

    private int dp(int value) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }
}