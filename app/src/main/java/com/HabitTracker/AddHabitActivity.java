package com.HabitTracker;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddHabitActivity extends AppCompatActivity {

    private TextInputEditText etHabitName, etHabitDesc;
    private LinearLayout cardHealth, cardStudy, cardMind, cardSoul, cardWork;
    private LinearLayout cardDaily, cardWeekly;
    private Button btnSave;

    private String selectedCategory  = "Health";
    private String selectedFrequency = "Daily";

    // ✅ Unique color per category
    private static final String COLOR_HEALTH  = "#4A7C59"; // Green
    private static final String COLOR_STUDY   = "#4A6FA5"; // Blue
    private static final String COLOR_MIND    = "#7B5EA7"; // Purple
    private static final String COLOR_SOUL    = "#C26D8A"; // Pink
    private static final String COLOR_WORK    = "#D4813A"; // Orange
    private static final String COLOR_UNSEL   = "#F0EBE6"; // Unselected bg
    private static final String COLOR_TEXT_DARK  = "#1F1A17";
    private static final String COLOR_TEXT_WHITE = "#FFFFFF";

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        dbHelper = new DatabaseHelper(this);

        bindViews();
        setupCategoryCards();
        setupFrequencyCards();
        setupSaveButton();

        // Set initial selected state
        selectCategory(cardHealth, "Health", COLOR_HEALTH);
        selectFrequency(cardDaily, "Daily");

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void bindViews() {
        etHabitName = findViewById(R.id.et_habit_name);
        etHabitDesc = findViewById(R.id.et_habit_desc);
        cardHealth  = findViewById(R.id.card_health);
        cardStudy   = findViewById(R.id.card_study);
        cardMind    = findViewById(R.id.card_mind);
        cardSoul    = findViewById(R.id.card_soul);
        cardWork    = findViewById(R.id.card_Work);
        cardDaily   = findViewById(R.id.card_daily);
        cardWeekly  = findViewById(R.id.card_weekly);
        btnSave     = findViewById(R.id.btn_save_habit);
    }

    private void setupCategoryCards() {
        cardHealth.setOnClickListener(v -> selectCategory(cardHealth, "Health", COLOR_HEALTH));
        cardStudy.setOnClickListener(v  -> selectCategory(cardStudy,  "Study",  COLOR_STUDY));
        cardMind.setOnClickListener(v   -> selectCategory(cardMind,   "Mind",   COLOR_MIND));
        cardSoul.setOnClickListener(v   -> selectCategory(cardSoul,   "Soul",   COLOR_SOUL));
        cardWork.setOnClickListener(v   -> selectCategory(cardWork,   "Work",   COLOR_WORK));
    }

    private void selectCategory(LinearLayout selected, String name, String color) {
        // Reset ALL cards to unselected (gray bg, dark text)
        resetCategoryCard(cardHealth);
        resetCategoryCard(cardStudy);
        resetCategoryCard(cardMind);
        resetCategoryCard(cardSoul);
        resetCategoryCard(cardWork);

        // ✅ Highlight selected card with its unique color
        selected.setBackgroundColor(Color.parseColor(color));
        updateCardTextColor(selected, COLOR_TEXT_WHITE);

        // Bounce animation
        selected.animate()
                .scaleX(1.08f).scaleY(1.08f).setDuration(120)
                .withEndAction(() ->
                        selected.animate()
                                .scaleX(1f).scaleY(1f)
                                .setDuration(100).start()
                ).start();

        selectedCategory = name;
    }

    private void resetCategoryCard(LinearLayout card) {
        card.setBackgroundResource(R.drawable.card_category_unselected);
        updateCardTextColor(card, COLOR_TEXT_DARK);
    }

    private void setupFrequencyCards() {
        cardDaily.setOnClickListener(v  -> selectFrequency(cardDaily,  "Daily"));
        cardWeekly.setOnClickListener(v -> selectFrequency(cardWeekly, "Weekly"));
    }

    private void selectFrequency(LinearLayout selected, String freq) {
        // Reset both
        cardDaily.setBackgroundResource(R.drawable.card_category_unselected);
        cardWeekly.setBackgroundResource(R.drawable.card_category_unselected);
        updateCardTextColor(cardDaily,  COLOR_TEXT_DARK);
        updateCardTextColor(cardWeekly, COLOR_TEXT_DARK);

        // Highlight selected with green
        selected.setBackgroundColor(Color.parseColor("#4A7C59"));
        updateCardTextColor(selected, COLOR_TEXT_WHITE);

        // Bounce animation
        selected.animate()
                .scaleX(1.08f).scaleY(1.08f).setDuration(120)
                .withEndAction(() ->
                        selected.animate()
                                .scaleX(1f).scaleY(1f)
                                .setDuration(100).start()
                ).start();

        selectedFrequency = freq;
    }

    private void updateCardTextColor(LinearLayout card, String colorHex) {
        for (int i = 0; i < card.getChildCount(); i++) {
            if (card.getChildAt(i) instanceof TextView) {
                ((TextView) card.getChildAt(i))
                        .setTextColor(Color.parseColor(colorHex));
            }
        }
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            String name = etHabitName.getText().toString().trim();
            String desc = etHabitDesc.getText().toString().trim();

            if (name.isEmpty()) {
                etHabitName.setError("Please enter a habit name");
                etHabitName.requestFocus();
                return;
            }

            String today = new SimpleDateFormat(
                    "yyyy-MM-dd", Locale.getDefault()
            ).format(new Date());

            dbHelper.addHabit(name, desc, selectedCategory, selectedFrequency, today);

            Toast.makeText(this,
                    "Habit saved! Keep it up 🎯", Toast.LENGTH_SHORT).show();

            setResult(RESULT_OK);
            finish();
        });
    }
}