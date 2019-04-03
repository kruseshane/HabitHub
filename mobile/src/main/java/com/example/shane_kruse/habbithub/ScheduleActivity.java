package com.example.shane_kruse.habbithub;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Objects;


public class ScheduleActivity extends AppCompatActivity {
    Toolbar mToolbar;
    Button dailyBtn, weeklyBtn, monthlyBtn, anytimeBtn;
    CheckBox repeatCheck;

    private String descr;           //Description of Task
    private int goal;               //Number of times Task should be completed
    private int prog;               //Current progress towards the goal
    private ZonedDateTime due_date; //Date/Time that the task must be completed by
    private String icon;            //Icon ID
    private boolean completed;      //Has the goal been met
    private String interval_type;   //Daily, weekly, monthly
    private ArrayList<String> interval;        //M, T, W, EVERYDAY, 4, BI-WEEKLY, START, WHOLE, etc
    private boolean repeat;         //On or off to repeat task every interval type
    private ZonedDateTime reminder_time;     //Set time of day to be reminded about task
    private String color;           //Color hex
    private int row_id;             //Row ID in Database
    private boolean on_watch;       //Is task on smartwatch
    private String abbrev;          //Abbreviation for smartwatch

    protected void onCreate (Bundle savedInstance) {
        // Initialize layout
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_schedule_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        // Enable animated layout changes
        ((ViewGroup) findViewById(R.id.schedule_layout)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        // Read Task info from create page
        Bundle data = getIntent().getExtras();
        descr = (String) data.get("desc");
        icon = (String) data.get("icon");
        color = (String) data.get("hex");

        // Set current interval layout to daily
        interval = new ArrayList<>();
        setIntervalDisplay("DAILY");
        interval_type = "DAILY";

        // Save Button
        TextView save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHandler hand = new DbHandler(ScheduleActivity.this);
                hand.insertTask(new Task(descr, 1, 0, ZonedDateTime.now(), icon,
                        false, "daily", "EVERYDAY", false,
                        ZonedDateTime.now(), color, false, "n/a"));

                Intent i  = new Intent(ScheduleActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        // Buttons for interval type
        dailyBtn = findViewById(R.id.interval_daily_btn);
        dailyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntervalDisplay("DAILY");
            }
        });

        weeklyBtn = findViewById(R.id.interval_weekly_btn);
        weeklyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntervalDisplay("WEEKLY");
            }
        });

        monthlyBtn = findViewById(R.id.interval_monthly_btn);
        monthlyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntervalDisplay("MONTHLY");
            }
        });

        repeatCheck = findViewById(R.id.repeat_checkbox);
    }

    public void setIntervalDisplay(String interval_type) {
        final int INDEX = 2;
        LinearLayout parent_layout = findViewById(R.id.schedule_layout);
        parent_layout.removeViewAt(INDEX);

        switch (interval_type) {
            case "DAILY":
                // Inflate new layout
                LinearLayout daily_view = (LinearLayout) getLayoutInflater().inflate(
                        R.layout.activity_schedule_daily, parent_layout, false);

                // Add layout to view at appropriate index
                parent_layout.addView(daily_view, INDEX);
                interval_type = "DAILY";
                interval.clear();

                // Setup new buttons
                Button sundayBtn = findViewById(R.id.interval_sunday_btn);
                sundayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!interval.contains("SUNDAY")) interval.add("SUNDAY");
                    }
                });

                Button mondayBtn = findViewById(R.id.interval_monday_btn);
                mondayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!interval.contains("MONDAY")) interval.add("MONDAY");
                    }
                });

                Button tuesdayBtn = findViewById(R.id.interval_tuesday_btn);
                tuesdayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!interval.contains("TUESDAY")) interval.add("TUESDAY");
                    }
                });

                Button wednesdayBtn = findViewById(R.id.interval_wednesday_btn);
                wednesdayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!interval.contains("WEDNESDAY")) interval.add("WEDNESDAY");
                    }
                });

                Button thursdayBtn = findViewById(R.id.interval_thursday_btn);
                thursdayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!interval.contains("THURSDAY")) interval.add("THURSDAY");
                    }
                });

                Button fridayBtn = findViewById(R.id.interval_friday_btn);
                fridayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!interval.contains("FRIDAY")) interval.add("FRIDAY");
                    }
                });

                Button saturdayBtn = findViewById(R.id.interval_saturday_btn);
                saturdayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!interval.contains("SATURDAY")) interval.add("SATURDAY");
                    }
                });

                break;

            case "WEEKLY":
                LinearLayout weekly_view = (LinearLayout) getLayoutInflater().inflate(
                        R.layout.activity_schedule_weekly, parent_layout, false);
                parent_layout.addView(weekly_view, INDEX);
                interval_type = "WEEKLY";
                interval.clear();

                Button biweeklyBtn = findViewById(R.id.interval_biweekly_btn);
                biweeklyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!interval.contains("BIWEEKLY")) interval.add("BIWEEKLY");
                    }
                });

                break;

            case "MONTHLY":
                LinearLayout monthly_view = (LinearLayout) getLayoutInflater().inflate(
                        R.layout.activity_schedule_monthly, parent_layout, false);
                parent_layout.addView(monthly_view, INDEX);
                interval_type = "MONTHLY";
                interval.clear();

                Button startBtn = findViewById(R.id.interval_start_btn);
                startBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!interval.contains("START")) interval.add("START");
                    }
                });

                Button midBtn = findViewById(R.id.interval_mid_btn);
                midBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!interval.contains("MID")) interval.add("MID");
                    }
                });

                Button endBtn = findViewById(R.id.interval_end_btn);
                endBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!interval.contains("END")) interval.add("END");
                    }
                });

                Button anytimeBtn = findViewById(R.id.interval_whole_btn);
                anytimeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!interval.contains("ANY")) interval.add("ANY");
                    }
                });

                break;
        }
    }
}
