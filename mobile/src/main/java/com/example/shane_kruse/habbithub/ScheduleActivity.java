package com.example.shane_kruse.habbithub;

import android.animation.LayoutTransition;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


public class ScheduleActivity extends AppCompatActivity {
    Toolbar mToolbar;
    String taskDesc, taskIcon, taskHex;

    protected void onCreate (Bundle savedInstance) {
        // Initialize layout
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_schedule_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Enable animated layout changes
        ((ViewGroup) findViewById(R.id.schedule_layout)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        // Read Task info from create page
        Bundle data = getIntent().getExtras();
        taskDesc = (String) data.get("desc");
        taskIcon = (String) data.get("icon");
        taskHex = (String) data.get("hex");

        // Set current interval layout to daily
        setIntervalDisplay("DAILY");

        // Buttons for days
        Button daily_btn = findViewById(R.id.interval_daily_btn);
        daily_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntervalDisplay("DAILY");
            }
        });

        Button weekly_btn = findViewById(R.id.interval_weekly_btn);
        weekly_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntervalDisplay("WEEKLY");
            }
        });

        Button monthly_btn = findViewById(R.id.interval_monthly_btn);
        monthly_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntervalDisplay("MONTHLY");
            }
        });
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
                break;

            case "WEEKLY":
                LinearLayout weekly_view = (LinearLayout) getLayoutInflater().inflate(
                        R.layout.activity_schedule_weekly, parent_layout, false);
                parent_layout.addView(weekly_view, INDEX);
                break;

            case "MONTHLY":
                LinearLayout monthly_view = (LinearLayout) getLayoutInflater().inflate(
                        R.layout.activity_schedule_monthly, parent_layout, false);
                parent_layout.addView(monthly_view, INDEX);
                break;
        }
    }
}
