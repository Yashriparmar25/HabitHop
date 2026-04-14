package com.HabitTracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);
        prefs    = getSharedPreferences("HabitKit", MODE_PRIVATE);

        bindViews();
        loadProfile();
        loadStats();

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileSetupActivity.class);
            intent.putExtra("edit_mode", true);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            loadProfile();
        }
    }

    private void bindViews() {
        profileAvatar   = findViewById(R.id.profileAvatar);
        tvProfileName   = findViewById(R.id.tvProfileName);
        tvProfileGoal   = findViewById(R.id.tvProfileGoal);
        tvBirthday      = findViewById(R.id.tvBirthday);
        tvGender        = findViewById(R.id.tvGender);
        tvGoal          = findViewById(R.id.tvGoal);
        tvTotalHabits   = findViewById(R.id.tvTotalHabits);
        tvProfileStreak = findViewById(R.id.tvProfileStreak);
        tvDoneToday     = findViewById(R.id.tvDoneToday);
        btnEditProfile  = findViewById(R.id.btnEditProfile);
        btnLogout       = findViewById(R.id.btnLogout);
    }

    private void loadProfile() {
        // ✅ Get name from SharedPreferences (saved during signup)
        String prefsName = prefs.getString("name", "");

        Cursor cursor = dbHelper.getUser();
        if (cursor != null && cursor.moveToFirst()) {
            String dbName    = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
            String birthday  = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BIRTHDAY));
            String gender    = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GENDER));
            String goal      = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GOAL));

            // ✅ SharedPreferences name takes priority over DB name
            String displayName = !prefsName.isEmpty() ? prefsName
                    : (dbName != null ? dbName : "Friend");

            tvProfileName.setText(displayName);
            tvProfileGoal.setText("Hi, " + displayName + " 👋");
            tvBirthday.setText(birthday != null && !birthday.isEmpty() ? birthday : "—");
            tvGender.setText(gender   != null && !gender.isEmpty()   ? gender   : "—");
            tvGoal.setText(goal       != null && !goal.isEmpty()     ? goal     : "—");

            cursor.close();
        } else {
            // No DB record — just use SharedPreferences name
            String displayName = !prefsName.isEmpty() ? prefsName : "Friend";
            tvProfileName.setText(displayName);
            tvProfileGoal.setText("Hi, " + displayName + " 👋");
            tvBirthday.setText("—");
            tvGender.setText("—");
            tvGoal.setText("—");
        }

        // ✅ Load avatar
        String savedUri = prefs.getString("avatar_gallery_uri", "");
        String savedRes = prefs.getString("avatar_res_name", "");
        if (!savedUri.isEmpty()) {
            profileAvatar.setImageURI(Uri.parse(savedUri));
        } else if (!savedRes.isEmpty()) {
            int resId = getResources().getIdentifier(savedRes, "drawable", getPackageName());
            if (resId != 0) profileAvatar.setImageResource(resId);
        }

        profileAvatar.post(() -> {
            profileAvatar.setClipToOutline(true);
            profileAvatar.setOutlineProvider(new android.view.ViewOutlineProvider() {
                @Override
                public void getOutline(View view, android.graphics.Outline outline) {
                    outline.setOval(0, 0, view.getWidth(), view.getHeight());
                }
            });
        });
    }

    private void loadStats() {
        List<Habit> habits = dbHelper.getAllHabitsList();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        int total  = habits.size();
        int done   = 0;
        int streak = 0;

        for (Habit h : habits) {
            if (dbHelper.isHabitDoneToday(h.getId(), today)) done++;
        }
        if (!habits.isEmpty()) {
            streak = dbHelper.getStreak(habits.get(0).getId());
        }

        tvTotalHabits.setText(String.valueOf(total));
        tvProfileStreak.setText(streak + "🔥");
        tvDoneToday.setText(done + "✅");
    }
}