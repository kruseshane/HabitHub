package com.example.shane_kruse.habbithub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class DbHandler extends SQLiteOpenHelper {
    MainActivity mainAct;

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

    DbHandler(Context context, MainActivity mainAct) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mainAct = mainAct;
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

    ArrayList<Task> loadToday() {
        ArrayList<Task> tasks = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        String query = "SELECT * FROM " + TABLE_ACTIVE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            int today = calendar.get(Calendar.DAY_OF_WEEK);
            int id = cursor.getInt(0);
            Task task = new Task(id, true);

            for (String dayAbrev : task.getInterval()) {
                int day_due = getDay(dayAbrev);
                if (today == day_due) {
                    tasks.add(task);
                    break;
                }
            }
        }
        return tasks;
    }

    ArrayList<Task> loadUpcoming() {
        ArrayList<Task> tasks = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        String query = "SELECT * FROM " + TABLE_ACTIVE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            boolean isCurrent = false;
            int today = calendar.get(Calendar.DAY_OF_WEEK);
            int id = cursor.getInt(0);
            Task task = new Task(id, true);

            for (String dayAbrev : task.getInterval()) {
                int day_due = getDay(dayAbrev);
                if (today == day_due) {
                    isCurrent = true;
                    break;
                }
            }
            // Check if the task is upcoming
            if (!isCurrent) tasks.add(task);
        }
        return tasks;
    }

    ArrayList<Task> loadHistory() {
        ArrayList<Task> tasks = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_HISTORY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            System.out.println(cursor.getString(13));
            int id = cursor.getInt(0);
            Task task = new Task(id, false);
            tasks.add(task);
        }
        cursor.close();
        db.close();
        return tasks;
    }

    int[] getDailyProgress() {
        int totalGoal = 0;
        int totalProg = 0;

        // Check tasks due today
        Calendar calendar = Calendar.getInstance();
        String queryActive = "SELECT * FROM " + TABLE_ACTIVE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursorActive = db.rawQuery(queryActive, null);

        while (cursorActive.moveToNext()) {
            int today = calendar.get(Calendar.DAY_OF_WEEK);
            int id = cursorActive.getInt(0);
            Task task = new Task(id, true);

            for (String dayAbrev : task.getInterval()) {
                int day_due = getDay(dayAbrev);
                if (today == day_due) {
                    totalGoal += task.getGoal();
                    totalProg += task.getProg();
                }
            }
        }

        // Check history for tasks completed today
        String today = LocalDate.now().toString();
        String queryHist = "SELECT * FROM " + TABLE_HISTORY + " WHERE "
                            + KEY_TIME_COMPLETED + " LIKE " + "'" + today + "%'";
        Cursor cursorHist = db.rawQuery(queryHist, null);

        while (cursorHist.moveToNext()) {
            totalGoal += cursorHist.getInt(2);
            totalProg += cursorHist.getInt(3);
        }

        return new int[] {totalGoal, totalProg};
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
        Cursor c = getTask(rowID, true);
        int new_prog = c.getInt(3) + 1;
        int goal = c.getInt(2);
        boolean completed = c.getInt(6) == 1;
        boolean repeat = c.getInt(8) == 1;
        ContentValues cv = new ContentValues();

        // Update progress
        cv.put(KEY_PROG, new_prog);
        mainAct.incrementProg();

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

    int repeatTask(int rowID) {
        int newRowID = copyToHistory(rowID);

        // Reset values of task in active table
        ContentValues cv = new ContentValues();
        cv.put(KEY_PROG, 0);
        cv.put(KEY_COMPLETED, 0);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ACTIVE, cv, KEY_ROW + " = " + rowID, null);

        return newRowID;
    }

    void removeTask(int rowID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACTIVE, KEY_ROW + " = " + rowID, null);
        db.close();
    }

    // Get values from active task and copy them into history table
    int copyToHistory(int rowID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = getTask(rowID, true);

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

        int row = (int)db.insert(TABLE_HISTORY, null, cv);
        return row;
    }

    Cursor getTask(int rowID, boolean active) {
        String table;
        if (active) table = TABLE_ACTIVE;
        else table = TABLE_HISTORY;

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + table + " WHERE " + KEY_ROW + " = " + rowID;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        return c;
    }

    String getDescr(int rowID, boolean active) {
        Cursor c = getTask(rowID, active);
        return c.getString(1);
    }

    int getGoal(int rowID, boolean active) {
        Cursor c = getTask(rowID, active);
        return c.getInt(2);
    }

    int getProg(int rowID, boolean active) {
        Cursor c = getTask(rowID, active);
        return c.getInt(3);
    }

    LocalTime getDueDate(int rowID, boolean active) {
        Cursor c = getTask(rowID, active);
        return LocalTime.parse(c.getString(4));
    }

    String getIcon(int rowID, boolean active) {
        Cursor c = getTask(rowID, active);
        return c.getString(5);
    }

    void setIcon(int rowID, String icon) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_ICON, icon);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ACTIVE, cv, KEY_ROW + " = " + rowID, null);
        db.close();
    }

    boolean getCompleted(int rowID, boolean active) {
        Cursor c = getTask(rowID, active);
        return c.getInt(6) == 1;
    }

    ArrayList<String> getInterval(int rowID, boolean active) {
        Cursor c = getTask(rowID, active);
        String interval_str = c.getString(7);

        return  new ArrayList<String>(Arrays.asList(interval_str.split(",")));
    }

    String getColor(int rowID, boolean active) {
        Cursor c = getTask(rowID, active);
        return c.getString(9);
    }

    void setColor(int rowID, String color) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_COLOR, color);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_ACTIVE, cv, KEY_ROW + " = " + rowID, null);
        db.close();
    }

    String getAbbrev(int rowID, boolean active) {
        Cursor c = getTask(rowID, active);
        return c.getString(11);
    }

    boolean isOnWatch(int rowID, boolean active) {
        Cursor c = getTask(rowID, active);
        return c.getInt(10) == 1;
    }

    public Boolean incrementTaskFromWatch(String abbrev, int newProg) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("UPDATE " + TABLE_ACTIVE + " SET " + KEY_PROG + " = " + newProg
         + " WHERE " + KEY_ABBREV + " = '" + abbrev + "'", null);
        cursor.moveToFirst();
        cursor.close();
        db = this.getReadableDatabase();
        Cursor check = db.rawQuery("SELECT prog,goal FROM " + TABLE_ACTIVE + " WHERE abbrev = '" + abbrev
         + "'", null);
        check.moveToFirst();
        if (check.getInt(0) == check.getInt(1)) {
            return true;
        } else {
            return false;
        }
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
        ArrayList<Task> today = loadToday();
        for (Task task : today) {
            if (task.isOnWatch()) {
                System.out.println(task.getRow_id());
                s += task.getAbbrev() + "," + task.getColor() + "," + task.getProg() + "," + task.getGoal() +
                        "," + task.getRow_id() + "&";
            }
        }
        return s;
    }

    int getDay(String dayAbrev) {
        int day = -1;
        switch(dayAbrev) {
            case "M":
                day = Calendar.MONDAY;
                break;
            case "T":
                day = Calendar.TUESDAY;
                break;
            case "W":
                day = Calendar.WEDNESDAY;
                break;
            case "TR":
                day = Calendar.THURSDAY;
                break;
            case "F":
                day = Calendar.FRIDAY;
                break;
            case "SA":
                day = Calendar.SATURDAY;
                break;
            case "SU":
                day = Calendar.SUNDAY;
                break;
        }
        return day;
    }
}
