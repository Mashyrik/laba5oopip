package com.example.laba5;

public class Excursion extends AbstractExcursion {
    public Excursion(String place, String dayType, String timeOfDay, String guideLevel) {
        super(place, dayType, timeOfDay, guideLevel);
    }

    @Override
    public void displayDetails() {
        System.out.println("Excursion to " + getPlace() +
                " | Day: " + getDayType() +
                " | Time: " + getTimeOfDay() +
                " | Guide: " + getGuideLevel());
    }
}