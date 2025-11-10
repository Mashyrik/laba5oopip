package com.example.laba5;

import java.io.Serializable;

public abstract class AbstractExcursion implements Serializable {
    private String place;
    private String dayType;
    private String timeOfDay;
    private String guideLevel;

    public AbstractExcursion(String place, String dayType, String timeOfDay, String guideLevel) {
        this.place = place;
        this.dayType = dayType;
        this.timeOfDay = timeOfDay;
        this.guideLevel = guideLevel;
    }

    // Геттеры
    public String getPlace() { return place; }
    public String getDayType() { return dayType; }
    public String getTimeOfDay() { return timeOfDay; }
    public String getGuideLevel() { return guideLevel; }

    public abstract void displayDetails();
}