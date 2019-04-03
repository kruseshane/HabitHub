package com.example.shane_kruse.habbithub;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;

public class Task {
    private String descr;           //Description of Task
    private int goal;               //Number of times Task should be completed
    private int prog;               //Current progress towards the goal
    private ZonedDateTime due_date; //Date/Time that the task must be completed by
    private String icon;            //Icon ID
    private boolean completed;      //Has the goal been met
    private String interval_type;   //Daily, weekly, monthly
    private ArrayList<String> interval;        //M, T, W, EVERYDAY, 4, BI-WEEKLY, START, WHOLE, etc
    private boolean repeat;         //On or off to repeat task every interval type
    private ZonedDateTime reminder_time;     //Set time of day to be reminded about task
    private String color;           //Color hex
    private int row_id;             //Row ID in Database
    private boolean on_watch;       //Is task on smartwatch
    private String abbrev;          //Abbreviation for smartwatch


    public Task(String descr, int goal, int prog, ZonedDateTime due_date, String icon, boolean completed,
                String interval_type, ArrayList<String> interval, boolean repeat, ZonedDateTime reminder_time,
                String color, boolean on_watch, String abbrev) {
        this.descr = descr;
        this.goal = goal;
        this.prog = prog;
        this.due_date = due_date;
        this.icon = icon;
        this.completed = completed;
        this.interval_type = interval_type;
        this.interval = interval;
        this.repeat = repeat;
        this.reminder_time = reminder_time;
        this.color = color;
        this.row_id = -1;
        this.on_watch = on_watch;
        this.abbrev = abbrev;
    }

    public int incrementProg(){
        prog++;
        if (prog >= goal) {
            completed = true;
        }
        return prog;
    }

    public void setRow_id(int id) {
        this.row_id = id;
    }

    public int getRow_id() {
        return this.row_id;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getProg() {
        return prog;
    }

    public void setProg(int prog) {
        this.prog = prog;
    }

    public ZonedDateTime getDue_date() {
        return due_date;
    }

    public void setDue_date(ZonedDateTime due_date) {
        this.due_date = due_date;
}

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public ArrayList<String> getInterval() {
        return interval;
    }

    public void setInterval(ArrayList<String> interval) {
        this.interval = interval;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getInterval_type() {
        return interval_type;
    }

    public void setInterval_type(String interval_type) {
        this.interval_type = interval_type;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public ZonedDateTime getReminder_time() {
        return reminder_time;
    }

    public void setReminder_time(ZonedDateTime reminder_time) {
        this.reminder_time = reminder_time;
    }

    public boolean isOn_watch() {
        return on_watch;
    }

    public void setOn_watch(boolean on_watch) {
        this.on_watch = on_watch;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

}
