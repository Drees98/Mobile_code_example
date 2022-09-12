package com.example.schedulingapp;

class TimeBlock {
    private int startHour, startMinute, endHour, endMinute;

    //Constructor
    TimeBlock(int startHour, int startMinute, int endHour, int endMinute){
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    int getStartHour(){
        return startHour;
    }

    int getStartMinute(){
        return startMinute;
    }

    int getEndHour(){
        return endHour;
    }

    int getEndMinute(){
        return endMinute;
    }

    @Override
    public String toString(){
        return ("sH: " + startHour + " sM: " + startMinute + " eH: " + endHour
                + " eM: " + endMinute);
    }

}
