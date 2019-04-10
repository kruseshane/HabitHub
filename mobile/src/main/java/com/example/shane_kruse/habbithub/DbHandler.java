package com.example.shane_kruse.habbithub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

public class DbHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "taskdb";

    private static final String TABLE_ACTIVE = "active_tasks";          //Row Index
    private static final String KEY_ROW = "_id";                        //0
    private static final String KEY_DESCR = "descr";                    //1
    private static final String KEY_GOAL = "goal";                      //2
    private static final String KEY_PROG = "prog";                      //3
    private static final String KEY_DUE_DATE = "due_date";              //4
    private static final String KEY_ICON = "icon";                      //5
    private static final String KEY_COMPLETED = "completed";            //6
    private static final String KEY_INTERVAL = "interval";              //7
    private static final String KEY_REPEAT = "repeat";                  //8
    private static final String KEY_COLOR = "color";                    //9
    private static final String KEY_ON_WATCH = "on_watch";              //10
    private static final String KEY_ABBREV = "abbrev";                  //11

    private static final String TABLE_HISTORY = "completed_tasks";
    private static final String KEY_TASK_ID = "task_id";                //12
    private static final String KEY_TIME_COMPLETED = "time_completed";  //13

    private static final String CREATE_TABLE_ACTIVE = "CREATE TABLE IF NOT EXISTS " + TABLE_ACTIVE + " ("
                                                + KEY_ROW + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                + KEY_DESCR + " VARCHAR, " + KEY_GOAL + " INTEGER, "
                                                + KEY_PROG + " INTEGER, " + KEY_DUE_DATE + " TEXT, "
                                                + KEY_ICON + " VARCAHR, " + KEY_COMPLETED + " BIT, "
                                                + KEY_INTERVAL + " VARCHAR, " + KEY_REPEAT + " BIT, "
                                                + KEY_COLOR + " VARCHAR, " + KEY_ON_WATCH + " BIT, "
                                                + KEY_ABBREV + " VARCHAR"
                                                + ")";

    private static final String CREATE_TABLE_HISTORY = "CREATE TABLE IF NOT EXISTS " + TABLE_HISTORY + " ("
                                                    + KEY_ROW + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                    + KEY_DESCR + " VARCHAR, " + KEY_GOAL + " INTEGER, "
                                                    + KEY_PROG + " INTEGER, " + KEY_DUE_DATE + " TEXT, "
                                                    + KEY_ICON + " VARCAHR, " + KEY_COMPLETED + " BIT, "
                                                    + KEY_INTERVAL + " VARCHAR, " + KEY_REPEAT + " BIT, "
                                                    + KEY_COLOR + " VARCHAR, " + KEY_ON_WATCH + " BIT, "
                                                    + KEY_ABBREV + " VARCHAR, " + KEY_TASK_ID + " INTEGER, "
                                                    + KEY_TIME_COMPLETED + " TEXT"
                                                    + ")";

    DbHandler(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ACTIVE);
        db.execSQL(CREATE_TABLE_HISTORY);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        // Create tables again
        onCreate(db);
    }

    public void resetDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);

        db.execSQL(CREATE_TABLE_ACTIVE);
        db.execSQL(CREATE_TABLE_HISTORY);

        db.close();
    }

    ArrayList<Task> loadData() throws ParseException {
        ArrayList<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_ACTIVE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            Task task = new Task(id);
            tasks.add(task);
        }
        cursor.close();
        db.close();
        return tasks;
    }

    int insertTask(String descr, int goal, int prog, LocalTime due_date, String icon,
                    boolean completed, ArrayList<String> interval, boolean repeat,
                    String color, boolean on_watch, String abbrev) {

        // Turn interval into String
        String interval_str = "";
        for (String s: interval) {
            interval_str += s + ",";
        }

        // Convert boolean into 0 or 1 to be inserted into BIT field on database
        int repeatBit = repeat ? 1 : 0;
        int watchBit = on_watch ? 1 : 0;
        int completedBit = completed ? 1 : 0;

        ContentValues cv = new ContentValues();
        cv.put(KEY_DESCR, descr);
        cv.put(KEY_GOAL, goal);
        cv.put(KEY_PROG, prog);
        cv.put(KEY_DUE_DATE, due_date.toString());
        cv.put(KEY_ICON, icon);
        cv.put(KEY_COMPLETED, completedBit);
        cv.put(KEY_INTERVAL, interval_str);
        cv.put(KEY_REPEAT, repeatBit);
        cv.put(KEY_COLOR, color);
        cv.put(KEY_ON_WATCH, watchBit);
        cv.put(KEY_ABBREV, abbrev);

        SQLiteDatabase db = this.getWritableDatabase();
        int row_id = (int) db.insert(TABLE_ACTIVE, null, cv);
        return row_id;
    }



    boolean incrementTask(int rowID) {
        Cursor c = getTask(rowID);
        int new_prog = c.getInt(3) + 1;
        int goal = c.getInt(2);
        boolean completed = c.getInt(6) == 1;
        boolean repeat = c.getInt(8) == 1;
        ContentValues cv = new ContentValues();

        // Update progress
        cv.put(KEY_PROG, new_prog);

        // Update completed and time_completed if at or past goal
        if (new_prog >= goal) {
            cv.put(KEY_COMPLETED, 1);
            completed = true;

            // If the task is set to repeat, reinsert it into the database
            if (repeat) repeatTask(rowID);
            // Otherwise move it to history table
            else {
                SQLiteDatabase db = this.getWritableDatabase();
                db.update(TABLE_ACTIVE, cv, KEY_ROW + " = " + rowID, null);
                db.close();
                copyToHistory(rowID);
                removeTask(rowID);
                return true;
            }
        }

        // Update table
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ACTIVE, cv, KEY_ROW + " = " + rowID, null);
        db.close();

        return completed;
    }

    void repeatTask(int rowID) {
        copyToHistory(rowID);

        // Reset values of task in active table
        ContentValues cv = new ContentValues();
        cv.put(KEY_PROG, 0);
        cv.put(KEY_COMPLETED, 0);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ACTIVE, cv, KEY_ROW + " = " + rowID, null);
    }

    void removeTask(int rowID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTIVE, KEY_ROW + " = " + rowID, null);
        db.close();
    }

    // Get values from active task and copy them into history table
    void copyToHistory(int rowID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = getTask(rowID);

        ContentValues cv = new ContentValues();
        cv.put(KEY_DESCR, c.getString(1));
        cv.put(KEY_GOAL, c.getInt(2));
        cv.put(KEY_PROG, c.getInt(3));
        cv.put(KEY_DUE_DATE, c.getString(4));
        cv.put(KEY_ICON, c.getString(5));
        cv.put(KEY_COMPLETED, c.getInt(6));
        cv.put(KEY_INTERVAL, c.getString(7));
        cv.put(KEY_REPEAT, c.getInt(8));
        cv.put(KEY_COLOR, c.getString(9));
        cv.put(KEY_ON_WATCH, c.getInt(10));
        cv.put(KEY_ABBREV, c.getString(11));
        cv.put(KEY_TASK_ID, rowID);
        cv.put(KEY_TIME_COMPLETED, LocalDateTime.now().toString());

        db.insert(TABLE_HISTORY, null, cv);
    }

    Cursor getTask(int rowID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_ACTIVE + " WHERE " + KEY_ROW + " = " + rowID;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        return c;
    }

    String getDescr(int rowID) {
        Cursor c = getTask(rowID);
        return c.getString(1);
    }

    int getGoal(int rowID) {
        Cursor c = getTask(rowID);
        return c.getInt(2);
    }

    int getProg(int rowID) {
        Cursor c = getTask(rowID);
        return c.getInt(3);
    }

    LocalTime getDueDate(int rowID) {
        Cursor c = getTask(rowID);
        return LocalTime.parse(c.getString(4));
    }

    String getIcon(int rowID) {
        Cursor c = getTask(rowID);
        return c.getString(5);
    }

    void setIcon(int rowID, String icon) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_ICON, icon);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ACTIVE, cv, KEY_ROW + " = " + rowID, null);
        db.close();
    }

    boolean getCompleted(int rowID) {
        Cursor c = getTask(rowID);
        return c.getInt(6) == 1;
    }

    ArrayList<String> getInterval(int rowID) {
        Cursor c = getTask(rowID);
        String interval_str = c.getString(7);

        return  new ArrayList<String>(Arrays.asList(interval_str.split(",")));
    }

    String getColor(int rowID) {
        Cursor c = getTask(rowID);
        return c.getString(9);
    }

    void setColor(int rowID, String color) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_COLOR, color);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ACTIVE, cv, KEY_ROW + " = " + rowID, null);
        db.close();
    }

    public void incrementTaskFromWatch(String abbrev, int newProg) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("UPDATE " + TABLE_ACTIVE + " SET " + KEY_PROG + " = " + newProg
         + " WHERE " + KEY_ABBREV + " = '" + abbrev + "' AND "  + " = 1", null);
        cursor.moveToFirst();
        cursor.close();
    }

    public int getCurrentProg(String taskDesc) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_PROG + " FROM " + TABLE_ACTIVE + " WHERE " + KEY_DESCR
         + " = '" + taskDesc + "'", null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public String getWatchTasks() {
        String s = "";
        SQLiteDatabase db = getReadableDatabase();
        Cursor mCount = db.rawQuery("select count(*) from " + TABLE_ACTIVE, null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        Cursor cursor = db.rawQuery("select * from " + TABLE_ACTIVE, null);
        if (count > 0 && cursor.moveToFirst()) {
            do {
                if (cursor.getInt(12) == 1) {
                    // abbrev, color, prog, goal
                    // TODO change index
                    s += cursor.getString(13) + "," + cursor.getString(11) + "," + cursor.getInt(3) +
                            "," + cursor.getInt(2) + "&";
                }
            } while (cursor.moveToNext());
        }
        return s;
    }
}
