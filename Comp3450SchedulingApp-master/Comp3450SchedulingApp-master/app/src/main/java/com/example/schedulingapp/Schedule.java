package com.example.schedulingapp;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private String name;
    List<TimeBlock> monday;
    List<TimeBlock> tuesday;
    List<TimeBlock> wednesday;
    List<TimeBlock> thursday;
    List<TimeBlock> friday;
    List<TimeBlock> saturday;
    List<TimeBlock> sunday;

    //Constructor initializes all of the lists to hold the timeblocks for each day
    public Schedule(String scheduleName) {
        name = scheduleName;
        monday = new ArrayList<>();
        tuesday = new ArrayList<>();
        wednesday = new ArrayList<>();
        thursday = new ArrayList<>();
        friday = new ArrayList<>();
        saturday = new ArrayList<>();
        sunday = new ArrayList<>();
    }
    public String getName(){
        return name;
    }

    //Helper function if needed
    public void addBlock(int startHour, int startMinute, int endHour, int endMinute, int day){
        switch (day){
            case 0:
                monday.add(new TimeBlock(startHour,startMinute,endHour,endMinute));
                break;

            case 1:
                tuesday.add(new TimeBlock(startHour,startMinute,endHour,endMinute));
                break;

            case 2:
                wednesday.add(new TimeBlock(startHour,startMinute,endHour,endMinute));
                break;

            case 3:
                thursday.add(new TimeBlock(startHour,startMinute,endHour,endMinute));
                break;

            case 4:
                friday.add(new TimeBlock(startHour,startMinute,endHour,endMinute));
                break;

            case 5:
                saturday.add(new TimeBlock(startHour,startMinute,endHour,endMinute));
                break;

            case 6:
                sunday.add(new TimeBlock(startHour,startMinute,endHour,endMinute));
                break;

        }
    }
    //Gets a list of timeblocks from the schedule based on the day
    public List<TimeBlock> getTimeBlock(int day) {
        switch (day) {
            case 0:
                return monday;

            case 1:
                return tuesday;

            case 2:
                return wednesday;

            case 3:
                return thursday;

            case 4:
                return friday;

            case 5:
                return saturday;

            case 6:
                return sunday;

        }
        return null;
    }

    @Override
    public String toString(){
        return (name + ": " + "Mon: " + monday + ", Tue: " + tuesday + ", Wed: " + wednesday +
                ", Thu: " + thursday + ", Fri: " + friday + ", Sat: " + saturday +
                ", Sun: " + sunday);
    }
}

