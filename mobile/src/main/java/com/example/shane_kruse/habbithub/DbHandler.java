package com.example.shane_kruse.habbithub;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DbHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "taskdb";
    private static final String TABLE_Task = "task";
    private static final String KEY_DESCR = "descr";
    private static final String KEY_GOAL = "goal";
    private static final String KEY_PROG = "prog";
    private static final String KEY_DUE_DATE = "due_date";
    private static final String KEY_ICON = "icon";
    private static final String KEY_COMPLETED = "completed";
    private static final String KEY_INTERVAL = "interval";
    private static final String KEY_COLOR = "color";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_Task + " (" + KEY_DESCR + " VARCHAR, "
                                                + KEY_GOAL + " INTEGER, " + KEY_PROG + " INTEGER, "
                                                + KEY_DUE_DATE + " DATETIME, " + KEY_ICON + " VARCAHR, "
                                                + KEY_COMPLETED + " BIT, " + KEY_INTERVAL + " VARCHAR, "
                                                + KEY_COLOR + " VARCHAR" + ")";

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

    public ArrayList<Task> loadData() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS", Locale.ENGLISH);
        ArrayList<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_Task;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            String descr = cursor.getString(0);
            int goal = cursor.getInt(1);
            int prog = cursor.getInt(2);

            String date_str = cursor.getString(3);
            Date due_date = formatter.parse(date_str);

            String icon = cursor.getString(4);

            boolean completed;
            int completed_int = cursor.getInt(5);
            if (completed_int == 1) completed = true;
            else completed = false;

            String interval = cursor.getString(6);
            String color = cursor.getString(7);

            Date temp_due_date = new Date();

            Task task = new Task(descr, goal, prog, temp_due_date, icon, completed, interval, color);
            tasks.add(task);
        }
        return tasks;
    }

    public void insertTask(Task t) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        String date_str = df.format(t.getDue_date());

        int completed;
        if (t.isCompleted()) completed = 1;
        else completed = 0;

        String query = "INSERT INTO " + TABLE_Task + " (" + KEY_DESCR + " " +
                        KEY_GOAL + " " + KEY_PROG + " " + KEY_DUE_DATE + " " +
                        KEY_ICON + " " + KEY_COMPLETED + " " + KEY_INTERVAL + " " +
                        KEY_COLOR + ") VALUES (" +
                        t.getDescr() + ", " + t.getGoal() + ", " + t.getProg() + ", " +
                        date_str + ", " + t.getIcon() + ", " + completed + ", " +
                        t.getInterval() + ", " + t.getColor() + ")";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
    }
}
