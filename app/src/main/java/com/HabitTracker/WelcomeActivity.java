package com.HabitTracker;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

/**
 * WelcomeActivity
 * ─────────────────────────────────────────────────────────────────
 * Profile preview screen shown after AvatarPickerActivity.
 *
 * • Reads all profile data from SharedPreferences ("HabitKit")
 * • Populates avatar, name, birthday, gender, goal, habits list
 * • After 800ms: animal companion sheet slides up from the bottom
 * • Companion bobs gently while visible
 * • X button slides the sheet back down and dismisses it
 * • "Let's Go" button navigates to MainActivity
 * ─────────────────────────────────────────────────────────────────
 */
public class WelcomeActivity extends AppCompatActivity {

    // ── Views ────────────────────────────────────────────────────────
    private ImageView     ivWelcomeAvatar;
    private TextView      tvWelcomeName;
    private TextView      tvWelcomeBirthday;
    private TextView      tvWelcomeGender;
    private TextView      tvWelcomeGoal;
    private LinearLayout  llWelcomeHabits;
    private TextView      tvNoHabits;
    private LinearLayout  llCompanionSheet;
    private ImageView     ivCompanionAnimal;
    private TextView      tvCompanionLabel;
    private ImageButton   btnDismissCompanion;

    // ── Bobbing animator ref (so we can cancel it cleanly) ───────────
    private ObjectAnimator bobbingAnimator;

    // ── SharedPrefs keys (must match ProfileSetupActivity) ──────────
    private static final String PREFS         = "HabitKit";
    private static final String KEY_NAME      = "name";
    private static final String KEY_BIRTHDAY  = "birthday";
    private static final String KEY_GENDER    = "gender";        // saved by chip selection
    private static final String KEY_GOAL      = "goal";          // saved by chip selection
    private static final String KEY_HABITS    = "habit_names";
    private static final String KEY_AVATAR_RES= "avatar_res_name";
    private static final String KEY_AVATAR_URI= "avatar_gallery_uri";

    // Companion name map — matches drawable res name → cute nickname
    private static final String[][] COMPANION_NAMES = {
            {"turtle",   "Shellshock"},
            {"rabbit",   "Hopster"},
            {"sloth",    "SlowMoKing"},
            {"penguin",  "IceWaddle"},
            {"duck",     "QuackAttack"},
            {"cow",      "Moooo"},
            {"cat",      "BowMeow"},
            {"squirrel", "ChatterChip"},
            {"dogoo",    "FluffBloom"},
            {"pig",      "OinkJoy"},
            {"mouse",    "CheeseBelle"},
            {"camel",    "ToffeeTongue"},
    };

