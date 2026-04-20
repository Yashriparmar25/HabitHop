package com.HabitTracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JournalActivity extends AppCompatActivity {

    private int waterCount = 0;
    private String selectedMood = "happy";
    private int selectedMoodIndex = 3;

    private final String[] moodIds = {"angry", "tired", "sad", "happy", "excited"};
    private final String[] prompts = {
            "Hey 🐻 Rough day? Let it out here, you're safe.",
            "Hi 🐱 Feeling drained? That's okay. Let's reflect gently.",
            "Hey 🐰 Feeling low? Writing it out brings clarity 💚",
            "Yay 🐶 You did wonderful today! Let's capture it! 🌟",
            "Woohoo 🦊 What a day! Write it all down! 🎉"
    };

    private final String[] dialogTitles = {
            "You seem angry",
            "You look tired",
            "You feel sad",
            "You look happy",
            "You feel excited"
    };

    private final String[] dialogMessages = {
            "It’s okay to feel angry. Take a breath and let it out safely.",
            "You deserve rest. Slow down and be gentle with yourself.",
            "Your feelings matter. Writing can help you feel lighter.",
            "Nice! Keep enjoying the good energy from today.",
            "Amazing energy! Capture this moment in your journal."
    };

    private TextInputEditText etGrateful1, etGrateful2, etGrateful3;
    private TextInputEditText etAffirmation1, etAffirmation2, etAffirmation3;
    private TextInputEditText etWentWell, etImprove, etNotes, etTomorrow;

    private TextView tvAnimalPrompt, tvAnimalSub, tvWaterCount, tvDate;
    private LottieAnimationView lottieAnimal;
    private LinearLayout layoutWater;

    private TextView[] moodViews;

    private DatabaseHelper dbHelper;
    private String currentUserEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        dbHelper = new DatabaseHelper(this);
        currentUserEmail = getSharedPreferences("HabitKit", MODE_PRIVATE)
                .getString("current_user_email", "");

        bindViews();
        setTodayDate();
        setupDayDots();
        setupWaterDrops();
        setupMoodSelection();
        setupSaveButton();
        setupViewEntriesButton();

        lottieAnimal.setAnimation(R.raw.animal_anim);
        lottieAnimal.loop(true);
        lottieAnimal.playAnimation();

        setAnimalPrompt(selectedMoodIndex);
        updateMoodSelection(selectedMoodIndex);
        updateWaterDrops();
    }

    private void bindViews() {
        etGrateful1 = findViewById(R.id.et_grateful1);
        etGrateful2 = findViewById(R.id.et_grateful2);
        etGrateful3 = findViewById(R.id.et_grateful3);

        etAffirmation1 = findViewById(R.id.et_affirmation1);
        etAffirmation2 = findViewById(R.id.et_affirmation2);
        etAffirmation3 = findViewById(R.id.et_affirmation3);

        etWentWell = findViewById(R.id.et_went_well);
        etImprove = findViewById(R.id.et_improve);
        etNotes = findViewById(R.id.et_notes);
        etTomorrow = findViewById(R.id.et_tomorrow);

        tvAnimalPrompt = findViewById(R.id.tv_animal_prompt);
        tvAnimalSub = findViewById(R.id.tv_animal_sub);
        tvWaterCount = findViewById(R.id.tv_water_count);
        tvDate = findViewById(R.id.tv_date);
        lottieAnimal = findViewById(R.id.lottie_animal);
        layoutWater = findViewById(R.id.layout_water);
    }

    private void setTodayDate() {
        String date = new SimpleDateFormat("dd / MM / yyyy", Locale.getDefault()).format(new Date());
        tvDate.setText(date);
    }

    private void setupDayDots() {
        LinearLayout dotsLayout = findViewById(R.id.layout_days);
        dotsLayout.removeAllViews();

        String today = new SimpleDateFormat("EEE", Locale.getDefault()).format(new Date()).toUpperCase();
        String[] days = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        String[] labels = {"S", "M", "T", "W", "T", "F", "S"};

        for (int i = 0; i < 7; i++) {
            LinearLayout dayCol = new LinearLayout(this);
            dayCol.setOrientation(LinearLayout.VERTICAL);
            dayCol.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            dayCol.setLayoutParams(p);

            TextView dot = new TextView(this);
            dot.setText("●");
            dot.setTextSize(16f);
            dot.setTextColor(days[i].equals(today) ? Color.parseColor("#2E7D52") : Color.parseColor("#C8E6C9"));
            dot.setGravity(android.view.Gravity.CENTER);

            TextView label = new TextView(this);
            label.setText(labels[i]);
            label.setTextSize(10f);
            label.setTextColor(Color.parseColor("#5A8A6A"));
            label.setGravity(android.view.Gravity.CENTER);

            dayCol.addView(dot);
            dayCol.addView(label);
            dotsLayout.addView(dayCol);
        }
    }

    private void setupWaterDrops() {
        layoutWater.removeAllViews();

        for (int i = 1; i <= 8; i++) {
            final int idx = i;
            TextView drop = new TextView(this);
            drop.setText("💧");
            drop.setTextSize(22f);
            drop.setAlpha(0.3f);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            drop.setLayoutParams(lp);
            drop.setGravity(android.view.Gravity.CENTER);

            drop.setOnClickListener(v -> {
                waterCount = idx;
                updateWaterDrops();
            });

            layoutWater.addView(drop);
        }
    }

    private void updateWaterDrops() {
        for (int i = 0; i < layoutWater.getChildCount(); i++) {
            TextView drop = (TextView) layoutWater.getChildAt(i);
            drop.setAlpha(i < waterCount ? 1f : 0.3f);
        }
        tvWaterCount.setText(waterCount + " / 8 glasses");
    }

    private void setupMoodSelection() {
        int[] moodViewIds = {
                R.id.mood_angry, R.id.mood_tired, R.id.mood_sad,
                R.id.mood_happy, R.id.mood_excited
        };

        moodViews = new TextView[moodViewIds.length];

        for (int i = 0; i < moodViewIds.length; i++) {
            final int idx = i;
            TextView moodView = findViewById(moodViewIds[i]);
            moodViews[i] = moodView;

            moodView.setClickable(true);
            moodView.setFocusable(true);

            moodView.setOnClickListener(v -> {
                selectedMood = moodIds[idx];
                selectedMoodIndex = idx;
                updateMoodSelection(idx);
                setAnimalPrompt(idx);
                showMoodDialog(idx);
            });
        }
    }

    private void updateMoodSelection(int selectedIndex) {
        for (int i = 0; i < moodViews.length; i++) {
            TextView moodView = moodViews[i];
            if (i == selectedIndex) {
                moodView.setBackgroundResource(R.drawable.bg_mood_selected);
                moodView.setAlpha(1f);
                moodView.setScaleX(1.06f);
                moodView.setScaleY(1.06f);
                moodView.setTextSize(30f);
            } else {
                moodView.setBackgroundResource(R.drawable.bg_mood_unselected);
                moodView.setAlpha(0.68f);
                moodView.setScaleX(1f);
                moodView.setScaleY(1f);
                moodView.setTextSize(28f);
            }
        }
    }

    private void setAnimalPrompt(int moodIndex) {
        tvAnimalPrompt.setText(prompts[moodIndex]);

        String[] subs = {
                "It's okay to feel this. Let's process it ",
                "Rest is growth too. Be gentle with yourself 💚",
                "Your feelings are valid. Write freely 🌸",
                "Let's capture this beautiful day! ✨",
                "Channel that energy into your journal! 🚀"
        };

        tvAnimalSub.setText(subs[moodIndex]);
        lottieAnimal.playAnimation();
    }

    private void showMoodDialog(int moodIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.dialog_mood_message, null);
        builder.setView(customView);

        TextView tvTitle = customView.findViewById(R.id.tv_dialog_title);
        TextView tvMessage = customView.findViewById(R.id.tv_dialog_message);
        LottieAnimationView dialogAnim = customView.findViewById(R.id.dialog_lottie);

        tvTitle.setText(dialogTitles[moodIndex]);
        tvMessage.setText(dialogMessages[moodIndex]);

        dialogAnim.setAnimation(R.raw.animal_anim);
        dialogAnim.loop(true);
        dialogAnim.playAnimation();

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private void setupSaveButton() {
        findViewById(R.id.btn_save_journal).setOnClickListener(v -> {
            if (currentUserEmail == null || currentUserEmail.isEmpty()) {
                Toast.makeText(this, "No user found. Please log in again.", Toast.LENGTH_SHORT).show();
                return;
            }

            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String now = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

            long result = dbHelper.saveJournalEntry(
                    currentUserEmail,
                    today,
                    now,
                    selectedMood,
                    getText(etWentWell),
                    getText(etGrateful1),
                    getText(etGrateful2),
                    getText(etGrateful3),
                    getText(etAffirmation1),
                    getText(etAffirmation2),
                    getText(etAffirmation3),
                    getText(etWentWell),
                    getText(etImprove),
                    getText(etNotes),
                    getText(etTomorrow),
                    waterCount
            );

            if (result != -1) {
                Toast.makeText(this, "Entry saved! Keep growing 💖", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to save entry", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViewEntriesButton() {
        findViewById(R.id.btn_view_entries).setOnClickListener(v -> {
            Toast.makeText(this, "Coming soon 💖", Toast.LENGTH_SHORT).show();
        });
    }
}