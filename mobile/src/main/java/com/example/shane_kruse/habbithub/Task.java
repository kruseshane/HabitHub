package com.example.shane_kruse.habbithub;

import android.database.sqlite.SQLiteDatabase;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;

public class Task {
    private int row_id;             //Row ID in Database
    private DbHandler dbh;


    // Create a new task and insert it into the database
    public Task(String descr, int goal, int prog, LocalTime due_date, String icon,
                boolean completed, ArrayList<String> interval, boolean repeat,
                String color, boolean on_watch, String abbrev) {

        this.dbh = MainActivity.dbh;
        row_id = dbh.insertTask(descr, goal, prog, due_date, icon, completed, interval,
                                repeat, color, on_watch, abbrev);
    }

    // Load a task from the database
    public Task(int row_id) {
        this.dbh = MainActivity.dbh;
        this.row_id = row_id;
    }

    public boolean incrementProg(){
        return dbh.incrementTask(row_id);
    }

    public int getRow_id() {
        return this.row_id;
    }

    public String getDescr() {
        return dbh.getDescr(row_id);
    }


    public int getGoal() {
        return dbh.getGoal(row_id);
    }


    public int getProg() {
        return dbh.getProg(row_id);
    }


    public LocalTime getDue_date() {
        return dbh.getDueDate(row_id);
    }


    public String getIcon() {
        return dbh.getIcon(row_id);
    }

    public void setIcon(String icon) {
        dbh.setIcon(row_id, icon);
    }

    public boolean isCompleted() {
        return dbh.getCompleted(row_id);
    }


    public ArrayList<String> getInterval() {
        return dbh.getInterval(row_id);
    }

    public String getColor() {
        return dbh.getColor(row_id);
    }

    public void setColor(String color) {
        dbh.setColor(row_id, color);
    }


}
