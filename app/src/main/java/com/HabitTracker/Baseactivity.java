package com.HabitTracker;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

public class Baseactivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = Localhelper.getPersistedLanguage(newBase);
        super.attachBaseContext(Localhelper.setLocale(newBase, lang));
    }
}