    // ════════════════════════════════════════════════════════════════
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getOnBackPressedDispatcher().addCallback(this,
                new androidx.activity.OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        // Do nothing — back is disabled on Welcome screen
                    }
                }
        );

        bindViews();
        populateProfile();
        setupDashboardButton();

        // Slide companion sheet up after short delay
        new Handler(Looper.getMainLooper()).postDelayed(
                this::slideCompanionUp,
                800L
        );
    }

    // ────────────────────────────────────────────────────────────────
    // Bind views
    // ────────────────────────────────────────────────────────────────
    private void bindViews() {
        ivWelcomeAvatar      = findViewById(R.id.iv_welcome_avatar);
        tvWelcomeName        = findViewById(R.id.tv_welcome_name);
        tvWelcomeBirthday    = findViewById(R.id.tv_welcome_birthday);
        tvWelcomeGender      = findViewById(R.id.tv_welcome_gender);
        tvWelcomeGoal        = findViewById(R.id.tv_welcome_goal);
        llWelcomeHabits      = findViewById(R.id.ll_welcome_habits);
        tvNoHabits           = findViewById(R.id.tv_no_habits);
        llCompanionSheet     = findViewById(R.id.ll_companion_sheet);
        ivCompanionAnimal    = findViewById(R.id.iv_companion_animal);
        tvCompanionLabel     = findViewById(R.id.tv_companion_label);
        btnDismissCompanion  = findViewById(R.id.btn_dismiss_companion);
    }

    // ────────────────────────────────────────────────────────────────
    // Read SharedPrefs and fill every view
    // ────────────────────────────────────────────────────────────────
    private void populateProfile() {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        // ── Name ─────────────────────────────────────────────────────
        String name = prefs.getString(KEY_NAME, "Hero");
        tvWelcomeName.setText(name);

        // ── Birthday ─────────────────────────────────────────────────
        String birthday = prefs.getString(KEY_BIRTHDAY, "—");
        tvWelcomeBirthday.setText(birthday.isEmpty() ? "—" : birthday);

        // ── Gender ───────────────────────────────────────────────────
        String gender = prefs.getString(KEY_GENDER, "—");
        tvWelcomeGender.setText(gender.isEmpty() ? "—" : gender);

        // ── Goal ─────────────────────────────────────────────────────
        String goal = prefs.getString(KEY_GOAL, "—");
        tvWelcomeGoal.setText(goal.isEmpty() ? "—" : goal);

        // ── Avatar ───────────────────────────────────────────────────
        String avatarRes = prefs.getString(KEY_AVATAR_RES, "");
        String avatarUri = prefs.getString(KEY_AVATAR_URI, "");

        if (!avatarUri.isEmpty()) {
            ivWelcomeAvatar.setImageURI(Uri.parse(avatarUri));
        } else if (!avatarRes.isEmpty()) {
            int resId = getResources().getIdentifier(
                    avatarRes, "drawable", getPackageName()
            );
            if (resId != 0) ivWelcomeAvatar.setImageResource(resId);
        }

        // ── Companion animal mirrors the chosen avatar ────────────────
        setCompanionFromAvatar(avatarRes);

        // ── Habits ───────────────────────────────────────────────────
        String habitsCsv = prefs.getString(KEY_HABITS, "");
        if (habitsCsv.isEmpty()) {
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

    // ────────────────────────────────────────────────────────────────
    // Set the companion animal to match the picked avatar
    // ────────────────────────────────────────────────────────────────
    private void setCompanionFromAvatar(String avatarResName) {
        if (avatarResName == null || avatarResName.isEmpty()) return;

        int resId = getResources().getIdentifier(
                avatarResName, "drawable", getPackageName()
        );
        if (resId != 0) {
            ivCompanionAnimal.setImageResource(resId);
        }

        // Update companion name label
        for (String[] pair : COMPANION_NAMES) {
            if (pair[0].equals(avatarResName)) {
                tvCompanionLabel.setText("🐾 " + pair[1]);
                break;
            }
        }
    }

    // ────────────────────────────────────────────────────────────────
    // Dynamically add a habit row chip to the habits card
    // ────────────────────────────────────────────────────────────────
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

    // ────────────────────────────────────────────────────────────────
    // "Let's Go" → MainActivity
    // ────────────────────────────────────────────────────────────────
    private void setupDashboardButton() {
        findViewById(R.id.btn_go_dashboard).setOnClickListener(v -> {
            // Reset onboarding flag so new user gets the tour
            getSharedPreferences("HabitKit", MODE_PRIVATE)
                    .edit()
                    .putBoolean("onboarding_done", false)
                    .apply();

            Intent intent = new Intent(this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    // ════════════════════════════════════════════════════════════════
    //  COMPANION ANIMATIONS
    // ════════════════════════════════════════════════════════════════

    /**
     * Slides the companion sheet up from below the screen.
     * Uses spring-like overshoot: translates to -20dp past target,
     * then settles to 0.
     */
    private void slideCompanionUp() {
        llCompanionSheet.setVisibility(View.VISIBLE);

        llCompanionSheet.animate()
                .translationY(0f)
                .setDuration(480)
                .setInterpolator(new android.view.animation.OvershootInterpolator(1.4f))
                .withEndAction(this::startBobbingAnimation)
                .start();

        // Wire dismiss button now that sheet is visible
        btnDismissCompanion.setOnClickListener(v -> slideCompanionDown());
    }

    /**
     * Slides the companion sheet back down off-screen when X is tapped.
     */
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

    /**
     * Gentle sine-wave bob on the companion animal image.
     * Runs while the sheet is visible.
     */
    private void startBobbingAnimation() {
        bobbingAnimator = ObjectAnimator.ofFloat(
                ivCompanionAnimal, "translationY", 0f, -14f
        );
        bobbingAnimator.setRepeatMode(ValueAnimator.REVERSE);
        bobbingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        bobbingAnimator.setDuration(850);
        bobbingAnimator.setInterpolator(
                t -> (float) Math.sin(t * Math.PI)
        );
        bobbingAnimator.start();
    }

    private void stopBobbingAnimation() {
        if (bobbingAnimator != null) {
            bobbingAnimator.cancel();
            ivCompanionAnimal.setTranslationY(0f);
            bobbingAnimator = null;
        }
    }

    // ────────────────────────────────────────────────────────────────
    // Lifecycle: pause/resume bobbing to prevent battery drain
    // ────────────────────────────────────────────────────────────────
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