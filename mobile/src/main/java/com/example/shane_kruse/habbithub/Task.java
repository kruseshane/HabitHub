package com.example.shane_kruse.habbithub;

public class Task {
    private String descr;
    private int count;
    private int current_count;


    public Task(String descr, int count) {
        this.descr = descr;
        this.count = count;
        this.current_count = 1;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCurrent_count() {
        return current_count;
    }

    public void setCurrent_count(int current_count) {
        this.current_count = current_count;
    }
}
