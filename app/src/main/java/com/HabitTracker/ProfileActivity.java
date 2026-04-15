package com.HabitTracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileAvatar;
    private TextView tvProfileName, tvProfileGoal, tvBirthday, tvGender, tvGoal;
    private TextView tvTotalHabits, tvProfileStreak, tvDoneToday;
    private Button btnEditProfile, btnLogout;

    private DatabaseHelper dbHelper;
    private SharedPreferences prefs;
    private String currentUserEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);
        prefs = getSharedPreferences("HabitKit", MODE_PRIVATE);
        currentUserEmail = prefs.getString("current_user_email", "");

        bindViews();
        loadProfile();
        loadStats();

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivityForResult(intent, 200);
        });

        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUserEmail = prefs.getString("current_user_email", "");
        loadProfile();
        loadStats();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            currentUserEmail = prefs.getString("current_user_email", "");
            loadProfile();
            loadStats();
        }
    }

    private void bindViews() {
        profileAvatar = findViewById(R.id.profileAvatar);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileGoal = findViewById(R.id.tvProfileGoal);
        tvBirthday = findViewById(R.id.tvBirthday);
        tvGender = findViewById(R.id.tvGender);
        tvGoal = findViewById(R.id.tvGoal);
        tvTotalHabits = findViewById(R.id.tvTotalHabits);
        tvProfileStreak = findViewById(R.id.tvProfileStreak);
        tvDoneToday = findViewById(R.id.tvDoneToday);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadProfile() {
        String prefsName = prefs.getString("name", "");

        String displayName = "Friend";
        String birthdayValue = "—";
        String genderValue = "—";
        String goalValue = "—";

        Cursor cursor = null;
        try {
            if (currentUserEmail != null && !currentUserEmail.isEmpty()) {
                cursor = dbHelper.getUser(currentUserEmail);
                if (cursor != null && cursor.moveToFirst()) {
                    String dbName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
                    String birthday = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BIRTHDAY));
                    String gender = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GENDER));
                    String goal = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GOAL));

                    if (prefsName != null && !prefsName.isEmpty()) {
                        displayName = prefsName;
                    } else if (dbName != null && !dbName.isEmpty()) {
                        displayName = dbName;
                    }

                    if (birthday != null && !birthday.isEmpty()) birthdayValue = birthday;
                    if (gender != null && !gender.isEmpty()) genderValue = gender;
                    if (goal != null && !goal.isEmpty()) goalValue = goal;
                } else {
                    if (prefsName != null && !prefsName.isEmpty()) displayName = prefsName;
                }
            } else {
                if (prefsName != null && !prefsName.isEmpty()) displayName = prefsName;
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) cursor.close();
        }

        tvProfileName.setText(displayName);
        tvProfileGoal.setText("Hi, " + displayName + " 👋");
        tvBirthday.setText(birthdayValue);
        tvGender.setText(genderValue);
        tvGoal.setText(goalValue);

        String savedUri = prefs.getString("avatar_gallery_uri", "");
        String savedRes = prefs.getString("avatar_res_name", "");

        if (!savedUri.isEmpty()) {
            try {
                profileAvatar.setImageURI(Uri.parse(savedUri));
            } catch (Exception e) {
                profileAvatar.setImageResource(R.drawable.turtle);
            }
        } else if (!savedRes.isEmpty()) {
            int resId = getResources().getIdentifier(savedRes, "drawable", getPackageName());
            if (resId != 0) profileAvatar.setImageResource(resId);
        } else {
            profileAvatar.setImageResource(R.drawable.turtle);
        }

        profileAvatar.post(() -> {
            profileAvatar.setClipToOutline(true);
            profileAvatar.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, android.graphics.Outline outline) {
                    outline.setOval(0, 0, view.getWidth(), view.getHeight());
                }
            });
        });
    }

    private void loadStats() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            tvTotalHabits.setText("0");
            tvProfileStreak.setText("0🔥");
            tvDoneToday.setText("0✅");
            return;
        }

        List<Habit> habits = dbHelper.getAllHabitsList(currentUserEmail);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        int total = habits.size();
        int done = 0;
        int streak = 0;

        for (Habit h : habits) {
            if (dbHelper.isHabitDoneToday(currentUserEmail, h.getId(), today)) done++;
        }


        if (!habits.isEmpty()) {
            streak = dbHelper.getStreak(currentUserEmail, habits.get(0).getId());
        }

        tvTotalHabits.setText(String.valueOf(total));
        tvProfileStreak.setText(streak + "🔥");
        tvDoneToday.setText(done + "✅");
    }
}