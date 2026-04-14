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
    private static final int DB_VERSION = 7;

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
    public static final String COL_J_TIME = "entry_time";
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
                + COL_LOG_DONE + " INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_JOURNAL + " ("
                + COL_JOURNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_JOURNAL_USER_EMAIL + " TEXT, "
                + COL_J_DATE + " TEXT, "
                + COL_J_TIME + " TEXT, "
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
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_EMAIL, email);
        values.put(COL_NAME, name);
        values.put(COL_BIRTHDAY, birthday);
        values.put(COL_GENDER, gender);
        values.put(COL_GOAL, goal);
        values.put(COL_AVATAR_RES, avatarRes);
        values.put(COL_AVATAR_URI, avatarUri);
        db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void saveUserCredentials(String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_PASSWORD, password);
        db.update(TABLE_USERS, values, COL_USER_EMAIL + "=?", new String[]{email});
    }

    public Cursor getUser(String email) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "=?",
                new String[]{email}
        );
    }

    public String getSavedPassword(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_USER_PASSWORD + " FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "=?",
                new String[]{email}
        );
        String password = "";
        if (cursor.moveToFirst()) {
            password = cursor.getString(0);
        }
        cursor.close();
        return password;
    }

    public String getUserName(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_NAME + " FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "=?",
                new String[]{email}
        );
        String name = "Friend";
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        return name;
    }

    public long saveHabit(String userEmail, String habitName, String habitDesc,
                          String category, String frequency, String createdAt) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_HABIT_USER_EMAIL, userEmail);
        values.put(COL_HABIT_NAME, habitName);
        values.put(COL_HABIT_DESC, habitDesc);
        values.put(COL_CATEGORY, category);
        values.put(COL_FREQUENCY, frequency);
        values.put(COL_CREATED_AT, createdAt);
        return db.insert(TABLE_HABITS, null, values);
    }

    public long addHabit(String userEmail, String habitName, String habitDesc,
                         String category, String frequency, String createdAt) {
        return saveHabit(userEmail, habitName, habitDesc, category, frequency, createdAt);
    }

    public List<Habit> getAllHabitsList(String userEmail) {
        List<Habit> habits = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_HABITS +
                        " WHERE " + COL_HABIT_USER_EMAIL + "=? " +
                        "ORDER BY " + COL_HABIT_ID + " DESC",
                new String[]{userEmail}
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_HABIT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_HABIT_NAME));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(COL_HABIT_DESC));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY));
                String frequency = cursor.getString(cursor.getColumnIndexOrThrow(COL_FREQUENCY));
                String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COL_CREATED_AT));
                habits.add(new Habit(id, name, desc, category, frequency, createdAt));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return habits;
    }

    public long logHabit(String userEmail, int habitId, String date, boolean done) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LOG_USER_EMAIL, userEmail);
        values.put(COL_LOG_HABIT, habitId);
        values.put(COL_LOG_DATE, date);
        values.put(COL_LOG_DONE, done ? 1 : 0);
        return db.insert(TABLE_LOGS, null, values);
    }

    public void updateHabitLog(String userEmail, int habitId, String date, boolean done) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LOG_DONE, done ? 1 : 0);

        int rows = db.update(
                TABLE_LOGS,
                values,
                COL_LOG_USER_EMAIL + "=? AND " + COL_LOG_HABIT + "=? AND " + COL_LOG_DATE + "=?",
                new String[]{userEmail, String.valueOf(habitId), date}
        );

        if (rows == 0) {
            logHabit(userEmail, habitId, date, done);
        }
    }

    public void unlogHabit(String userEmail, int habitId, String date) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                TABLE_LOGS,
                COL_LOG_USER_EMAIL + "=? AND " + COL_LOG_HABIT + "=? AND " + COL_LOG_DATE + "=?",
                new String[]{userEmail, String.valueOf(habitId), date}
        );
    }

    public boolean isHabitDoneToday(String userEmail, int habitId, String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_LOG_DONE + " FROM " + TABLE_LOGS +
                        " WHERE " + COL_LOG_USER_EMAIL + "=? AND " + COL_LOG_HABIT + "=? AND " + COL_LOG_DATE + "=?",
                new String[]{userEmail, String.valueOf(habitId), date}
        );

        boolean done = false;
        if (cursor.moveToFirst()) {
            done = cursor.getInt(0) == 1;
        }
        cursor.close();
        return done;
    }

    public int getDailyCompletionStatus(String userEmail, String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_LOGS +
                        " WHERE " + COL_LOG_USER_EMAIL + "=? AND " + COL_LOG_DATE + "=? AND " + COL_LOG_DONE + "=1",
                new String[]{userEmail, date}
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count == 0 ? 0 : 2;
    }

    public int getStreak(String userEmail, int habitId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_LOGS +
                        " WHERE " + COL_LOG_USER_EMAIL + "=? AND " + COL_LOG_HABIT + "=? AND " + COL_LOG_DONE + "=1",
                new String[]{userEmail, String.valueOf(habitId)}
        );

        int streak = 0;
        if (cursor.moveToFirst()) {
            streak = cursor.getInt(0);
        }
        cursor.close();
        return streak;
    }

    public void saveJournal(String userEmail, String entryDate, String entryTime, String text, String mood) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_JOURNAL_USER_EMAIL, userEmail);
        values.put(COL_J_DATE, entryDate);
        values.put(COL_J_TIME, entryTime);
        values.put(COL_J_TEXT, text);
        values.put(COL_J_MOOD, mood);
        db.insert(TABLE_JOURNAL, null, values);
    }

    public Cursor getJournalEntriesForDate(String userEmail, String entryDate) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_JOURNAL +
                        " WHERE " + COL_JOURNAL_USER_EMAIL + "=? AND " + COL_J_DATE + "=? " +
                        "ORDER BY " + COL_JOURNAL_ID + " DESC",
                new String[]{userEmail, entryDate}
        );
    }

    public Cursor getAllJournalEntries(String userEmail) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_JOURNAL +
                        " WHERE " + COL_JOURNAL_USER_EMAIL + "=? " +
                        "ORDER BY " + COL_JOURNAL_ID + " DESC",
                new String[]{userEmail}
        );
    }
}