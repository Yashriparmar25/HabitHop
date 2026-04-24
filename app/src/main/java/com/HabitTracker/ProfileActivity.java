package com.HabitTracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class ProfileActivity extends Baseactivity {

    private LinearLayout btnLanguage;
    private LinearLayout navHome, navJournal, navAdd, navReminders, navProfile;
    private Button btnLogout, btnDeleteAccount;
    private Button btnEditProfile;
    private TextView tvCurrentLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnLanguage = findViewById(R.id.btnLanguage);
        tvCurrentLanguage = findViewById(R.id.tvCurrentLanguage);

        btnLogout = findViewById(R.id.btnLogout);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        navHome = findViewById(R.id.navHome);
        navJournal = findViewById(R.id.navJournal);
        navAdd = findViewById(R.id.navAdd);
        navReminders = findViewById(R.id.navReminders);
        navProfile = findViewById(R.id.navProfile);

        updateLanguageLabel();
        setupClicks();
    }

    private void setupClicks() {
        if (btnLanguage != null) {
            btnLanguage.setOnClickListener(v -> showLanguageDialog());
        }

        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v ->
                    startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class)));
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                // your logout logic here
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finishAffinity();
            });
        }

        if (btnDeleteAccount != null) {
            btnDeleteAccount.setOnClickListener(v -> {
                // your delete account logic here
            });
        }

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                startActivity(new Intent(ProfileActivity.this, DashboardActivity.class));
                finish();
            });
        }

        if (navJournal != null) {
            navJournal.setOnClickListener(v ->
                    startActivity(new Intent(ProfileActivity.this, JournalActivity.class)));
        }

        if (navAdd != null) {
            navAdd.setOnClickListener(v ->
                    startActivity(new Intent(ProfileActivity.this, AddHabitActivity.class)));
        }

        if (navReminders != null) {
            navReminders.setOnClickListener(v ->
                    startActivity(new Intent(ProfileActivity.this, RemindersActivity.class)));
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
            });
        }
    }

    private void updateLanguageLabel() {
        String lang = Localhelper.getPersistedLanguage(this);
        if (tvCurrentLanguage == null) return;

        if ("hi".equals(lang)) {
            tvCurrentLanguage.setText(R.string.hindi);
        } else if ("gu".equals(lang)) {
            tvCurrentLanguage.setText(R.string.gujarati);
        } else {
            tvCurrentLanguage.setText(R.string.english);
        }
    }

    private void showLanguageDialog() {
        final String[] languages = {
                getString(R.string.english),
                getString(R.string.hindi),
                getString(R.string.gujarati)
        };
        final String[] codes = {"en", "hi", "gu"};

        int selectedIndex = 0;
        String current = Localhelper.getPersistedLanguage(this);
        for (int i = 0; i < codes.length; i++) {
            if (codes[i].equals(current)) {
                selectedIndex = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.select_language)
                .setSingleChoiceItems(languages, selectedIndex, (dialog, which) -> {
                    Localhelper.setLocale(ProfileActivity.this, codes[which]);
                    dialog.dismiss();
                    recreate();
                })
                .setNegativeButton(R.string.back, (dialog, which) -> dialog.dismiss())
                .show();
    }
}