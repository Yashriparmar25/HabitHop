package com.HabitTracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "HabitTracker.db";
    private static final int DB_VERSION = 9;

    public static final String TABLE_USERS = "users";
    public static final String TABLE_HABITS = "habits";
    public static final String TABLE_LOGS = "habit_logs";
    public static final String TABLE_JOURNAL = "journal";

    public static final String COL_ID = "id";
    public static final String COL_USER_EMAIL = "user_email";

    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_NAME = "name";
    public static final String COL_BIRTHDAY = "birthday";
    public static final String COL_GENDER = "gender";
    public static final String COL_GOAL = "goal";
    public static final String COL_AVATAR_RES = "avatar_res";
    public static final String COL_AVATAR_URI = "avatar_uri";

    public static final String COL_HABIT_NAME = "habit_name";
    public static final String COL_HABIT_DESC = "habit_desc";
    public static final String COL_CATEGORY = "category";
    public static final String COL_FREQUENCY = "frequency";
    public static final String COL_CREATED_AT = "created_at";

    public static final String COL_LOG_HABIT = "habit_id";
    public static final String COL_LOG_DATE = "log_date";
    public static final String COL_LOG_DONE = "is_done";

    public static final String COL_J_DATE = "entry_date";
    public static final String COL_J_TIME = "entry_time";
    public static final String COL_J_TEXT = "entry_text";
    public static final String COL_J_MOOD = "mood";
    public static final String COL_J_GRATEFUL1 = "grateful1";
    public static final String COL_J_GRATEFUL2 = "grateful2";
    public static final String COL_J_GRATEFUL3 = "grateful3";
    public static final String COL_J_AFFIRMATION1 = "affirmation1";
    public static final String COL_J_AFFIRMATION2 = "affirmation2";
    public static final String COL_J_AFFIRMATION3 = "affirmation3";
    public static final String COL_J_WENT_WELL = "went_well";
    public static final String COL_J_IMPROVE = "improve";
    public static final String COL_J_NOTES = "notes";
    public static final String COL_J_TOMORROW = "tomorrow";
    public static final String COL_J_WATER = "water_count";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_EMAIL + " TEXT UNIQUE, "
                + COL_USER_PASSWORD + " TEXT, "
                + COL_NAME + " TEXT, "
                + COL_BIRTHDAY + " TEXT, "
                + COL_GENDER + " TEXT, "
                + COL_GOAL + " TEXT, "
                + COL_AVATAR_RES + " TEXT, "
                + COL_AVATAR_URI + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_HABITS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_EMAIL + " TEXT, "
                + COL_HABIT_NAME + " TEXT, "
                + COL_HABIT_DESC + " TEXT, "
                + COL_CATEGORY + " TEXT, "
                + COL_FREQUENCY + " TEXT, "
                + COL_CREATED_AT + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_LOGS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_EMAIL + " TEXT, "
                + COL_LOG_HABIT + " INTEGER, "
                + COL_LOG_DATE + " TEXT, "
                + COL_LOG_DONE + " INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_JOURNAL + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_EMAIL + " TEXT, "
                + COL_J_DATE + " TEXT, "
                + COL_J_TIME + " TEXT, "
                + COL_J_TEXT + " TEXT, "
                + COL_J_MOOD + " TEXT, "
                + COL_J_GRATEFUL1 + " TEXT, "
                + COL_J_GRATEFUL2 + " TEXT, "
                + COL_J_GRATEFUL3 + " TEXT, "
                + COL_J_AFFIRMATION1 + " TEXT, "
                + COL_J_AFFIRMATION2 + " TEXT, "
                + COL_J_AFFIRMATION3 + " TEXT, "
                + COL_J_WENT_WELL + " TEXT, "
                + COL_J_IMPROVE + " TEXT, "
                + COL_J_NOTES + " TEXT, "
                + COL_J_TOMORROW + " TEXT, "
                + COL_J_WATER + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOURNAL);
        onCreate(db);
    }

    public void saveUser(String email, String name, String birthday, String gender, String goal, String avatarRes, String avatarUri) {
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

    public int updateUserProfile(String email, String name, String birthday, String gender, String goal, String avatarRes, String avatarUri) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_BIRTHDAY, birthday);
        values.put(COL_GENDER, gender);
        values.put(COL_GOAL, goal);
        values.put(COL_AVATAR_RES, avatarRes);
        values.put(COL_AVATAR_URI, avatarUri);
        return db.update(TABLE_USERS, values, COL_USER_EMAIL + "=?", new String[]{email});
    }

    public boolean userExists(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "=? LIMIT 1", new String[]{email});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public void saveUserCredentials(String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_PASSWORD, password);
        db.update(TABLE_USERS, values, COL_USER_EMAIL + "=?", new String[]{email});
    }

    public String getUserName(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_NAME + " FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "=?", new String[]{email});
        String name = "Friend";
        if (cursor.moveToFirst()) name = cursor.getString(0);
        cursor.close();
        return name;
    }

    public long saveHabit(String userEmail, String habitName, String habitDesc, String category, String frequency, String createdAt) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_EMAIL, userEmail);
        values.put(COL_HABIT_NAME, habitName);
        values.put(COL_HABIT_DESC, habitDesc);
        values.put(COL_CATEGORY, category);
        values.put(COL_FREQUENCY, frequency);
        values.put(COL_CREATED_AT, createdAt);
        return db.insert(TABLE_HABITS, null, values);
    }

    public List<Habit> getAllHabitsList(String userEmail) {
        List<Habit> habits = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HABITS + " WHERE " + COL_USER_EMAIL + "=? ORDER BY " + COL_ID + " DESC", new String[]{userEmail});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
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

    public void updateHabitLog(String userEmail, int habitId, String date, boolean done) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LOG_DONE, done ? 1 : 0);

        int rows = db.update(TABLE_LOGS, values,
                COL_USER_EMAIL + "=? AND " + COL_LOG_HABIT + "=? AND " + COL_LOG_DATE + "=?",
                new String[]{userEmail, String.valueOf(habitId), date});

        if (rows == 0) {
            ContentValues insertValues = new ContentValues();
            insertValues.put(COL_USER_EMAIL, userEmail);
            insertValues.put(COL_LOG_HABIT, habitId);
            insertValues.put(COL_LOG_DATE, date);
            insertValues.put(COL_LOG_DONE, done ? 1 : 0);
            db.insert(TABLE_LOGS, null, insertValues);
        }
    }

    public boolean isHabitDoneToday(String userEmail, int habitId, String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_LOG_DONE + " FROM " + TABLE_LOGS +
                        " WHERE " + COL_USER_EMAIL + "=? AND " + COL_LOG_HABIT + "=? AND " + COL_LOG_DATE + "=?",
                new String[]{userEmail, String.valueOf(habitId), date}
        );

        boolean done = false;
        if (cursor.moveToFirst()) done = cursor.getInt(0) == 1;
        cursor.close();
        return done;
    }

    public int getDailyCompletionStatus(String userEmail, String date) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor habitCursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_HABITS + " WHERE " + COL_USER_EMAIL + "=?",
                new String[]{userEmail}
        );

        int totalHabits = 0;
        if (habitCursor.moveToFirst()) {
            totalHabits = habitCursor.getInt(0);
        }
        habitCursor.close();

        if (totalHabits == 0) return 0;

        Cursor doneCursor = db.rawQuery(
                "SELECT COUNT(DISTINCT " + COL_LOG_HABIT + ") FROM " + TABLE_LOGS +
                        " WHERE " + COL_USER_EMAIL + "=? AND " + COL_LOG_DATE + "=? AND " + COL_LOG_DONE + "=1",
                new String[]{userEmail, date}
        );

        int doneHabits = 0;
        if (doneCursor.moveToFirst()) {
            doneHabits = doneCursor.getInt(0);
        }
        doneCursor.close();

        if (doneHabits == 0) return 0;
        if (doneHabits == totalHabits) return 2;
        return 1;
    }

    public int getDailyCompletionStreak(String userEmail) {
        SQLiteDatabase db = getReadableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        int streak = 0;

        while (true) {
            String date = sdf.format(cal.getTime());

            Cursor habitCursor = db.rawQuery(
                    "SELECT COUNT(*) FROM " + TABLE_HABITS + " WHERE " + COL_USER_EMAIL + "=?",
                    new String[]{userEmail}
            );

            int totalHabits = 0;
            if (habitCursor.moveToFirst()) {
                totalHabits = habitCursor.getInt(0);
            }
            habitCursor.close();

            if (totalHabits == 0) break;

            Cursor doneCursor = db.rawQuery(
                    "SELECT COUNT(DISTINCT " + COL_LOG_HABIT + ") FROM " + TABLE_LOGS +
                            " WHERE " + COL_USER_EMAIL + "=? AND " + COL_LOG_DATE + "=? AND " + COL_LOG_DONE + "=1",
                    new String[]{userEmail, date}
            );

            int doneHabits = 0;
            if (doneCursor.moveToFirst()) {
                doneHabits = doneCursor.getInt(0);
            }
            doneCursor.close();

            if (doneHabits == totalHabits) {
                streak++;
                cal.add(Calendar.DATE, -1);
            } else {
                break;
            }
        }

        return streak;
    }

    public int getStreak(String userEmail, int habitId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_LOGS +
                        " WHERE " + COL_USER_EMAIL + "=? AND " + COL_LOG_HABIT + "=? AND " + COL_LOG_DONE + "=1",
                new String[]{userEmail, String.valueOf(habitId)});

        int streak = 0;
        if (cursor.moveToFirst()) streak = cursor.getInt(0);
        cursor.close();
        return streak;
    }

    public long saveJournalEntry(String userEmail, String entryDate, String entryTime, String mood,
                                 String text, String grateful1, String grateful2, String grateful3,
                                 String affirmation1, String affirmation2, String affirmation3,
                                 String wentWell, String improve, String notes, String tomorrow,
                                 int waterCount) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_EMAIL, userEmail);
        values.put(COL_J_DATE, entryDate);
        values.put(COL_J_TIME, entryTime);
        values.put(COL_J_MOOD, mood);
        values.put(COL_J_TEXT, text);
        values.put(COL_J_GRATEFUL1, grateful1);
        values.put(COL_J_GRATEFUL2, grateful2);
        values.put(COL_J_GRATEFUL3, grateful3);
        values.put(COL_J_AFFIRMATION1, affirmation1);
        values.put(COL_J_AFFIRMATION2, affirmation2);
        values.put(COL_J_AFFIRMATION3, affirmation3);
        values.put(COL_J_WENT_WELL, wentWell);
        values.put(COL_J_IMPROVE, improve);
        values.put(COL_J_NOTES, notes);
        values.put(COL_J_TOMORROW, tomorrow);
        values.put(COL_J_WATER, waterCount);

        return db.insert(TABLE_JOURNAL, null, values);
    }

    public Cursor getAllJournalEntries(String userEmail) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_JOURNAL +
                        " WHERE " + COL_USER_EMAIL + "=? ORDER BY " + COL_ID + " DESC",
                new String[]{userEmail});
    }

    public Cursor getJournalEntriesForDate(String userEmail, String date) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_JOURNAL +
                        " WHERE " + COL_USER_EMAIL + "=? AND " + COL_J_DATE + "=? ORDER BY " + COL_ID + " DESC",
                new String[]{userEmail, date});
    }

    public void unlogHabit(String userEmail, int habitId, String today) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_LOGS,
                COL_USER_EMAIL + "=? AND " + COL_LOG_HABIT + "=? AND " + COL_LOG_DATE + "=?",
                new String[]{userEmail, String.valueOf(habitId), today});
    }

    public String getSavedPassword(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_USER_PASSWORD + " FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "=?", new String[]{email});
        String password = null;
        if (cursor.moveToFirst()) password = cursor.getString(0);
        cursor.close();
        return password;
    }

    public Cursor getUser(String currentUserEmail) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_EMAIL + "=?", new String[]{currentUserEmail});
    }

    public void deleteUserAccount(String userEmail) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_LOGS, COL_USER_EMAIL + "=?", new String[]{userEmail});
        db.delete(TABLE_JOURNAL, COL_USER_EMAIL + "=?", new String[]{userEmail});
        db.delete(TABLE_HABITS, COL_USER_EMAIL + "=?", new String[]{userEmail});
        db.delete(TABLE_USERS, COL_USER_EMAIL + "=?", new String[]{userEmail});
    }
}