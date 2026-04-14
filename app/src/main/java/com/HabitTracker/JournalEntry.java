package com.HabitTracker;

public class JournalEntry {
    private final String text;
    private final String mood;
    private final String time;

    public JournalEntry(String text, String mood, String time) {
        this.text = text;
        this.mood = mood;
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public String getMood() {
        return mood;
    }

    public String getTime() {
        return time;
    }
}