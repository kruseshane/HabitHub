package com.example.shane_kruse.habbithub;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ScheduleActivity extends AppCompatActivity {
    Toolbar mToolbar;
    Button dailyBtn, weeklyBtn, monthlyBtn, timepickerBtn;
    Switch repeatSwitch, watchSwitch;
    TimePicker duedatePicker;
    TextView save;
    EditText abbrevText;
    NumberPicker dailyNumPicker, weeklyNumPicker, monthlyNumPicker;
    Context context;

    private String descr;           //Description of Task
    private int goal;               //Number of times Task should be completed
    private int prog = 0;               //Current progress towards the goal
    private ZonedDateTime due_date; //Date/Time that the task must be completed by
    private String icon;            //Icon ID
    private boolean completed = false;      //Has the goal been met
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

        context = getApplicationContext();

        setContentView(R.layout.activity_schedule_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        // Save Button
        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interval.isEmpty() || due_date == null) {
                    showPopup();
                    return;
                }

                // Read goal based on interval type
                switch(interval_type) {
                    case "DAILY":
                        goal = dailyNumPicker.getValue();
                        break;
                    case "WEEKLY":
                        goal = weeklyNumPicker.getValue();
                        break;
                    case "MONTHLY":
                        goal = monthlyNumPicker.getValue();
                }

                String newTaskMsg = "";
                System.out.println("Task assinged to watch: " + on_watch);
                // Send added task to watch if on watch selected
                if (on_watch) {
                    //newTaskMsg = abbrev + "," + color + "," + prog + "," + goal + "&";
                    newTaskMsg = getString(R.string.new_smartwatch_task);
                }

                // Add to database
                DbHandler hand = new DbHandler(ScheduleActivity.this);
                hand.insertTask(new Task(descr, goal, prog, due_date, icon,
                        completed, interval_type, interval, repeat,
                        ZonedDateTime.now(), color, on_watch, abbrev));

                // Return to dashboard
                Intent i  = new Intent(ScheduleActivity.this, MainActivity.class);
                i.putExtra("taskMsg", newTaskMsg);
                startActivity(i);
            }
        });

        TextView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Buttons
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

        timepickerBtn = findViewById(R.id.timepicker_btn);
        timepickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimepickerPopup();
            }
        });

        // Switches
        repeatSwitch = findViewById(R.id.repeat_switch);
        repeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    repeat = true;
                } else {
                    repeat = false;
                }
            }
        });

        watchSwitch = findViewById(R.id.watch_task_switch);
        watchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    on_watch = true;
                    LayoutInflater inflater = getLayoutInflater();
                    View popup = inflater.inflate(R.layout.abbrev_popup, null);
                    final AlertDialog alert = new AlertDialog.Builder(ScheduleActivity.this).create();
                    alert.setView(popup);

                    abbrevText = popup.findViewById(R.id.abbrev_text);
                    Button okBtn = popup.findViewById(R.id.ok_button);
                    okBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (String.valueOf(abbrevText.getText()).length() < 6) {
                                abbrev = String.valueOf(abbrevText.getText());
                                alert.dismiss();
                            }
                        }
                    });
                    alert.show();

                } else on_watch = false;
            }
        });

        // TimePicker
        duedatePicker = findViewById(R.id.time_picker);

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
    }

    public void setIntervalDisplay(String interval_type) {
        final int INDEX = 2;
        LinearLayout parent_layout = findViewById(R.id.schedule_layout);
        parent_layout.removeViewAt(INDEX);

        ArrayList<Button> buttonList = new ArrayList<>();
        ArrayList<String> intervalList = new ArrayList<>();

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
                final Button sundayBtn = findViewById(R.id.interval_sunday_btn);
                buttonList.add(sundayBtn);
                intervalList.add("SUNDAY");

                sundayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!interval.contains("SUNDAY")) interval.add("SUNDAY");
                    }
                });

                Button mondayBtn = findViewById(R.id.interval_monday_btn);
                buttonList.add(mondayBtn);
                intervalList.add("MONDAY");

                Button tuesdayBtn = findViewById(R.id.interval_tuesday_btn);
                buttonList.add(tuesdayBtn);
                intervalList.add("TUESDAY");

                Button wednesdayBtn = findViewById(R.id.interval_wednesday_btn);
                buttonList.add(wednesdayBtn);
                intervalList.add("WEDNESDAY");

                Button thursdayBtn = findViewById(R.id.interval_thursday_btn);
                buttonList.add(thursdayBtn);
                intervalList.add("THURSDAY");

                Button fridayBtn = findViewById(R.id.interval_friday_btn);
                buttonList.add(fridayBtn);
                intervalList.add("FRIDAY");

                Button saturdayBtn = findViewById(R.id.interval_saturday_btn);
                buttonList.add(saturdayBtn);
                intervalList.add("SATURDAY");

                // Setup Buttons
                setupIntervalBtn(buttonList, intervalList);

                // Setup NumberPickers
                dailyNumPicker = findViewById(R.id.daily_times_per_day_picker);
                dailyNumPicker.setMinValue(0);
                dailyNumPicker.setMaxValue(10);
                dailyNumPicker.setValue(5);

                break;

            case "WEEKLY":
                LinearLayout weekly_view = (LinearLayout) getLayoutInflater().inflate(
                        R.layout.activity_schedule_weekly, parent_layout, false);
                parent_layout.addView(weekly_view, INDEX);
                interval_type = "WEEKLY";
                interval.clear();

                Button biweeklyBtn = findViewById(R.id.interval_biweekly_btn);
                buttonList.add(biweeklyBtn);
                intervalList.add("BIWEEKLY");

                setupIntervalBtn(buttonList, intervalList);

                weeklyNumPicker = findViewById(R.id.weekly_days_picker);

                break;

            case "MONTHLY":
                LinearLayout monthly_view = (LinearLayout) getLayoutInflater().inflate(
                        R.layout.activity_schedule_monthly, parent_layout, false);
                parent_layout.addView(monthly_view, INDEX);
                interval_type = "MONTHLY";
                interval.clear();

                Button startBtn = findViewById(R.id.interval_start_btn);
                buttonList.add(startBtn);
                intervalList.add("START");

                Button midBtn = findViewById(R.id.interval_mid_btn);
                buttonList.add(midBtn);
                intervalList.add("MID");

                Button endBtn = findViewById(R.id.interval_end_btn);
                buttonList.add(endBtn);
                intervalList.add("END");

                Button anytimeBtn = findViewById(R.id.interval_whole_btn);
                buttonList.add(anytimeBtn);
                intervalList.add("ANY");

                setupIntervalBtn(buttonList, intervalList);

                monthlyNumPicker = findViewById(R.id.times_per_month_picker);

                break;
        }
    }

    void showPopup() {
        LayoutInflater inflater = getLayoutInflater();
        View popup = inflater.inflate(R.layout.not_completed_popup, null);
        final AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setView(popup);

        Button dismissBtn = popup.findViewById(R.id.dismissBtn);
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        alert.show();
    }

    void showTimepickerPopup() {
        LayoutInflater inflater = getLayoutInflater();
        View popup = inflater.inflate(R.layout.timepicker_popup, null);
        final AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setView(popup);

        duedatePicker = popup.findViewById(R.id.time_picker);

        final Button anytimeBtn = popup.findViewById(R.id.anytimeBtn);
        anytimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (anytimeBtn.isSelected()) {
                    anytimeBtn.setBackgroundColor(Color.GRAY);
                    anytimeBtn.setSelected(false);
                }
                else {
                    anytimeBtn.setBackgroundColor(Color.GREEN);
                    anytimeBtn.setSelected(true);
                }
            }
        });

        final Button saveBtn = popup.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set due date
                int hour = 24;
                int minute = 59;
                if (!anytimeBtn.isSelected()) {
                    hour = duedatePicker.getHour();
                    minute = duedatePicker.getMinute();
                }
                due_date = ZonedDateTime.now();
                due_date.withHour(hour);
                due_date.withMinute(minute);

                alert.dismiss();
            }
        });

        alert.show();
    }

    void setupIntervalBtn(ArrayList<Button> buttons, ArrayList<String> intervals) {
        for (int i = 0; i < buttons.size(); i++) {
            final Button b = buttons.get(i);
            final String inter = intervals.get(i);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!interval.contains(inter)) interval.add(inter);
                    b.setSelected(true);
                    b.setBackgroundColor(Color.GREEN);
                }
            });

        }
    }
}
