package com.HabitTracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MoodTrackerActivity extends AppCompatActivity {

    private TextView tvMoodStatus, tvSelectedMood, tvMoodScore;
    private Button btnSaveMood, btnMoodAmazing, btnMoodGood, btnMoodOkay, btnMoodLow, btnMoodSad;
    private EditText etMoodNote;
    private RecyclerView recyclerMoodHistory;

    private SharedPreferences prefs;
    private String userEmail = "";

    private String selectedMood = "";
    private int selectedMoodScore = 0;
    private final List<MoodEntry> moodHistory = new ArrayList<>();
    private MoodHistoryAdapter adapter;

    private static final String PREFS = "HabitKit";

    private static final String KEY_HISTORY = "mood_history";
    private static final String KEY_LAST_MOOD = "latest_mood_name";
    private static final String KEY_LAST_NOTE = "latest_mood_note";
    private static final String KEY_LAST_SCORE = "latest_mood_score";
    private static final String KEY_LAST_DATE = "latest_mood_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_tracker);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        userEmail = prefs.getString("current_user_email", "");

        bindViews();
        setupHistory();
        loadData();
        refreshMoodUi();

        btnMoodAmazing.setOnClickListener(v -> chooseMood("Amazing", 100));
        btnMoodGood.setOnClickListener(v -> chooseMood("Good", 80));
        btnMoodOkay.setOnClickListener(v -> chooseMood("Okay", 60));
        btnMoodLow.setOnClickListener(v -> chooseMood("Low", 40));
        btnMoodSad.setOnClickListener(v -> chooseMood("Sad", 20));

        btnSaveMood.setOnClickListener(v -> saveMood());
    }

    private String key(String base) {
        return base + "_" + userEmail;
    }

    private void bindViews() {
        tvMoodStatus = findViewById(R.id.tvMoodStatus);
        tvSelectedMood = findViewById(R.id.tvSelectedMood);
        tvMoodScore = findViewById(R.id.tvMoodScore);
        btnSaveMood = findViewById(R.id.btnSaveMood);
        btnMoodAmazing = findViewById(R.id.btnMoodAmazing);
        btnMoodGood = findViewById(R.id.btnMoodGood);
        btnMoodOkay = findViewById(R.id.btnMoodOkay);
        btnMoodLow = findViewById(R.id.btnMoodLow);
        btnMoodSad = findViewById(R.id.btnMoodSad);
        etMoodNote = findViewById(R.id.etMoodNote);
        recyclerMoodHistory = findViewById(R.id.recyclerMoodHistory);
    }

    private void setupHistory() {
        adapter = new MoodHistoryAdapter(moodHistory);
        recyclerMoodHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerMoodHistory.setAdapter(adapter);
    }

    private void loadData() {
        selectedMood = prefs.getString(key(KEY_LAST_MOOD), "");
        selectedMoodScore = prefs.getInt(key(KEY_LAST_SCORE), 0);
        etMoodNote.setText(prefs.getString(key(KEY_LAST_NOTE), ""));
        loadHistory();
    }

    private void refreshMoodUi() {
        if (TextUtils.isEmpty(selectedMood)) {
            tvMoodStatus.setText("Mood tracking is idle");
            tvSelectedMood.setText("Pick a mood");
            tvMoodScore.setText("0/100");
        } else {
            tvMoodStatus.setText("Mood selected");
            tvSelectedMood.setText(selectedMood);
            tvMoodScore.setText(selectedMoodScore + "/100");
        }
        updateMoodButtonStates();
    }

    private void chooseMood(String mood, int score) {
        selectedMood = mood;
        selectedMoodScore = score;
        refreshMoodUi();
    }

    private void updateMoodButtonStates() {
        setButtonState(btnMoodAmazing, "Amazing".equals(selectedMood));
        setButtonState(btnMoodGood, "Good".equals(selectedMood));
        setButtonState(btnMoodOkay, "Okay".equals(selectedMood));
        setButtonState(btnMoodLow, "Low".equals(selectedMood));
        setButtonState(btnMoodSad, "Sad".equals(selectedMood));
    }

    private void setButtonState(Button button, boolean active) {
        button.setAlpha(active ? 1f : 0.65f);
    }

    private void saveMood() {
        if (TextUtils.isEmpty(selectedMood)) {
            Toast.makeText(this, "Please pick a mood first", Toast.LENGTH_SHORT).show();
            return;
        }

        String note = etMoodNote.getText().toString().trim();
        String date = new SimpleDateFormat("EEE, dd MMM • hh:mm a", Locale.getDefault()).format(new Date());

        MoodEntry entry = new MoodEntry(date, selectedMood, selectedMoodScore, note);
        moodHistory.add(0, entry);
        adapter.notifyItemInserted(0);
        recyclerMoodHistory.scrollToPosition(0);

        saveHistory();
        prefs.edit()
                .putString(key(KEY_LAST_MOOD), selectedMood)
                .putString(key(KEY_LAST_NOTE), note)
                .putInt(key(KEY_LAST_SCORE), selectedMoodScore)
                .putString(key(KEY_LAST_DATE), date)
                .apply();

        tvMoodStatus.setText("Mood saved");
        Toast.makeText(this, "Mood saved", Toast.LENGTH_SHORT).show();
    }

    private void saveHistory() {
        StringBuilder sb = new StringBuilder();
        for (MoodEntry e : moodHistory) {
            sb.append(e.date).append("||")
                    .append(e.mood).append("||")
                    .append(e.score).append("||")
                    .append(e.note == null ? "" : e.note.replace("||", " "))
                    .append("\n");
        }
        prefs.edit().putString(key(KEY_HISTORY), sb.toString()).apply();
    }

    private void loadHistory() {
        moodHistory.clear();
        String raw = prefs.getString(key(KEY_HISTORY), "");
        if (!TextUtils.isEmpty(raw)) {
            String[] lines = raw.split("\n");
            for (String line : lines) {
                if (TextUtils.isEmpty(line)) continue;
                String[] p = line.split("\\|\\|", -1);
                if (p.length >= 4) {
                    try {
                        moodHistory.add(new MoodEntry(p[0], p[1], Integer.parseInt(p[2]), p[3]));
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    static class MoodEntry {
        String date;
        String mood;
        int score;
        String note;

        MoodEntry(String date, String mood, int score, String note) {
            this.date = date;
            this.mood = mood;
            this.score = score;
            this.note = note;
        }
    }

    static class MoodHistoryAdapter extends RecyclerView.Adapter<MoodHistoryAdapter.VH> {
        private final List<MoodEntry> items;

        MoodHistoryAdapter(List<MoodEntry> items) {
            this.items = items;
        }

        @Override
        public VH onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View v = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mood_history, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            MoodEntry e = items.get(position);
            holder.tvDate.setText(e.date);
            holder.tvMood.setText(e.mood);
            holder.tvScore.setText(e.score + "/100");
            holder.tvNote.setText(TextUtils.isEmpty(e.note) ? "No note added" : e.note);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvDate, tvMood, tvScore, tvNote;

            VH(android.view.View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tvMoodDate);
                tvMood = itemView.findViewById(R.id.tvMoodName);
                tvScore = itemView.findViewById(R.id.tvMoodScoreItem);
                tvNote = itemView.findViewById(R.id.tvMoodNote);
            }
        }
    }
}