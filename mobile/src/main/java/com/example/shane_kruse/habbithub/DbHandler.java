package com.example.shane_kruse.habbithub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

public class DbHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "taskdb";

    private static final String TABLE_Task = "task";
    private static final String KEY_ROW = "_id";
    private static final String KEY_DESCR = "descr";
    private static final String KEY_GOAL = "goal";
    private static final String KEY_PROG = "prog";
    private static final String KEY_DUE_DATE = "due_date";
    private static final String KEY_ICON = "icon";
    private static final String KEY_COMPLETED = "completed";
    private static final String KEY_INTERVAL = "interval";
    private static final String KEY_REPEAT = "repeat";
    private static final String KEY_COLOR = "color";
    private static final String KEY_ON_WATCH = "on_watch";
    private static final String KEY_ABBREV = "abbrev";

    private static final String CREATE_TABLE_TASK = "CREATE TABLE IF NOT EXISTS " + TABLE_Task + " ("
                                                + KEY_ROW + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                + KEY_DESCR + " VARCHAR, " + KEY_GOAL + " INTEGER, "
                                                + KEY_PROG + " INTEGER, " + KEY_DUE_DATE + " TEXT, "
                                                + KEY_ICON + " VARCAHR, " + KEY_COMPLETED + " BIT, "
                                                + KEY_INTERVAL + " VARCHAR, " + KEY_REPEAT + " BIT, "
                                                + KEY_COLOR + " VARCHAR, " + KEY_ON_WATCH + " BIT, "
                                                + KEY_ABBREV + " VARCHAR"
                                                + ")";

    DbHandler(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TASK);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Task);
        // Create tables again
        onCreate(db);
    }

    public void resetDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Task);
        db.execSQL(CREATE_TABLE_TASK);
        db.close();
    }

    ArrayList<Task> loadData() throws ParseException {
        ArrayList<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_Task;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String descr = cursor.getString(1);
            int goal = cursor.getInt(2);
            int prog = cursor.getInt(3);
            LocalTime due_date = LocalTime.parse(cursor.getString(4));
            String icon = cursor.getString(5);
            boolean completed = cursor.getInt(6) == 1;
            String interval_str = cursor.getString(7);
            boolean repeat = (1 == cursor.getInt(8));
            String color = cursor.getString(9);
            boolean on_watch = (1 == cursor.getInt(10));
            String abbrev = cursor.getString(11);

            // Turn string into ArrayList<String>
            String[] interval_list = interval_str.split(",");
            ArrayList<String> interval = new ArrayList<String>(Arrays.asList(interval_list));

            Task task = new Task(descr, goal, prog, due_date, icon, completed,
                                interval, repeat, color, on_watch, abbrev);
            tasks.add(task);
            task.setRow_id(id);
        }
        cursor.close();
        db.close();
        return tasks;
    }

    void insertTask(Task t) {
        int completed;
        if (t.isCompleted()) completed = 1;
        else completed = 0;

        // Turn interval into String
        String interval_str = "";
        for (String s: t.getInterval()) {
            interval_str += s + ",";
        }

        // Convert boolean into 0 or 1 to be inserted into BIT field on database
        int repeatBit = t.isRepeat() ? 1 : 0;
        int watchBit = t.isOn_watch() ? 1 : 0;

        ContentValues cv = new ContentValues();
        cv.put(KEY_DESCR, t.getDescr());
        cv.put(KEY_GOAL, t.getGoal());
        cv.put(KEY_PROG, t.getProg());
        cv.put(KEY_DUE_DATE, t.getDue_date().toString());
        cv.put(KEY_ICON, t.getIcon());
        cv.put(KEY_COMPLETED, completed);
        cv.put(KEY_INTERVAL, interval_str);
        cv.put(KEY_REPEAT, repeatBit);
        cv.put(KEY_COLOR, t.getColor());
        cv.put(KEY_ON_WATCH, watchBit);
        cv.put(KEY_ABBREV, t.getAbbrev());

        SQLiteDatabase db = this.getWritableDatabase();
        int row_id = (int) db.insert(TABLE_Task, null, cv);
        t.setRow_id(row_id);
        System.out.println(row_id);
    }

    int incrementTask(Task t) {
        int new_prog  = t.incrementProg();
        int row = t.getRow_id();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("UPDATE " + TABLE_Task + " SET " + KEY_PROG + " = " + new_prog
                                    + " WHERE " + KEY_ROW + " = " + row, null);

        cursor.moveToFirst();
        cursor.close();
        db.close();
        return new_prog;
    }

    public void incrementTaskFromWatch(String abbrev, int newProg) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("UPDATE " + TABLE_Task + " SET " + KEY_PROG + " = " + newProg
         + " WHERE " + KEY_ABBREV + " = '" + abbrev + "'", null);
        cursor.moveToFirst();
        cursor.close();
    }

    public int getCurrentProg(String taskDesc) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_PROG + " FROM " + TABLE_Task + " WHERE " + KEY_DESCR
         + " = '" + taskDesc + "'", null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public String getWatchTasks() {
        String s = "";
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCount = db.rawQuery("select count(*) from " + TABLE_Task, null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        Cursor cursor = db.rawQuery("select * from " + TABLE_Task, null);
        if (count > 0 && cursor.moveToFirst()) {
            do {
                if (cursor.getInt(12) == 1) {
                    s += cursor.getString(13) + "," + cursor.getString(11) + "," + cursor.getInt(3) +
                            "," + cursor.getInt(2) + "&";
                }
            } while (cursor.moveToNext());
        }

        return s;
    }
}
