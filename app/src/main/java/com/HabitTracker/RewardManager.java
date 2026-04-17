package com.HabitTracker;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    private final int[] rewardImages = new int[]{
            R.drawable.duck,
            R.drawable.dinoyawwr,
            R.drawable.cow,
            R.drawable.cat,
            R.drawable.camel,
            R.drawable.dogoo,
            R.drawable.frog_logo,
            R.drawable.pig,
            R.drawable.mouse,
            R.drawable.penguin,
            R.drawable.rabbit,
            R.drawable.squirrel,
            R.drawable.sloth,
            R.drawable.turtle,
            R.drawable.ic_animated_hug_frame,
            R.drawable.ic_otter_motivation
    };

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

        if (completedHabits == totalHabits) {
            showRewardDialog();
        }
    }

    private void showRewardDialog() {
        if (!(context instanceof Activity)) return;

        Activity activity = (Activity) context;
        if (activity.isFinishing() || activity.isDestroyed()) return;

        activity.runOnUiThread(() -> {
            Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            LayoutInflater inflater = LayoutInflater.from(activity);
            LinearLayout root = (LinearLayout) inflater.inflate(R.layout.dialog_reward, null, false);

            ImageView imageView = root.findViewById(R.id.ivRewardImage);
            TextView title = root.findViewById(R.id.tvRewardTitle);
            TextView message = root.findViewById(R.id.tvRewardMessage);
            Button ok = root.findViewById(R.id.btnRewardOk);

            int randomImage = rewardImages[random.nextInt(rewardImages.length)];
            imageView.setImageResource(randomImage);

            title.setText("Reward unlocked!");
            message.setText("You completed 100% of your habits today. Amazing work!");

            ok.setOnClickListener(v -> dialog.dismiss());

            dialog.setContentView(root);

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogPopupAnimation;
            }

            dialog.show();

            if (dialog.getWindow() != null) {
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.90f);
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setAttributes(params);
            }
        });
    }
}