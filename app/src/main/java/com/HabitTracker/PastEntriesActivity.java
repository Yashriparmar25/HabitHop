package com.HabitTracker;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PastEntriesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private String currentUserEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_entries);

        recyclerView = findViewById(R.id.recycler_journal_entries);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        currentUserEmail = getSharedPreferences("HabitKit", MODE_PRIVATE)
                .getString("current_user_email", "");

        loadEntries();
    }

    private void loadEntries() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show();
            return;
        }

        List<JournalEntry> entries = dbHelper.getAllJournalEntryList(currentUserEmail);

        if (entries.isEmpty()) {
            Toast.makeText(this, "No past entries found", Toast.LENGTH_SHORT).show();
        }

        recyclerView.setAdapter(new JournalEntryAdapter(entries));
    }
}