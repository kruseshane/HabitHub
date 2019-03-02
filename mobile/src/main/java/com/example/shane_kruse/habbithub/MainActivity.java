package com.example.shane_kruse.habbithub;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.Date;

public class MainActivity extends AppCompatActivity {


    Date date = new Date();
    Task[] tasks = {new Task("Drink water", 3, 0, date, "n/a", false, "daily", "000FF"),
            new Task("Go for a run", 1, 0, date, "n/a", false, "daily", "000FF"),
            new Task("Do homework", 1, 0, date, "n/a", false, "daily", "000FF"),
            new Task("Eat healthy", 3, 0, date, "n/a", false, "daily", "000FF")};

    private RecyclerView taskRecycler;
    private MyTaskAdapter mAdapter;
    private Toolbar mToolbar;
    private Handler myHandler; // was protected
    private int total;
    private float sum;
    private DonutProgress progressBarOverall;
    private ImageView addTask;
    private ImageView menuOptions;
    private Button todayButton;
    private Button upcomingButton;
    private Button completedButton;

    @SuppressLint("ClickableViewAccessibility")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        addTask = findViewById(R.id.edit_add_task);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editScreen = new Intent(MainActivity.this, EditActivity.class);
                startActivityForResult(editScreen, 101);
            }
        });

        menuOptions = findViewById(R.id.menu_options);

        //Register to receive local broadcasts, which we'll be creating in the next step//
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        // Initialize Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        for (int i = 0; i < tasks.length; i++) {
            total += tasks[i].getGoal();
            sum += tasks[i].getProg();
        }

        progressBarOverall = findViewById(R.id.goal_progress_overall);
        progressBarOverall.setFinishedStrokeWidth(45);
        progressBarOverall.setUnfinishedStrokeWidth(45);
        progressBarOverall.setProgress((sum/total) * 100);

        todayButton = findViewById(R.id.today_button);
        upcomingButton = findViewById(R.id.upcoming_button);
        completedButton = findViewById(R.id.completed_button);

        todayButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                todayButton.setPressed(true);
                upcomingButton.setPressed(false);
                completedButton.setPressed(false);
                return true;
            }
        });

        upcomingButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                todayButton.setPressed(false);
                upcomingButton.setPressed(true);
                completedButton.setPressed(false);
                return true;
            }
        });

        completedButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                todayButton.setPressed(false);
                upcomingButton.setPressed(false);
                completedButton.setPressed(true);
                return true;
            }
        });

        // Create RecyclerView and fill in data from "tasks"
        // aka magic
        mAdapter = new MyTaskAdapter(R.layout.task_recycler, tasks);
        taskRecycler = (RecyclerView) findViewById(R.id.task_list);
        taskRecycler.setLayoutManager(new LinearLayoutManager(this));
        taskRecycler.setItemAnimator(new DefaultItemAnimator());
        taskRecycler.setAdapter(mAdapter);
    }

    /*
    // Inflates menu icons as if it were an ActionBar
    // Whatever that means...
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.toolbar_edit) {
            item.setIcon(R.mipmap.ic_launcher_foreground_add_task);

            for(int i = 0; i < tasks.length; i++) {
                final int position = i;
                final Task task = tasks[i];
                MyTaskAdapter.ViewHolder taskView = (MyTaskAdapter.ViewHolder) taskRecycler.findViewHolderForAdapterPosition(i);
                System.out.println(taskView.task_desc.getText());
                TextView taskTextView = taskView.task_desc;

                taskTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent editScreen = new Intent(MainActivity.this, EditActivity.class);
                        editScreen.putExtra("task_desc", task.getDescr());
                        editScreen.putExtra("task_goal", task.getGoal());
                        editScreen.putExtra("task_pos", position);
                        startActivityForResult(editScreen, 101);
                    }
                });
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 101 && resultCode == RESULT_OK) {
            int position = data.getIntExtra("task_pos", 0);
            String new_desc = data.getStringExtra("task_desc");
            int new_goal = data.getIntExtra("task_goal", 0);
            Task task = tasks[position];
            TextView taskView = (TextView) taskRecycler.getLayoutManager().findViewByPosition(position);

        }
    }

    //Define a nested class that extends BroadcastReceiver//
    public class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Upon receiving each message from the wearable, display the following text//

            String message = intent.getStringExtra("message");
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
