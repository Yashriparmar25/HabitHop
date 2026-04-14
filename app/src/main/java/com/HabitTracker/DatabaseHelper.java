package com.HabitTracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "HabitTracker.db";
    private static final int DB_VERSION = 3;

    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_EMAIL = "user_email";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_NAME = "name";
    public static final String COL_BIRTHDAY = "birthday";
    public static final String COL_GENDER = "gender";
    public static final String COL_GOAL = "goal";
    public static final String COL_AVATAR_RES = "avatar_res";
    public static final String COL_AVATAR_URI = "avatar_uri";

    public static final String TABLE_HABITS = "habits";
    public static final String COL_HABIT_ID = "id";
    public static final String COL_HABIT_USER_EMAIL = "user_email";
    public static final String COL_HABIT_NAME = "habit_name";
    public static final String COL_HABIT_DESC = "habit_desc";
    public static final String COL_CATEGORY = "category";
    public static final String COL_FREQUENCY = "frequency";
    public static final String COL_CREATED_AT = "created_at";

    public static final String TABLE_LOGS = "habit_logs";
    public static final String COL_LOG_ID = "id";
    public static final String COL_LOG_USER_EMAIL = "user_email";
    public static final String COL_LOG_HABIT = "habit_id";
    public static final String COL_LOG_DATE = "log_date";
    public static final String COL_LOG_DONE = "is_done";

    public static final String TABLE_JOURNAL = "journal";
    public static final String COL_JOURNAL_ID = "id";
    public static final String COL_JOURNAL_USER_EMAIL = "user_email";
    public static final String COL_J_DATE = "entry_date";
    public static final String COL_J_TEXT = "entry_text";
    public static final String COL_J_MOOD = "mood";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_EMAIL + " TEXT UNIQUE, "
                + COL_USER_PASSWORD + " TEXT, "
                + COL_NAME + " TEXT, "
                + COL_BIRTHDAY + " TEXT, "
                + COL_GENDER + " TEXT, "
                + COL_GOAL + " TEXT, "
                + COL_AVATAR_RES + " TEXT, "
                + COL_AVATAR_URI + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_HABITS + " ("
                + COL_HABIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_HABIT_USER_EMAIL + " TEXT, "
                + COL_HABIT_NAME + " TEXT, "
                + COL_HABIT_DESC + " TEXT, "
                + COL_CATEGORY + " TEXT, "
                + COL_FREQUENCY + " TEXT, "
                + COL_CREATED_AT + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_LOGS + " ("
                + COL_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_LOG_USER_EMAIL + " TEXT, "
                + COL_LOG_HABIT + " INTEGER, "
                + COL_LOG_DATE + " TEXT, "
                + COL_LOG_DONE + " INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE " + TABLE_JOURNAL + " ("
                + COL_JOURNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_JOURNAL_USER_EMAIL + " TEXT, "
                + COL_J_DATE + " TEXT, "
                + COL_J_TEXT + " TEXT, "
                + COL_J_MOOD + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOURNAL);
        onCreate(db);
    }

    public void saveUser(String email, String name, String birthday, String gender,
                         String goal, String avatarRes, String avatarUri) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_USER_EMAIL, email);
        cv.put(COL_NAME, name);
        cv.put(COL_BIRTHDAY, birthday);
        cv.put(COL_GENDER, gender);
        cv.put(COL_GOAL, goal);
        cv.put(COL_AVATAR_RES, avatarRes);
        cv.put(COL_AVATAR_URI, avatarUri);

        int updated = db.update(TABLE_USERS, cv, COL_USER_EMAIL + "=?", new String[]{email});
        if (updated == 0) {
            db.insert(TABLE_USERS, null, cv);
        }
        db.close();
    }

    public void saveUserCredentials(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_EMAIL, email);
        cv.put(COL_USER_PASSWORD, password);

        int updated = db.update(TABLE_USERS, cv, COL_USER_EMAIL + "=?", new String[]{email});
        if (updated == 0) {
            db.insert(TABLE_USERS, null, cv);
        }
        db.close();
    }

    public String getSavedPassword(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String password = "";
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_USER_PASSWORD + " FROM " + TABLE_USERS +
                        " WHERE " + COL_USER_EMAIL + "=? LIMIT 1",
                new String[]{email}
        );
        if (cursor.moveToFirst()) {
            password = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return password;
    }

    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String name = "Friend";
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_NAME + " FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "=? LIMIT 1",
                new String[]{email}
        );
        if (cursor.moveToFirst()) name = cursor.getString(0);
        cursor.close();
        db.close();
        return name;
    }

    public Cursor getUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "=? LIMIT 1",
                new String[]{email}
        );
    }

    public void addHabit(String userEmail, String name, String desc, String category,
                         String frequency, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_HABIT_USER_EMAIL, userEmail);
        cv.put(COL_HABIT_NAME, name);
        cv.put(COL_HABIT_DESC, desc);
        cv.put(COL_CATEGORY, category);
        cv.put(COL_FREQUENCY, frequency);
        cv.put(COL_CREATED_AT, date);
        db.insert(TABLE_HABITS, null, cv);
        db.close();
    }

    public Cursor getAllHabits(String userEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_HABITS + " WHERE " + COL_HABIT_USER_EMAIL + "=?",
                new String[]{userEmail}
        );
    }

    public List<Habit> getAllHabitsList(String userEmail) {
        List<Habit> habits = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_HABITS + " WHERE " + COL_HABIT_USER_EMAIL + "=?",
                new String[]{userEmail}
        );

        if (cursor.moveToFirst()) {
            do {
                habits.add(new Habit(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_HABIT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_HABIT_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_HABIT_DESC)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_FREQUENCY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_CREATED_AT))
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return habits;
    }

    public void logHabit(String userEmail, int habitId, String date, boolean done) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_LOG_USER_EMAIL, userEmail);
        cv.put(COL_LOG_HABIT, habitId);
        cv.put(COL_LOG_DATE, date);
        cv.put(COL_LOG_DONE, done ? 1 : 0);
        db.insert(TABLE_LOGS, null, cv);
        db.close();
    }

    public void unlogHabit(String userEmail, int habitId, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOGS,
                COL_LOG_USER_EMAIL + "=? AND " + COL_LOG_HABIT + "=? AND " + COL_LOG_DATE + "=?",
                new String[]{userEmail, String.valueOf(habitId), date});
        db.close();
    }

    public boolean isHabitDoneToday(String userEmail, int habitId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_LOGS +
                        " WHERE " + COL_LOG_USER_EMAIL + "=? AND " +
                        COL_LOG_HABIT + "=? AND " +
                        COL_LOG_DATE + "=? AND " +
                        COL_LOG_DONE + "=1",
                new String[]{userEmail, String.valueOf(habitId), date}
        );
        boolean done = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return done;
    }

    public int getDailyCompletionStatus(String userEmail, String date) {
        List<Habit> habits = getAllHabitsList(userEmail);
        if (habits.isEmpty()) return 0;

        int total = habits.size();
        int done = 0;
        for (Habit h : habits) {
            if (isHabitDoneToday(userEmail, h.getId(), date)) done++;
        }

        if (done == 0) return 0;
        if (done < total) return 1;
        return 2;
    }

    public int getStreak(String userEmail, int habitId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_LOG_DATE + " FROM " + TABLE_LOGS +
                        " WHERE " + COL_LOG_USER_EMAIL + "=? AND " +
                        COL_LOG_HABIT + "=? AND " + COL_LOG_DONE + "=1" +
                        " ORDER BY " + COL_LOG_DATE + " DESC",
                new String[]{userEmail, String.valueOf(habitId)}
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
                } else {
                    break;
                }
            } catch (Exception e) {
                break;
            }
        }

        cursor.close();
        db.close();
        return streak;
    }

    public void saveJournal(String userEmail, String date, String text, String mood) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_JOURNAL_USER_EMAIL, userEmail);
        cv.put(COL_J_DATE, date);
        cv.put(COL_J_TEXT, text);
        cv.put(COL_J_MOOD, mood);
        db.insert(TABLE_JOURNAL, null, cv);
        db.close();
    }

    public Cursor getJournalByDate(String userEmail, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_JOURNAL + " WHERE " +
                        COL_JOURNAL_USER_EMAIL + "=? AND " + COL_J_DATE + "=?",
                new String[]{userEmail, date}
        );
    }
}