package com.example.shane_kruse.habbithub;

import android.database.sqlite.SQLiteDatabase;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;

public class Task {
    private String descr;           //Description of Task
    private int goal;               //Number of times Task should be completed
    private int prog;               //Current progress towards the goal
    private LocalTime due_date; //Date/Time that the task must be completed by
    private String icon;            //Icon ID
    private boolean completed;      //Has the goal been met
    private ArrayList<String> interval;        //M, T, W, EVERYDAY, 4, BI-WEEKLY, START, WHOLE, etc
    private boolean repeat;         //On or off to repeat task every interval type
    private String color;           //Color hex
    private int row_id;             //Row ID in Database
    private boolean on_watch;       //Is task on smartwatch
    private String abbrev;          //Abbreviation for smartwatch
    private boolean active;         //Task is in progress
    private LocalDateTime completed_time;
    private DbHandler dbh;


    public Task(String descr, int goal, int prog, LocalTime due_date, String icon,
                boolean completed, ArrayList<String> interval, boolean repeat,
                String color, boolean on_watch, String abbrev, boolean active,
                LocalDateTime completed_time) {

        this.descr = descr;
        this.goal = goal;
        this.prog = prog;
        this.due_date = due_date;
        this.icon = icon;
        this.completed = completed;
        this.interval = interval;
        this.repeat = repeat;
        this.color = color;
        this.row_id = -1;
        this.on_watch = on_watch;
        this.abbrev = abbrev;
        this.active = active;
        this.completed_time = completed_time;
        this.dbh = MainActivity.dbh;
    }

    // Copy another Task object for repeat
    public Task (Task t) {
        this.descr = t.descr;
        this.goal = t.goal;
        this.prog = 0;
        this.due_date = t.due_date;
        this.icon = t.icon;
        this.completed = false;
        this.interval = t.interval;
        this.repeat = t.repeat;
        this.color = t.color;
        this.row_id = -1;
        this.on_watch = t.on_watch;
        this.abbrev = t.abbrev;
        this.active = true;
        this.completed_time = null;
    }

    public int incrementProg(){
        prog = dbh.incrementTask(row_id);

        if (prog >= goal) {
            completed = true;
            if (!repeat) active = false;
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
        SQLiteDatabase db = dbh.getWritableDatabase();
        prog = dbh.getProg(row_id);

        if (prog >= goal) {
            completed = true;
            if (!repeat) active = false;
        }
        return prog;
    }

    public void setProg(int prog) {
        this.prog = prog;
    }

    public LocalTime getDue_date() {
        return due_date;
    }

    public void setDue_date(LocalTime due_date) {
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

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCompleted_time() {
        return completed_time;
    }

    public void setCompleted_time(LocalDateTime completed_time) {
        this.completed_time = completed_time;
    }

}
