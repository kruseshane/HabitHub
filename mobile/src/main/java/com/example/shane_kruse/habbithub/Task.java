package com.example.shane_kruse.habbithub;

public class Task {
    private String descr;
    private int count;

    public Task(String descr, int count) {
        this.descr = descr;
        this.count = count;
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
}
