package com.example.shane_kruse.habbithub;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


public class ScheduleActivity extends AppCompatActivity {
    Toolbar mToolbar;
    Button mondayBtn, tuesdayBtn, wednesdayBtn, thursdayBtn,
            fridayBtn, saturdayBtn, sundayBtn, timePickerBtn,
            everyDayBtn, pickTimeBtn;
    CheckBox anytimeChkBox;
    Switch repeatSwitch, watchSwitch;
    TimePicker duedatePicker;
    private View timePopup;
    TextView save;
    EditText abbrevText;
    com.shawnlin.numberpicker.NumberPicker dailyNumPicker;
    Context context;
    private DbHandler dbh = new DbHandler(ScheduleActivity.this);

    boolean isEverydaySelected = false;

    private String descr;               //Description of Task
    private int goal;                   //Number of times Task should be completed
    private int prog = 0;               //Current progress towards the goal
    private LocalTime due_date;         //Date/Time that the task must be completed by
    private String icon;                //Icon ID
    private boolean completed = false;  //Has the goal been met
    private boolean repeat;             //On or off to repeat task every interval type
    private String color;               //Color hex
    private boolean on_watch = false;           //Is task on smartwatch
    private String abbrev;              //Abbreviation for smartwatch
    private boolean active = true;      //Task in progress
    private LocalDateTime time_completed = null;
    private boolean time_selected = false;

    private boolean edit = false;
    private int id;
    private int goalEdit;
    private String due_dateEdit;
    private String intervalEdit;
    private boolean repeatEdit;
    private boolean on_watchEdit;
    private String abbrevEdit;

    ArrayList<Button> buttonList;

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
                // Get list of days selected
                ArrayList<String> intervalList = new ArrayList<>();
                for (Button b : buttonList) {
                    if (b.isSelected()) intervalList.add(b.getText().toString());
                }

                // Set the time the task is due
                int hour, minute;
                if (anytimeChkBox.isChecked()) {
                    hour = 23;
                    minute = 59;
                }
                else {
                    hour = duedatePicker.getHour();
                    minute = duedatePicker.getMinute();
                }

                due_date = LocalTime.of(hour, minute);

                // Check if scheduling info was entered
                if (intervalList.isEmpty() || !time_selected) {
                    showPopup();
                    return;
                }

                // Set goal from numberpicker
                goal = dailyNumPicker.getValue();


                // Update the smartwatch
                String newTaskMsg = "";
                System.out.println("Task assinged to watch: " + on_watch);
                // Send added task to watch if on watch selected
                if (on_watch) {
                    newTaskMsg = getString(R.string.new_smartwatch_task);
                }

                // Update database
                Intent i  = new Intent(ScheduleActivity.this, MainActivity.class);

                if (edit)
                    dbh.updateTask(id, descr, goal, prog, due_date, icon, completed,
                            intervalList, repeat, color, on_watch, abbrev);

                // Add to database
                else {
                    dbh.insertTask(descr, goal, prog, due_date, icon, completed,
                            intervalList, repeat, color, on_watch, abbrev);

                    i.putExtra("taskMsg", newTaskMsg);
                }

