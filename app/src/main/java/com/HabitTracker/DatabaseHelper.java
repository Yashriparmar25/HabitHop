package com.HabitTracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "HabitTracker.db";
    private static final int    DB_VERSION = 1;

    // ── Table: users ─────────────────────────────────────────────────
    public static final String TABLE_USERS    = "users";
    public static final String COL_USER_ID    = "id";
    public static final String COL_NAME       = "name";
    public static final String COL_BIRTHDAY   = "birthday";
    public static final String COL_GENDER     = "gender";
    public static final String COL_GOAL       = "goal";
    public static final String COL_AVATAR_RES = "avatar_res";
    public static final String COL_AVATAR_URI = "avatar_uri";

    // ── Table: habits ────────────────────────────────────────────────
    public static final String TABLE_HABITS   = "habits";
    public static final String COL_HABIT_ID   = "id";
    public static final String COL_HABIT_NAME = "habit_name";
    public static final String COL_HABIT_DESC = "habit_desc";
    public static final String COL_CREATED_AT = "created_at";

    // ── Table: habit_logs ────────────────────────────────────────────
    public static final String TABLE_LOGS     = "habit_logs";
    public static final String COL_LOG_ID     = "id";
    public static final String COL_LOG_HABIT  = "habit_id";
    public static final String COL_LOG_DATE   = "log_date";
    public static final String COL_LOG_DONE   = "is_done";

    // ── Table: journal ───────────────────────────────────────────────
    public static final String TABLE_JOURNAL  = "journal";
    public static final String COL_JOURNAL_ID = "id";
    public static final String COL_J_DATE     = "entry_date";
    public static final String COL_J_TEXT     = "entry_text";
    public static final String COL_J_MOOD     = "mood";

    // ════════════════════════════════════════════════════════════════
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME       + " TEXT, "
                + COL_BIRTHDAY   + " TEXT, "
                + COL_GENDER     + " TEXT, "
                + COL_GOAL       + " TEXT, "
                + COL_AVATAR_RES + " TEXT, "
                + COL_AVATAR_URI + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_HABITS + " ("
                + COL_HABIT_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_HABIT_NAME + " TEXT, "
                + COL_HABIT_DESC + " TEXT, "
                + "category TEXT, "
                + "frequency TEXT, "
                + COL_CREATED_AT + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_LOGS + " ("
                + COL_LOG_ID    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_LOG_HABIT + " INTEGER, "
                + COL_LOG_DATE  + " TEXT, "
                + COL_LOG_DONE  + " INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE " + TABLE_JOURNAL + " ("
                + COL_JOURNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_J_DATE     + " TEXT, "
                + COL_J_TEXT     + " TEXT, "
                + COL_J_MOOD     + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOURNAL);
        onCreate(db);
    }

    // ════════════════════════════════════════════════════════════════
    //  USER METHODS
    // ════════════════════════════════════════════════════════════════

    // Save user profile
    public void saveUser(String name, String birthday, String gender,
                         String goal, String avatarRes, String avatarUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Clear old user first (single user app)
        db.delete(TABLE_USERS, null, null);

        ContentValues cv = new ContentValues();
        cv.put(COL_NAME,       name);
        cv.put(COL_BIRTHDAY,   birthday);
        cv.put(COL_GENDER,     gender);
        cv.put(COL_GOAL,       goal);
        cv.put(COL_AVATAR_RES, avatarRes);
        cv.put(COL_AVATAR_URI, avatarUri);
        db.insert(TABLE_USERS, null, cv);
        db.close();
    }

    // Get user name
    public String getUserName() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_NAME + " FROM " + TABLE_USERS + " LIMIT 1", null
        );
        String name = "Friend";
        if (cursor.moveToFirst()) name = cursor.getString(0);
        cursor.close();
        db.close();
        return name;
    }

    // Get full user profile as Cursor
    public Cursor getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " LIMIT 1", null);
    }

    // ════════════════════════════════════════════════════════════════
    //  HABIT METHODS
    // ════════════════════════════════════════════════════════════════

    // Add a habit
    public void addHabit(String name, String desc, String category,
                         String frequency, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_HABIT_NAME, name);
        cv.put(COL_HABIT_DESC, desc);
        cv.put("category",     category);
        cv.put("frequency",    frequency);
        cv.put(COL_CREATED_AT, date);
        db.insert(TABLE_HABITS, null, cv);
        db.close();
    }

    // Get all habits
    // Get all habits as Cursor
    public Cursor getAllHabits() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_HABITS, null);
    }

    // Get all habits as List ← ADD THIS BELOW
    public List<Habit> getAllHabitsList() {
        List<Habit> habits = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HABITS, null);

        if (cursor.moveToFirst()) {
            do {
                habits.add(new Habit(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_HABIT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_HABIT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_HABIT_DESC)),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getString(cursor.getColumnIndexOrThrow("frequency")),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CREATED_AT))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return habits;
    }

    // Log habit as done
    public void logHabit(int habitId, String date, boolean done) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_LOG_HABIT, habitId);
        cv.put(COL_LOG_DATE,  date);
        cv.put(COL_LOG_DONE,  done ? 1 : 0);
        db.insert(TABLE_LOGS, null, cv);
        db.close();
    }

    // Undo habit log
    public void unlogHabit(int habitId, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOGS,
                COL_LOG_HABIT + "=? AND " + COL_LOG_DATE + "=?",
                new String[]{String.valueOf(habitId), date});
        db.close();
    }

    // Check if habit done today
    public boolean isHabitDoneToday(int habitId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_LOGS +
                        " WHERE " + COL_LOG_HABIT + "=? AND " +
                        COL_LOG_DATE + "=? AND " + COL_LOG_DONE + "=1",
                new String[]{String.valueOf(habitId), date}
        );
        boolean done = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return done;
    }

    // Get completion status for a date (0 = none, 1 = partial, 2 = all done)
    public int getDailyCompletionStatus(String date) {
        List<Habit> habits = getAllHabitsList();
        if (habits.isEmpty()) return 0;

        int total = habits.size();
        int done  = 0;
        for (Habit h : habits) {
            if (isHabitDoneToday(h.getId(), date)) done++;
        }

        if (done == 0)     return 0; // nothing done
        if (done < total)  return 1; // partial
        return 2;                    // all done 🔥
    }

    // Get streak count
    public int getStreak(int habitId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_LOG_DATE + " FROM " + TABLE_LOGS +
                        " WHERE " + COL_LOG_HABIT + "=? AND " + COL_LOG_DONE + "=1" +
                        " ORDER BY " + COL_LOG_DATE + " DESC",
                new String[]{String.valueOf(habitId)}
        );

        int streak = 0;
        java.util.Calendar cal = java.util.Calendar.getInstance();
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());

        while (cursor.moveToNext()) {
            try {
                String logDate = cursor.getString(0);
                String expected = sdf.format(cal.getTime());
                if (logDate.equals(expected)) {
                    streak++;
                    cal.add(java.util.Calendar.DATE, -1);
                } else break;
            } catch (Exception e) { break; }
        }
        cursor.close();
        db.close();
        return streak;
    }

    // ════════════════════════════════════════════════════════════════
    //  JOURNAL METHODS
    // ════════════════════════════════════════════════════════════════

    // Save journal entry
    public void saveJournal(String date, String text, String mood) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_J_DATE, date);
        cv.put(COL_J_TEXT, text);
        cv.put(COL_J_MOOD, mood);
        db.insert(TABLE_JOURNAL, null, cv);
        db.close();
    }

    // Get journal by date
    public Cursor getJournalByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_JOURNAL + " WHERE " + COL_J_DATE + "=?",
                new String[]{date}
        );
    }
}