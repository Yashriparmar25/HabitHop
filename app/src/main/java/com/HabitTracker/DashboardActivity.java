package com.HabitTracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvUserName;
    private ImageView profileImage;

    private LinearLayout navHome, navJournal, navAdd, navReminders, navProfile;
    private LinearLayout cardSleep, cardMood;
    private TextView tvSleepCardTitle, tvSleepCardSubtitle, tvSleepCardMeta;
    private TextView tvMoodCardTitle, tvMoodCardSubtitle;

    private TextView tvStreakCount, tvStreakMessage, tvTasksDone, tvTasksDue;
    private TextView dayMon, dayTue, dayWed, dayThu, dayFri, daySat, daySun;

    private TextView tvProgressEmoji, tvProgressMessage, tvProgressCount;
    private ProgressBar progressHabits;

    private RecyclerView recyclerHabits;
    private HabitAdapter habitAdapter;
    private DatabaseHelper dbHelper;

    private Button btnWriteMore;
    private SharedPreferences prefs;
    private String currentUserEmail = "";

    private FrameLayout onboardingOverlay;
    private int currentStep = 0;

    private final int[] stepViewIds = {
            R.id.profileImage,
            R.id.navHome,
            R.id.navJournal,
            R.id.navAdd,
            R.id.navReminders,
            R.id.navProfile
    };

    private final String[] stepTitles = {
            "Your Profile 👤",
            "Home Dashboard 🏠",
            "Journal ✍️",
            "Add a Habit ➕",
            "Reminders 🔔",
            "Profile Settings ⚙️"
    };

    private final String[] stepDescs = {
            "Tap your avatar to view and edit your profile, change your photo, and update personal info.",
            "This is your home! See your daily streak, sleep, mood, and all your habits at a glance.",
            "Write your daily journal entries, express your thoughts, and track how you feel each day.",
            "Tap the + button anytime to add a new habit you want to build and track daily.",
            "Set smart reminders so you never forget to complete your habits every day.",
            "View your progress stats, edit your profile details, and manage your account here."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        prefs = getSharedPreferences("HabitKit", MODE_PRIVATE);
        currentUserEmail = prefs.getString("current_user_email", "");
        dbHelper = new DatabaseHelper(this);

        bindViews();

        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        loadUserData();
        setupNavigation();
        setupSleepCard();
        setupMoodCard();
        setupHabitsRecycler();
        setupWriteMoreButton();

        loadHabits();
        updateProgress();
        updateStreak();
        updateSleepCard();
        updateMoodCard();

        if (!prefs.getBoolean("onboarding_done", false)) {
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(this::startOnboarding, 800L);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUserEmail = prefs.getString("current_user_email", "");
        loadUserData();
        loadHabits();
        updateProgress();
        updateStreak();
        updateSleepCard();
        updateMoodCard();
    }

    private String key(String base) {
        return base + "_" + currentUserEmail;
    }

    private void bindViews() {
        tvUserName = findViewById(R.id.tvUserName);
        profileImage = findViewById(R.id.profileImage);

        navHome = findViewById(R.id.navHome);
        navJournal = findViewById(R.id.navJournal);
        navAdd = findViewById(R.id.navAdd);
        navReminders = findViewById(R.id.navReminders);
        navProfile = findViewById(R.id.navProfile);

        cardSleep = findViewById(R.id.cardSleep);
        cardMood = findViewById(R.id.cardMood);

        tvSleepCardTitle = findViewById(R.id.tvSleepCardTitle);
        tvSleepCardSubtitle = findViewById(R.id.tvSleepCardSubtitle);
        tvSleepCardMeta = findViewById(R.id.tvSleepCardMeta);

        tvMoodCardTitle = findViewById(R.id.tvMoodCardTitle);
        tvMoodCardSubtitle = findViewById(R.id.tvMoodCardSubtitle);

        tvStreakCount = findViewById(R.id.tvStreakCount);
        tvStreakMessage = findViewById(R.id.tvStreakMessage);
        tvTasksDone = findViewById(R.id.tvTasksDone);
        tvTasksDue = findViewById(R.id.tvTasksDue);

        dayMon = findViewById(R.id.dayMon);
        dayTue = findViewById(R.id.dayTue);
        dayWed = findViewById(R.id.dayWed);
        dayThu = findViewById(R.id.dayThu);
        dayFri = findViewById(R.id.dayFri);
        daySat = findViewById(R.id.daySat);
        daySun = findViewById(R.id.daySun);

        tvProgressEmoji = findViewById(R.id.tvProgressEmoji);
        tvProgressMessage = findViewById(R.id.tvProgressMessage);
        tvProgressCount = findViewById(R.id.tvProgressCount);
        progressHabits = findViewById(R.id.progressHabits);

        recyclerHabits = findViewById(R.id.recyclerHabits);
        btnWriteMore = findViewById(R.id.btnWriteMore);
    }

    private void setupWriteMoreButton() {
        if (btnWriteMore != null) {
            btnWriteMore.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, JournalActivity.class)));
        }
    }

    private void setupSleepCard() {
        if (cardSleep != null) {
            cardSleep.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, SleepTrackerActivity.class)));
        }
    }

    private void setupMoodCard() {
        if (cardMood != null) {
            cardMood.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, MoodTrackerActivity.class)));
        }
    }

    private void updateSleepCard() {
        String lastDate = prefs.getString(key("sleep_last_date"), "");
        String lastDuration = prefs.getString(key("sleep_last_duration"), "");
        String lastQuality = prefs.getString(key("sleep_last_quality"), "");
        String lastBed = prefs.getString(key("sleep_last_bed"), "");
        String lastWake = prefs.getString(key("sleep_last_wake"), "");

        boolean hasSleepRecord = !lastDate.isEmpty() || !lastDuration.isEmpty();

        if (!hasSleepRecord) {
            tvSleepCardTitle.setText("Track sleep");
            tvSleepCardSubtitle.setText("Start your first sleep session");
            tvSleepCardMeta.setText("Tap to open sleep tracker");
            return;
        }

        tvSleepCardTitle.setText("Last sleep");

        String subtitle = lastDuration.isEmpty() ? "Recent sleep session" : lastDuration;
        if (!lastQuality.isEmpty()) subtitle += " • " + lastQuality;
        tvSleepCardSubtitle.setText(subtitle);

        StringBuilder meta = new StringBuilder();
        if (!lastDate.isEmpty()) meta.append(lastDate);
        if (!lastBed.isEmpty() || !lastWake.isEmpty()) {
            if (meta.length() > 0) meta.append(" • ");
            meta.append(lastBed).append(" → ").append(lastWake);
        }
        tvSleepCardMeta.setText(meta.length() == 0 ? "Tap to view details" : meta.toString());
    }

    private void updateMoodCard() {
        String mood = prefs.getString(key("latest_mood_name"), "");
        String moodNote = prefs.getString(key("latest_mood_note"), "");

        if (mood == null || mood.isEmpty()) {
            tvMoodCardTitle.setText("Track mood");
            tvMoodCardSubtitle.setText("How are you feeling today?");
            return;
        }

        tvMoodCardTitle.setText(mood);

        if (moodNote == null || moodNote.isEmpty()) {
            tvMoodCardSubtitle.setText("Latest mood entry");
        } else {
            tvMoodCardSubtitle.setText(moodNote);
        }
    }

    private void loadHabits() {
        List<Habit> habits = dbHelper.getAllHabitsList(currentUserEmail);
        if (habitAdapter != null) {
            habitAdapter.updateHabits(habits);
        }
    }

    private void updateProgress() {
        List<Habit> habits = dbHelper.getAllHabitsList(currentUserEmail);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new java.util.Date());

        int total = habits.size();
        int done = 0;
        for (Habit h : habits) {
            if (dbHelper.isHabitDoneToday(currentUserEmail, h.getId(), today)) done++;
        }

        tvProgressCount.setText(done + "/" + total + " done");
        int percent = total == 0 ? 0 : (done * 100) / total;
        progressHabits.setProgress(percent);

        String emoji, message;
        if (percent == 0) {
            emoji = "😴";
            message = "Wake up! Let's start your habits!";
        } else if (percent <= 25) {
            emoji = "😐";
            message = "Just getting started...";
        } else if (percent <= 50) {
            emoji = "🙂";
            message = "Getting there, keep going!";
        } else if (percent <= 75) {
            emoji = "😊";
            message = "You're doing great!";
        } else if (percent < 100) {
            emoji = "😄";
            message = "Almost there, don't stop now!";
        } else {
            emoji = "🥳";
            message = "All done! You're amazing!";
        }

        tvProgressEmoji.setText(emoji);
        tvProgressMessage.setText(message);
    }

    private void updateStreak() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(Calendar.getInstance().getTime());

        List<Habit> habits = dbHelper.getAllHabitsList(currentUserEmail);
        if (habits == null) habits = new ArrayList<>();

        int total = habits.size();
        int done = 0;

        for (Habit h : habits) {
            if (dbHelper.isHabitDoneToday(currentUserEmail, h.getId(), today)) {
                done++;
            }
        }

        tvTasksDone.setText(String.valueOf(done));
        tvTasksDue.setText(String.valueOf(total - done));

        boolean allCompleted = total > 0 && done == total;

        if (allCompleted) {
            int streak = dbHelper.getDailyCompletionStreak(currentUserEmail);
            tvStreakCount.setText(String.valueOf(streak));

            if (streak == 0) tvStreakMessage.setText("Start your streak today! 💪");
            else if (streak < 3) tvStreakMessage.setText("Good start, keep going!");
            else if (streak < 7) tvStreakMessage.setText("You are doing great! 🔥");
            else tvStreakMessage.setText("Unstoppable! 🚀");
        } else {
            tvStreakCount.setText("0");
            tvStreakMessage.setText("Complete all habits today to start your streak.");
        }

        TextView[] dayViews = {dayMon, dayTue, dayWed, dayThu, dayFri, daySat, daySun};
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        for (int i = 0; i < 7; i++) {
            String dateStr = sdf.format(cal.getTime());
            boolean isToday = dateStr.equals(today);
            boolean isPast = cal.getTime().before(Calendar.getInstance().getTime()) && !isToday;
            int status = dbHelper.getDailyCompletionStatus(currentUserEmail, dateStr);
            TextView tv = dayViews[i];

            if (status == 2) {
                tv.setText("✓");
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundResource(R.drawable.day_completed_bg);
            } else if (isToday) {
                tv.setText("👀");
                tv.setBackgroundResource(R.drawable.day_today_bg);
            } else if (isPast && status == 1) {
                tv.setText("");
                tv.setTextColor(Color.parseColor("#F4A300"));
                tv.setBackgroundResource(R.drawable.day_pending_bg);
            } else {
                tv.setText("");
                tv.setBackgroundResource(R.drawable.day_pending_bg);
            }
            cal.add(Calendar.DATE, 1);
        }
    }

    private void setupHabitsRecycler() {
        habitAdapter = new HabitAdapter(
                this,
                new ArrayList<>(),
                currentUserEmail,
                () -> {
                    updateProgress();
                    updateStreak();
                }
        );
        recyclerHabits.setLayoutManager(new LinearLayoutManager(this));
        recyclerHabits.setAdapter(habitAdapter);
    }

    private void loadUserData() {
        String name = dbHelper.getUserName(currentUserEmail);
        tvUserName.setText("Hi, " + name + " 👋");

        String avatarRes = prefs.getString("avatar_res_name", "");
        String avatarUri = prefs.getString("avatar_gallery_uri", "");

        if (!avatarUri.isEmpty()) {
            profileImage.setImageURI(Uri.parse(avatarUri));
        } else if (!avatarRes.isEmpty()) {
            int resId = getResources().getIdentifier(avatarRes, "drawable", getPackageName());
            if (resId != 0) profileImage.setImageResource(resId);
        }

        profileImage.post(() -> {
            profileImage.setClipToOutline(true);
            profileImage.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, android.graphics.Outline outline) {
                    outline.setOval(0, 0, view.getWidth(), view.getHeight());
                }
            });
        });
    }

    private void setupNavigation() {
        navAdd.setOnClickListener(v ->
                startActivityForResult(new Intent(this, AddHabitActivity.class), 100));
        navJournal.setOnClickListener(v ->
                startActivity(new Intent(this, JournalActivity.class)));
        navReminders.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, RemindersActivity.class)));
        navProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void startOnboarding() {
        currentStep = 0;
        onboardingOverlay = new FrameLayout(this);
        onboardingOverlay.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        onboardingOverlay.setClickable(true);
        ViewGroup root = findViewById(android.R.id.content);
        root.addView(onboardingOverlay);
        showStep(currentStep);
    }

    private void showStep(int step) {
        onboardingOverlay.removeAllViews();

        View targetView = findViewById(stepViewIds[step]);
        if (targetView == null) {
            nextStep();
            return;
        }

        int[] loc = new int[2];
        targetView.getLocationOnScreen(loc);
        int cx = loc[0] + targetView.getWidth() / 2;
        int cy = loc[1] + targetView.getHeight() / 2;
        int radius = Math.max(targetView.getWidth(), targetView.getHeight()) / 2 + 40;

        Spotlightview spotlight = new Spotlightview(this, cx, cy, radius);
        spotlight.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        onboardingOverlay.addView(spotlight);

        LinearLayout tooltip = new LinearLayout(this);
        tooltip.setOrientation(LinearLayout.VERTICAL);
        tooltip.setBackgroundColor(Color.WHITE);
        tooltip.setPadding(56, 48, 56, 48);
        tooltip.setElevation(24f);

        LinearLayout dotsRow = new LinearLayout(this);
        dotsRow.setOrientation(LinearLayout.HORIZONTAL);
        dotsRow.setPadding(0, 0, 0, 20);
        for (int i = 0; i < stepViewIds.length; i++) {
            TextView dot = new TextView(this);
            dot.setText("●");
            dot.setTextSize(10f);
            dot.setTextColor(i == step ? Color.parseColor("#2D6A4F") : Color.parseColor("#CCCCCC"));
            LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dp.setMargins(0, 0, 8, 0);
            dot.setLayoutParams(dp);
            dotsRow.addView(dot);
        }
        tooltip.addView(dotsRow);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(stepTitles[step]);
        tvTitle.setTextColor(Color.parseColor("#1F1A17"));
        tvTitle.setTextSize(20f);
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setPadding(0, 0, 0, 12);
        tooltip.addView(tvTitle);

        TextView tvDesc = new TextView(this);
        tvDesc.setText(stepDescs[step]);
        tvDesc.setTextColor(Color.parseColor("#6F5B5B"));
        tvDesc.setTextSize(14f);
        tvDesc.setLineSpacing(6f, 1f);
        tvDesc.setPadding(0, 0, 0, 32);
        tooltip.addView(tvDesc);

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView btnSkip = new TextView(this);
        btnSkip.setText("Skip tour");
        btnSkip.setTextColor(Color.parseColor("#8A817C"));
        btnSkip.setTextSize(14f);
        LinearLayout.LayoutParams skipParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        btnSkip.setLayoutParams(skipParams);
        btnSkip.setOnClickListener(v -> finishOnboarding());
        btnRow.addView(btnSkip);

        boolean isLast = (step == stepViewIds.length - 1);
        Button btnNext = new Button(this);
        btnNext.setText(isLast ? "Done 🎉" : "Next  →");
        btnNext.setTextColor(Color.WHITE);
        btnNext.setBackgroundResource(R.drawable.btn_add_circle);
        btnNext.setTextSize(14f);
        btnNext.setTypeface(null, Typeface.BOLD);
        btnNext.setPadding(48, 16, 48, 16);
        btnNext.setOnClickListener(v -> {
            if (isLast) finishOnboarding();
            else nextStep();
        });
        btnRow.addView(btnNext);
        tooltip.addView(btnRow);

        FrameLayout.LayoutParams tooltipParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tooltipParams.gravity = Gravity.BOTTOM;

        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        if (cy > screenHeight * 2 / 3) {
            tooltipParams.bottomMargin = screenHeight - cy + radius + 16;
        } else {
            tooltipParams.bottomMargin = 0;
        }

        tooltip.setLayoutParams(tooltipParams);
        onboardingOverlay.addView(tooltip);

        tooltip.setTranslationY(60f);
        tooltip.setAlpha(0f);
        tooltip.animate().translationY(0f).alpha(1f).setDuration(300).start();
    }

    private void nextStep() {
        currentStep++;
        if (currentStep < stepViewIds.length) showStep(currentStep);
        else finishOnboarding();
    }

    private void finishOnboarding() {
        if (onboardingOverlay != null) {
            onboardingOverlay.animate().alpha(0f).setDuration(250).withEndAction(() -> {
                ViewGroup root = findViewById(android.R.id.content);
                root.removeView(onboardingOverlay);
                onboardingOverlay = null;
            }).start();
        }
        prefs.edit().putBoolean("onboarding_done", true).apply();
    }
}