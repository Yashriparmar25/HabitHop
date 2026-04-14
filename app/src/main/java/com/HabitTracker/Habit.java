package com.HabitTracker;

public class Habit{
    private int id;
    private String name;
    private String description;
    private String category;
    private String frequency;
    private String dateCreated;

    public Habit(int id, String name, String description,
                 String category, String frequency, String dateCreated) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.category    = category;
        this.frequency   = frequency;
        this.dateCreated = dateCreated;
    }

    public int    getId()          { return id; }
    public String getName()        { return name; }
    public String getDescription() { return description; }
    public String getCategory()    { return category; }
    public String getFrequency()   { return frequency; }
    public String getDateCreated() { return dateCreated; }
}