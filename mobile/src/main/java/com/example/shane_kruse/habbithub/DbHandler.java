package com.example.shane_kruse.habbithub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DbHandler extends SQLiteOpenHelper {
    private static ArrayList<Task> tasks;

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
    private static final String KEY_COLOR = "color";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_Task + " ("
                                                + KEY_ROW + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                                + KEY_DESCR + " VARCHAR, " + KEY_GOAL + " INTEGER, "
                                                + KEY_PROG + " INTEGER, " + KEY_DUE_DATE + " DATETIME, "
                                                + KEY_ICON + " VARCAHR, " + KEY_COMPLETED + " BIT, "
                                                + KEY_INTERVAL + " VARCHAR, " + KEY_COLOR + " VARCHAR"
                                                + ")";

    public DbHandler(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
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
        db.execSQL(CREATE_TABLE);
        db.close();
    }

    public ArrayList<Task> loadData() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'", Locale.ENGLISH);
        ArrayList<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_Task;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String descr = cursor.getString(1);
            int goal = cursor.getInt(2);
            int prog = cursor.getInt(3);

            String date_str = cursor.getString(4);
            Date due_date = formatter.parse(date_str);

            String icon = cursor.getString(5);

            boolean completed;
            int completed_int = cursor.getInt(6);
            if (completed_int == 1) completed = true;
            else completed = false;

            String interval = cursor.getString(7);
            String color = cursor.getString(8);

            Date temp_due_date = new Date();

            Task task = new Task(descr, goal, prog, temp_due_date, icon, completed, interval, color);
            tasks.add(task);
            task.setRow_id(id);
        }
        db.close();
        this.tasks = tasks;
        return tasks;
    }

    public void insertTask(Task t) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        String date_str = df.format(t.getDue_date());

        int completed;
        if (t.isCompleted()) completed = 1;
        else completed = 0;

        ContentValues cv = new ContentValues();
        cv.put(KEY_DESCR, t.getDescr());
        cv.put(KEY_GOAL, t.getGoal());
        cv.put(KEY_PROG, t.getProg());
        cv.put(KEY_DUE_DATE, date_str);
        cv.put(KEY_ICON, t.getIcon());
        cv.put(KEY_COMPLETED, completed);
        cv.put(KEY_INTERVAL, t.getInterval());
        cv.put(KEY_COLOR, t.getColor());

        SQLiteDatabase db = this.getWritableDatabase();
        int row_id = (int) db.insert(TABLE_Task, null, cv);
        t.setRow_id(row_id);
        System.out.println(row_id);
    }

    public int incrementTask(Task t) {
        int new_prog  = t.incrementProg();
        SQLiteDatabase db = this.getWritableDatabase();
        db.rawQuery("UPDATE " + TABLE_Task + " SET " + KEY_PROG + " = " + new_prog + " WHERE " + KEY_ROW + " = " + t.getRow_id(), null);
        return new_prog;
    }
}
