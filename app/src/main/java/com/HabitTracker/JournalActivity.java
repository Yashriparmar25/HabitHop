package com.HabitTracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class JournalActivity extends AppCompatActivity {

    private TextView tvJournalDate;
    private EditText etJournalText;
    private TextView moodHappy, moodCalm, moodTired;
    private Button btnSaveJournal, btnNewJournal;
    private ImageView btnBack;

    private LinearLayout navHome, navJournal, navAdd, navReminders, navProfile;

    private SharedPreferences prefs;
    private DatabaseHelper dbHelper;
    private String currentUserEmail = "";
    private String selectedMood = "Happy";
    private String todayDate;

    private RecyclerView recyclerJournalEntries;
    private JournalAdapter journalAdapter;
    private final ArrayList<JournalEntry> journalEntries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        prefs = getSharedPreferences("HabitKit", MODE_PRIVATE);
        dbHelper = new DatabaseHelper(this);
        currentUserEmail = prefs.getString("current_user_email", "");

        bindViews();
        setupMoodSelection();
        setupNavigation();
        setupDate();
        setupRecycler();

        loadTodayEntries();

        btnBack.setOnClickListener(v -> finish());
        btnSaveJournal.setOnClickListener(v -> saveJournal());
        btnNewJournal.setOnClickListener(v -> createNewJournal());
    }

    private void bindViews() {
        tvJournalDate = findViewById(R.id.tvJournalDate);
        etJournalText = findViewById(R.id.etJournalText);
        moodHappy = findViewById(R.id.moodHappy);
        moodCalm = findViewById(R.id.moodCalm);
        moodTired = findViewById(R.id.moodTired);
        btnSaveJournal = findViewById(R.id.btnSaveJournal);
        btnNewJournal = findViewById(R.id.btnNewJournal);
        btnBack = findViewById(R.id.btnBack);
        recyclerJournalEntries = findViewById(R.id.recyclerJournalEntries);

        navHome = findViewById(R.id.navHome);
        navJournal = findViewById(R.id.navJournal);
        navAdd = findViewById(R.id.navAdd);
        navReminders = findViewById(R.id.navReminders);
        navProfile = findViewById(R.id.navProfile);
    }

    private void setupDate() {
        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String displayDate = new SimpleDateFormat("dd MMM", Locale.getDefault()).format(new Date());
        tvJournalDate.setText(displayDate);
    }

    private void setupMoodSelection() {
        View.OnClickListener moodClick = v -> {
            resetMoodCards();
            v.setBackgroundResource(R.drawable.card_category_selected);

            if (v.getId() == R.id.moodHappy) selectedMood = "Happy";
            else if (v.getId() == R.id.moodCalm) selectedMood = "Calm";
            else if (v.getId() == R.id.moodTired) selectedMood = "Tired";
        };

        moodHappy.setOnClickListener(moodClick);
        moodCalm.setOnClickListener(moodClick);
        moodTired.setOnClickListener(moodClick);

        resetMoodCards();
        moodHappy.setBackgroundResource(R.drawable.card_category_selected);
        selectedMood = "Happy";
    }

    private void resetMoodCards() {
        moodHappy.setBackgroundResource(R.drawable.card_category_unselected);
        moodCalm.setBackgroundResource(R.drawable.card_category_unselected);
        moodTired.setBackgroundResource(R.drawable.card_category_unselected);
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        navJournal.setOnClickListener(v -> {});

        navAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddHabitActivity.class))
        );

        navReminders.setOnClickListener(v ->
                Toast.makeText(this, "Reminders coming soon!", Toast.LENGTH_SHORT).show()
        );

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class))
        );
    }

    private void setupRecycler() {
        journalAdapter = new JournalAdapter(this, journalEntries);
        recyclerJournalEntries.setLayoutManager(new LinearLayoutManager(this));
        recyclerJournalEntries.setAdapter(journalAdapter);
    }

    private void loadTodayEntries() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) return;

        journalEntries.clear();
        Cursor cursor = dbHelper.getJournalEntriesForDate(currentUserEmail, todayDate);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int textIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_J_TEXT);
                int moodIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_J_MOOD);
                int timeIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_J_TIME);

                String text = cursor.getString(textIndex);
                String mood = cursor.getString(moodIndex);
                String time = cursor.getString(timeIndex);

                journalEntries.add(new JournalEntry(text, mood, time));
            }
            cursor.close();
        }

        journalAdapter.notifyDataSetChanged();
    }

    private void createNewJournal() {
        etJournalText.setText("");
        resetMoodCards();
        moodHappy.setBackgroundResource(R.drawable.card_category_selected);
        selectedMood = "Happy";
        etJournalText.requestFocus();
        Toast.makeText(this, "Ready for a new entry ✨", Toast.LENGTH_SHORT).show();
    }

    private void saveJournal() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            Toast.makeText(this, "Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String text = etJournalText.getText().toString().trim();
        if (text.isEmpty()) {
            etJournalText.setError("Write something for today");
            etJournalText.requestFocus();
            return;
        }

        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        dbHelper.saveJournal(currentUserEmail, todayDate, time, text, selectedMood);

        Toast.makeText(this, "Journal saved 💗", Toast.LENGTH_SHORT).show();
        etJournalText.setText("");
        loadTodayEntries();
    }
}