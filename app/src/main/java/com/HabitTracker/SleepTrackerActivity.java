package com.HabitTracker;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SleepTrackerActivity extends AppCompatActivity {

    private TextView tvSleepStatus, tvBedtimeValue, tvWakeValue, tvDurationValue, tvQualityValue, tvSleepScore;
    private Button btnStartSleep, btnWakeUp, btnPickBedtime, btnPickWakeTime, btnSaveEntry;
    private Button btnQualityPoor, btnQualityOkay, btnQualityGood, btnQualityGreat;
    private EditText etSleepNote;
    private RecyclerView recyclerSleepHistory;

    private SharedPreferences prefs;
    private String userEmail = "";

    private boolean sleepRunning = false;
    private int bedtimeHour = -1, bedtimeMinute = -1;
    private int wakeHour = -1, wakeMinute = -1;
    private String selectedQuality = "Good";

    private final List<SleepEntry> history = new ArrayList<>();
    private SleepHistoryAdapter historyAdapter;

    private static final String PREFS = "HabitKit";

    private static final String KEY_SLEEP_RUNNING = "sleep_running";
    private static final String KEY_BED_H = "sleep_bed_h";
    private static final String KEY_BED_M = "sleep_bed_m";
    private static final String KEY_WAKE_H = "sleep_wake_h";
    private static final String KEY_WAKE_M = "sleep_wake_m";
    private static final String KEY_QUALITY = "sleep_quality";
    private static final String KEY_NOTE = "sleep_note";
    private static final String KEY_HISTORY = "sleep_history";
    private static final String KEY_LAST_DATE = "sleep_last_date";
    private static final String KEY_LAST_DURATION = "sleep_last_duration";
    private static final String KEY_LAST_QUALITY = "sleep_last_quality";
    private static final String KEY_LAST_BED = "sleep_last_bed";
    private static final String KEY_LAST_WAKE = "sleep_last_wake";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_tracker);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        userEmail = prefs.getString("current_user_email", "");

        bindViews();
        setupHistory();
        loadState();
        refreshUI();

        btnStartSleep.setOnClickListener(v -> {
            sleepRunning = true;
            Calendar c = Calendar.getInstance();
            bedtimeHour = c.get(Calendar.HOUR_OF_DAY);
            bedtimeMinute = c.get(Calendar.MINUTE);
            wakeHour = -1;
            wakeMinute = -1;
            saveState();
            refreshUI();
            Toast.makeText(this, "Sleep session started", Toast.LENGTH_SHORT).show();
        });

        btnWakeUp.setOnClickListener(v -> {
            if (!sleepRunning) {
                Toast.makeText(this, "Start sleep tracking first", Toast.LENGTH_SHORT).show();
                return;
            }
            sleepRunning = false;
            Calendar c = Calendar.getInstance();
            wakeHour = c.get(Calendar.HOUR_OF_DAY);
            wakeMinute = c.get(Calendar.MINUTE);
            addHistoryEntry();
            saveState();
            refreshUI();
            Toast.makeText(this, "Wake-up saved", Toast.LENGTH_SHORT).show();
        });

        btnPickBedtime.setOnClickListener(v -> pickBedTime());
        btnPickWakeTime.setOnClickListener(v -> pickWakeTime());

        btnQualityPoor.setOnClickListener(v -> setQuality("Poor"));
        btnQualityOkay.setOnClickListener(v -> setQuality("Okay"));
        btnQualityGood.setOnClickListener(v -> setQuality("Good"));
        btnQualityGreat.setOnClickListener(v -> setQuality("Great"));

        btnSaveEntry.setOnClickListener(v -> {
            String note = etSleepNote.getText().toString().trim();
            prefs.edit().putString(key(KEY_NOTE), note).apply();
            Toast.makeText(this, "Sleep note saved", Toast.LENGTH_SHORT).show();
        });
    }

    private String key(String base) {
        return base + "_" + userEmail;
    }

    private void bindViews() {
        tvSleepStatus = findViewById(R.id.tvSleepStatus);
        tvBedtimeValue = findViewById(R.id.tvBedtimeValue);
        tvWakeValue = findViewById(R.id.tvWakeValue);
        tvDurationValue = findViewById(R.id.tvDurationValue);
        tvQualityValue = findViewById(R.id.tvQualityValue);
        tvSleepScore = findViewById(R.id.tvSleepScore);

        btnStartSleep = findViewById(R.id.btnStartSleep);
        btnWakeUp = findViewById(R.id.btnWakeUp);
        btnPickBedtime = findViewById(R.id.btnPickBedtime);
        btnPickWakeTime = findViewById(R.id.btnPickWakeTime);
        btnSaveEntry = findViewById(R.id.btnSaveEntry);

        btnQualityPoor = findViewById(R.id.btnQualityPoor);
        btnQualityOkay = findViewById(R.id.btnQualityOkay);
        btnQualityGood = findViewById(R.id.btnQualityGood);
        btnQualityGreat = findViewById(R.id.btnQualityGreat);

        etSleepNote = findViewById(R.id.etSleepNote);
        recyclerSleepHistory = findViewById(R.id.recyclerSleepHistory);
    }

    private void setupHistory() {
        historyAdapter = new SleepHistoryAdapter(history);
        recyclerSleepHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerSleepHistory.setAdapter(historyAdapter);
    }

    private void loadState() {
        sleepRunning = prefs.getBoolean(key(KEY_SLEEP_RUNNING), false);
        bedtimeHour = prefs.getInt(key(KEY_BED_H), -1);
        bedtimeMinute = prefs.getInt(key(KEY_BED_M), -1);
        wakeHour = prefs.getInt(key(KEY_WAKE_H), -1);
        wakeMinute = prefs.getInt(key(KEY_WAKE_M), -1);
        selectedQuality = prefs.getString(key(KEY_QUALITY), "Good");
        etSleepNote.setText(prefs.getString(key(KEY_NOTE), ""));
        loadHistory();
    }

    private void saveState() {
        prefs.edit()
                .putBoolean(key(KEY_SLEEP_RUNNING), sleepRunning)
                .putInt(key(KEY_BED_H), bedtimeHour)
                .putInt(key(KEY_BED_M), bedtimeMinute)
                .putInt(key(KEY_WAKE_H), wakeHour)
                .putInt(key(KEY_WAKE_M), wakeMinute)
                .putString(key(KEY_QUALITY), selectedQuality)
                .apply();
    }

    private void refreshUI() {
        tvSleepStatus.setText(sleepRunning ? "Sleep tracking is running" : "Sleep tracking is idle");
        tvBedtimeValue.setText(formatTime(bedtimeHour, bedtimeMinute));
        tvWakeValue.setText(formatTime(wakeHour, wakeMinute));
        tvQualityValue.setText(selectedQuality);
        updateDurationAndScore();
        btnStartSleep.setEnabled(!sleepRunning);
        btnWakeUp.setEnabled(sleepRunning);
        updateQualityButtons();
    }

    private void updateDurationAndScore() {
        String duration = "—";
        int score = 0;

        if (bedtimeHour != -1 && wakeHour != -1) {
            int start = bedtimeHour * 60 + bedtimeMinute;
            int end = wakeHour * 60 + wakeMinute;
            if (end < start) end += 24 * 60;

            int diff = end - start;
            int hrs = diff / 60;
            int mins = diff % 60;
            duration = hrs + "h " + mins + "m";

            score = Math.min(100, (diff * 100) / (8 * 60));
            if ("Great".equals(selectedQuality)) score = Math.min(100, score + 10);
            else if ("Poor".equals(selectedQuality)) score = Math.max(0, score - 15);
            else if ("Okay".equals(selectedQuality)) score = Math.max(0, score - 5);
        }

        tvDurationValue.setText(duration);
        tvSleepScore.setText(score + "/100");
    }

    private void setQuality(String quality) {
        selectedQuality = quality;
        saveState();
        refreshUI();
    }

    private void updateQualityButtons() {
        btnQualityPoor.setAlpha("Poor".equals(selectedQuality) ? 1f : 0.6f);
        btnQualityOkay.setAlpha("Okay".equals(selectedQuality) ? 1f : 0.6f);
        btnQualityGood.setAlpha("Good".equals(selectedQuality) ? 1f : 0.6f);
        btnQualityGreat.setAlpha("Great".equals(selectedQuality) ? 1f : 0.6f);
    }

    private void pickBedTime() {
        Calendar c = Calendar.getInstance();
        int h = bedtimeHour == -1 ? c.get(Calendar.HOUR_OF_DAY) : bedtimeHour;
        int m = bedtimeMinute == -1 ? c.get(Calendar.MINUTE) : bedtimeMinute;

        new TimePickerDialog(this, (TimePicker view, int hourOfDay, int minute) -> {
            bedtimeHour = hourOfDay;
            bedtimeMinute = minute;
            saveState();
            refreshUI();
        }, h, m, false).show();
    }

    private void pickWakeTime() {
        Calendar c = Calendar.getInstance();
        int h = wakeHour == -1 ? c.get(Calendar.HOUR_OF_DAY) : wakeHour;
        int m = wakeMinute == -1 ? c.get(Calendar.MINUTE) : wakeMinute;

        new TimePickerDialog(this, (TimePicker view, int hourOfDay, int minute) -> {
            wakeHour = hourOfDay;
            wakeMinute = minute;
            saveState();
            refreshUI();
        }, h, m, false).show();
    }

    private String formatTime(int hour, int minute) {
        if (hour == -1 || minute == -1) return "Not set";
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(c.getTime());
    }

    private void addHistoryEntry() {
        String note = etSleepNote.getText().toString().trim();
        String duration = tvDurationValue.getText().toString();
        String date = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(new Date());

        SleepEntry entry = new SleepEntry(
                date,
                formatTime(bedtimeHour, bedtimeMinute),
                formatTime(wakeHour, wakeMinute),
                duration,
                selectedQuality,
                note
        );

        history.add(0, entry);
        historyAdapter.notifyItemInserted(0);
        recyclerSleepHistory.scrollToPosition(0);
        saveHistory();
        saveLatestSummary(date, duration, selectedQuality, formatTime(bedtimeHour, bedtimeMinute), formatTime(wakeHour, wakeMinute));
    }

    private void saveLatestSummary(String date, String duration, String quality, String bed, String wake) {
        prefs.edit()
                .putString(key(KEY_LAST_DATE), date)
                .putString(key(KEY_LAST_DURATION), duration)
                .putString(key(KEY_LAST_QUALITY), quality)
                .putString(key(KEY_LAST_BED), bed)
                .putString(key(KEY_LAST_WAKE), wake)
                .apply();
    }

    private void saveHistory() {
        StringBuilder sb = new StringBuilder();
        for (SleepEntry e : history) {
            sb.append(e.date).append("||")
                    .append(e.bedtime).append("||")
                    .append(e.wakeTime).append("||")
                    .append(e.duration).append("||")
                    .append(e.quality).append("||")
                    .append(e.note == null ? "" : e.note.replace("||", " "))
                    .append("\n");
        }
        prefs.edit().putString(key(KEY_HISTORY), sb.toString()).apply();
    }

    private void loadHistory() {
        history.clear();
        String raw = prefs.getString(key(KEY_HISTORY), "");
        if (!TextUtils.isEmpty(raw)) {
            String[] lines = raw.split("\n");
            for (String line : lines) {
                if (TextUtils.isEmpty(line)) continue;
                String[] p = line.split("\\|\\|", -1);
                if (p.length >= 6) {
                    history.add(new SleepEntry(p[0], p[1], p[2], p[3], p[4], p[5]));
                }
            }
        }
        if (historyAdapter != null) historyAdapter.notifyDataSetChanged();
    }

    static class SleepEntry {
        String date, bedtime, wakeTime, duration, quality, note;

        SleepEntry(String date, String bedtime, String wakeTime, String duration, String quality, String note) {
            this.date = date;
            this.bedtime = bedtime;
            this.wakeTime = wakeTime;
            this.duration = duration;
            this.quality = quality;
            this.note = note;
        }
    }

    static class SleepHistoryAdapter extends RecyclerView.Adapter<SleepHistoryAdapter.VH> {
        private final List<SleepEntry> items;

        SleepHistoryAdapter(List<SleepEntry> items) {
            this.items = items;
        }

        @Override
        public VH onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View v = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sleep_history, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            SleepEntry e = items.get(position);
            holder.tvDate.setText(e.date);
            holder.tvTime.setText(e.bedtime + " → " + e.wakeTime);
            holder.tvDuration.setText(e.duration + " • " + e.quality);
            holder.tvNote.setText(TextUtils.isEmpty(e.note) ? "No note added" : e.note);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvDate, tvTime, tvDuration, tvNote;

            VH(android.view.View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tvHistoryDate);
                tvTime = itemView.findViewById(R.id.tvHistoryTime);
                tvDuration = itemView.findViewById(R.id.tvHistoryDuration);
                tvNote = itemView.findViewById(R.id.tvHistoryNote);
            }
        }
    }
}