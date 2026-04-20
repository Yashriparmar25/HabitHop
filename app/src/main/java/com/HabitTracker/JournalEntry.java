package com.HabitTracker;

public class JournalEntry {
    public int id;
    public String date;
    public String time;
    public String mood;
    public String grateful1;
    public String grateful2;
    public String grateful3;
    public String affirmation1;
    public String affirmation2;
    public String affirmation3;
    public String wentWell;
    public String improve;
    public String notes;
    public String tomorrow;
    public int waterCount;

    public JournalEntry(int id, String date, String time, String mood,
                        String grateful1, String grateful2, String grateful3,
                        String affirmation1, String affirmation2, String affirmation3,
                        String wentWell, String improve, String notes, String tomorrow,
                        int waterCount) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.mood = mood;
        this.grateful1 = grateful1;
        this.grateful2 = grateful2;
        this.grateful3 = grateful3;
        this.affirmation1 = affirmation1;
        this.affirmation2 = affirmation2;
        this.affirmation3 = affirmation3;
        this.wentWell = wentWell;
        this.improve = improve;
        this.notes = notes;
        this.tomorrow = tomorrow;
        this.waterCount = waterCount;
    }
}