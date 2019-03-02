package com.example.shane_kruse.habbithub;

import java.util.Date;

public class Task {
    private String descr;       //Description of Task
    private int goal;           //Number of times Task should be completed
    private int prog;           //Current progress towards the goal
    private Date due_date;      //Date/Time that the task must be completed by
    private String icon;        //Icon ID
    private boolean completed;  //Has the goal been met
    private String interval;    //Daily, weekly, monthly, etc
    private String color;       //Color hex
    private int row_id;


    public Task(String descr, int goal, int prog, Date due_date, String icon, boolean completed, String interval, String color) {
        this.descr = descr;
        this.goal = goal;
        this.prog = prog;
        this.due_date = due_date;
        this.icon = icon;
        this.completed = completed;
        this.interval = interval;
        this.color = color;
        this.row_id = 0;
    }

    public void setRow_id(int row) {
        this.row_id = row;
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

    public Date getDue_date() {
        return due_date;
    }

    public void setDue_date(Date due_date) {
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

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}
