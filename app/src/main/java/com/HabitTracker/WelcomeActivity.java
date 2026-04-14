package com.HabitTracker;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private ImageView ivWelcomeAvatar;
    private TextView tvWelcomeName;
    private TextView tvWelcomeBirthday;
    private TextView tvWelcomeGender;
    private TextView tvWelcomeGoal;
    private LinearLayout llWelcomeHabits;
    private TextView tvNoHabits;
    private LinearLayout llCompanionSheet;
    private ImageView ivCompanionAnimal;
    private TextView tvCompanionLabel;
    private ImageButton btnDismissCompanion;

    private ObjectAnimator bobbingAnimator;

    private static final String PREFS = "HabitKit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getOnBackPressedDispatcher().addCallback(this,
                new androidx.activity.OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                    }
                }
        );

        bindViews();
        populateProfile();
        setupDashboardButton();

        new Handler(Looper.getMainLooper()).postDelayed(this::slideCompanionUp, 800L);
    }

    private void bindViews() {
        ivWelcomeAvatar = findViewById(R.id.iv_welcome_avatar);
        tvWelcomeName = findViewById(R.id.tv_welcome_name);
        tvWelcomeBirthday = findViewById(R.id.tv_welcome_birthday);
        tvWelcomeGender = findViewById(R.id.tv_welcome_gender);
        tvWelcomeGoal = findViewById(R.id.tv_welcome_goal);
        llWelcomeHabits = findViewById(R.id.ll_welcome_habits);
        tvNoHabits = findViewById(R.id.tv_no_habits);
        llCompanionSheet = findViewById(R.id.ll_companion_sheet);
        ivCompanionAnimal = findViewById(R.id.iv_companion_animal);
        tvCompanionLabel = findViewById(R.id.tv_companion_label);
        btnDismissCompanion = findViewById(R.id.btn_dismiss_companion);
    }

    private void populateProfile() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        String name = prefs.getString("name", "Hero");
        tvWelcomeName.setText(name);

        String birthday = prefs.getString("birthday", "—");
        tvWelcomeBirthday.setText(birthday.isEmpty() ? "—" : birthday);

        String gender = prefs.getString("gender", "—");
        tvWelcomeGender.setText(gender.isEmpty() ? "—" : gender);

        String goal = prefs.getString("goal", "—");
        tvWelcomeGoal.setText(goal.isEmpty() ? "—" : goal);

        String avatarRes = prefs.getString("avatar_res_name", "");
        String avatarUri = prefs.getString("avatar_gallery_uri", "");

        if (avatarUri != null && !avatarUri.isEmpty()) {
            try {
                Uri uri = Uri.parse(avatarUri);
                ivWelcomeAvatar.setImageURI(uri);
            } catch (Exception e) {
                ivWelcomeAvatar.setImageResource(R.drawable.turtle);
            }
        } else if (avatarRes != null && !avatarRes.isEmpty()) {
            int resId = getResources().getIdentifier(avatarRes, "drawable", getPackageName());
            if (resId != 0) ivWelcomeAvatar.setImageResource(resId);
        }

        setCompanionFromAvatar(avatarRes);

        String habitsCsv = prefs.getString("habit_names", "");
        if (habitsCsv == null || habitsCsv.isEmpty()) {
            tvNoHabits.setVisibility(View.VISIBLE);
        } else {
            String[] habits = habitsCsv.split(",");
            for (String habit : habits) {
                if (!habit.trim().isEmpty()) {
                    addHabitRow(habit.trim());
                }
            }
        }
    }

    private void setCompanionFromAvatar(String avatarResName) {
        if (avatarResName == null || avatarResName.isEmpty()) {
            tvCompanionLabel.setText("🐾 Your Buddy");
            ivCompanionAnimal.setImageResource(R.drawable.turtle);
            return;
        }

        int resId = getResources().getIdentifier(avatarResName, "drawable", getPackageName());
        if (resId != 0) {
            ivCompanionAnimal.setImageResource(resId);
        }

        if ("turtle".equals(avatarResName)) tvCompanionLabel.setText("🐾 Shellshock");
        else if ("rabbit".equals(avatarResName)) tvCompanionLabel.setText("🐾 Hopster");
        else if ("sloth".equals(avatarResName)) tvCompanionLabel.setText("🐾 SlowMoKing");
        else if ("penguin".equals(avatarResName)) tvCompanionLabel.setText("🐾 IceWaddle");
        else if ("duck".equals(avatarResName)) tvCompanionLabel.setText("🐾 QuackAttack");
        else if ("cow".equals(avatarResName)) tvCompanionLabel.setText("🐾 Moooo");
        else if ("cat".equals(avatarResName)) tvCompanionLabel.setText("🐾 BowMeow");
        else if ("squirrel".equals(avatarResName)) tvCompanionLabel.setText("🐾 ChatterChip");
        else if ("dogoo".equals(avatarResName)) tvCompanionLabel.setText("🐾 FluffBloom");
        else if ("pig".equals(avatarResName)) tvCompanionLabel.setText("🐾 OinkJoy");
        else if ("mouse".equals(avatarResName)) tvCompanionLabel.setText("🐾 CheeseBelle");
        else if ("camel".equals(avatarResName)) tvCompanionLabel.setText("🐾 ToffeeTongue");
    }

    private void addHabitRow(String habitName) {
        TextView chip = new TextView(this);
        chip.setText("✅  " + habitName);
        chip.setTextSize(13f);
        chip.setTextColor(getResources().getColor(R.color.text_primary, getTheme()));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 10);
        chip.setLayoutParams(params);

        llWelcomeHabits.addView(chip);
    }

    private void setupDashboardButton() {
        findViewById(R.id.btn_go_dashboard).setOnClickListener(v -> {
            getSharedPreferences(PREFS, MODE_PRIVATE)
                    .edit()
                    .putBoolean("onboarding_done", false)
                    .apply();

            Intent intent = new Intent(this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void slideCompanionUp() {
        llCompanionSheet.setVisibility(View.VISIBLE);
        llCompanionSheet.animate()
                .translationY(0f)
                .setDuration(480)
                .setInterpolator(new android.view.animation.OvershootInterpolator(1.4f))
                .withEndAction(this::startBobbingAnimation)
                .start();

        btnDismissCompanion.setOnClickListener(v -> slideCompanionDown());
    }

    private void slideCompanionDown() {
        stopBobbingAnimation();
        float offScreen = llCompanionSheet.getHeight() + 60f;
        llCompanionSheet.animate()
                .translationY(offScreen)
                .setDuration(350)
                .setInterpolator(new android.view.animation.AccelerateInterpolator(1.6f))
                .withEndAction(() -> llCompanionSheet.setVisibility(View.GONE))
                .start();
    }

    private void startBobbingAnimation() {
        bobbingAnimator = ObjectAnimator.ofFloat(ivCompanionAnimal, "translationY", 0f, -14f);
        bobbingAnimator.setRepeatMode(ValueAnimator.REVERSE);
        bobbingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        bobbingAnimator.setDuration(850);
        bobbingAnimator.setInterpolator(t -> (float) Math.sin(t * Math.PI));
        bobbingAnimator.start();
    }

    private void stopBobbingAnimation() {
        if (bobbingAnimator != null) {
            bobbingAnimator.cancel();
            ivCompanionAnimal.setTranslationY(0f);
            bobbingAnimator = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bobbingAnimator != null && bobbingAnimator.isRunning()) {
            bobbingAnimator.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bobbingAnimator != null && bobbingAnimator.isPaused()) {
            bobbingAnimator.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBobbingAnimation();
    }
}