                // Return to dashboard
                startActivity(i);
            }
        });

        // Back button
        TextView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        everyDayBtn = findViewById(R.id.interval_everyday_btn);
        everyDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEverydaySelected = true;
                clearWeekdayBtns(buttonList);
                everyDayBtn.setBackground(getDrawable(R.drawable.rounded_schedule_btn_selected));
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

        if (dbh.getNumWatchTasks() > 5)
            watchSwitch.setEnabled(false);

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
                    if (abbrevEdit != null)
                        abbrevText.setText(abbrevEdit);

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

        // Setup day buttons
        buttonList = new ArrayList<>();

        sundayBtn = findViewById(R.id.interval_sunday_btn);
        buttonList.add(sundayBtn);

        mondayBtn = findViewById(R.id.interval_monday_btn);
        buttonList.add(mondayBtn);

        tuesdayBtn = findViewById(R.id.interval_tuesday_btn);
        buttonList.add(tuesdayBtn);

        wednesdayBtn = findViewById(R.id.interval_wednesday_btn);
        buttonList.add(wednesdayBtn);

        thursdayBtn = findViewById(R.id.interval_thursday_btn);
        buttonList.add(thursdayBtn);

        fridayBtn = findViewById(R.id.interval_friday_btn);
        buttonList.add(fridayBtn);

        saturdayBtn = findViewById(R.id.interval_saturday_btn);
        buttonList.add(saturdayBtn);

        // Setup Buttons
        setupIntervalBtn(buttonList);

        // Setup NumberPickers
        dailyNumPicker = findViewById(R.id.num_picker);

        // Enable animated layout changes
        ((ViewGroup) findViewById(R.id.schedule_layout)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        // Pick time button
        pickTimeBtn = findViewById(R.id.pick_time_btn);
        pickTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePopup();
            }
        });

        // Time Popup
        LayoutInflater timeInflater = getLayoutInflater();
        timePopup = timeInflater.inflate(R.layout.time_popup_daily, null);
        duedatePicker = timePopup.findViewById(R.id.time_picker);
        anytimeChkBox = timePopup.findViewById(R.id.anytime_checkbox);

        // Read Task info from create page
        Bundle data = getIntent().getExtras();
        descr = (String) data.get("desc");
        icon = (String) data.get("icon");
        color = (String) data.get("hex");

        if (data.get("due_date") != null) {
            edit = true;

            // Read data from intent
            id = (int) data.get("id");
            goalEdit = (int) data.get("goal");
            due_dateEdit = (String) data.get("due_date");
            intervalEdit = (String) data.get("interval");
            repeatEdit = (boolean) data.get("repeat");
            on_watchEdit = (boolean) data.get("on_watch");
            abbrevEdit = (String) data.get("abbrev");

            goal = goalEdit;
            due_date = LocalTime.parse(due_dateEdit);
            repeat = repeatEdit;
            on_watch = on_watchEdit;
            abbrev = abbrevEdit;

            // Update UI on delay (after it loads)
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Switches
                    repeatSwitch.setChecked(repeatEdit);
                    watchSwitch.setChecked(on_watchEdit);

                    // TimePicker
                    if (due_dateEdit.equals("23:59")){
                        anytimeChkBox.setChecked(true);
                        duedatePicker.setEnabled(false);
                    }
                    else {
                        duedatePicker.setHour(due_date.getHour());
                        duedatePicker.setMinute(due_date.getMinute());
                    }

                    // Goal
                    dailyNumPicker.setValue(goalEdit);

                    // Interval Buttons
                    ArrayList<String> intervals = new ArrayList<>(
                            Arrays.asList(intervalEdit.split(",")));

                    for (String s : intervals) {
                        for (Button b: buttonList) {
                            String bText = b.getText().toString();
                            if (bText.equals(s)) {
                                b.performClick();
                                break;
                            }
                        }
                    }
                }
            }, 500);


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

    void showTimePopup() {
        final AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setView(timePopup);

        anytimeChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    duedatePicker.setEnabled(false);
                } else {
                    duedatePicker.setEnabled(true);
                }
            }
        });

        Button saveBtn = timePopup.findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time_selected = true;
                alert.dismiss();
            }
        });

        alert.show();
    }

    void setupIntervalBtn(ArrayList<Button> buttons) {
        for (int i = 0; i < buttons.size(); i++) {
            final Button b = buttons.get(i);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // TODO fix button not deselecting!!!!
                    if (isEverydaySelected) {
                        everyDayBtn.setBackground(getDrawable(R.drawable.rounded_btn_schedule));
                    }
                    b.setBackground(getDrawable(R.drawable.weekday_selected));
                    
                    if (b.isSelected()) b.setSelected(false);
                    else b.setSelected(true);
                }
            });
        }
    }

    // Set weekday buttons back to default
    void clearWeekdayBtns(ArrayList<Button> buttons) {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setBackground(getDrawable(R.drawable.round_weekday_btn));
        }
    }
}
