package com.example.shane_kruse.habbithub;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Task> tasks = new ArrayList<>();
    private RecyclerView taskRecycler;
    private MyTaskAdapter mAdapter;
    private Toolbar mToolbar;
    private Handler myHandler; // was protected
    private int total;
    private float sum;
    DonutProgress progressBarOverall;
    private ImageView addTask;
    private ImageView menuOptions;
    private DbHandler dbh = new DbHandler(this);
    private Button todayButton;
    private Button upcomingButton;
    private Button completedButton;
    private String newTaskMsg;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create a message handler//
        myHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle stuff = msg.getData();
                return true;
            }
        });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        newTaskMsg = intent.getStringExtra("taskMsg");
        System.out.println("taskMsg = " + newTaskMsg);
        if (newTaskMsg != null && newTaskMsg.equals(getString(R.string.new_smartwatch_task))) {
            System.out.println("Sending " + newTaskMsg + " to watch");
            new NewThread("/my_path", newTaskMsg).start();
        }

        tasks = null;
        try {
            tasks = dbh.loadData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        progressBarOverall = findViewById(R.id.goal_progress_overall);
        setProgressBarAttributes();

        if (tasks.size() < 1) {
            updateGoalProgress(0, 0);
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                total += tasks.get(i).getGoal();
                sum += tasks.get(i).getProg();
            }
            updateGoalProgress(sum, total);
        }

        todayButton = findViewById(R.id.today_button);
        todayButton.setPressed(true); // Default to today

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
        mAdapter = new MyTaskAdapter(R.layout.task_recycler, tasks, MainActivity.this);
        taskRecycler = (RecyclerView) findViewById(R.id.task_list);
        taskRecycler.setLayoutManager(new LinearLayoutManager(this));
        taskRecycler.setItemAnimator(new DefaultItemAnimator());
        taskRecycler.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 101 && resultCode == RESULT_OK) {
            if (data != null) {
                int position = data.getIntExtra("task_pos", 0);
                String new_desc = data.getStringExtra("task_desc");
                int new_goal = data.getIntExtra("task_goal", 0);
                Task task = tasks.get(position);
                TextView taskView = (TextView) taskRecycler.getLayoutManager().findViewByPosition(position);
            }
        }
    }

    //Define a nested class that extends BroadcastReceiver//
    public class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Upon receiving each message from the wearable, display the following text//

            String message = intent.getStringExtra("message");
            //System.out.println(message);
            if (message.equals("REQUEST_UPDATE")) {
                System.out.println("REQUEST_UPDATE RECEIVED");
                //dbh.getWatchTasks();
                String updateMsg = dbh.getWatchTasks();

                // Send update to watch
                new NewThread("/my_path", updateMsg).start();

                System.out.println("UPDATE SENT");

            }
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void updateGoalProgress(float sum, int total) {
        DecimalFormat df = new DecimalFormat("#.#");
        for (int i = 0; i < tasks.size(); i++) {
            total += tasks.get(i).getGoal();
            sum += tasks.get(i).getProg();
        }

        float prog = (sum/total) * 100;

        progressBarOverall.setProgress(Float.parseFloat(df.format(prog)));
    }

    private void setProgressBarAttributes() {
        progressBarOverall.setFinishedStrokeWidth(45);
        progressBarOverall.setUnfinishedStrokeWidth(45);
    }

    class NewThread extends Thread {
        String path;
        String message;

        //Constructor for sending information to the Data Layer//
        NewThread(String p, String m) {
            path = p;
            message = m;
        }

        public void run() {

            System.out.println("Sending " + message + " to watch");
            //Retrieve the connected devices, known as nodes//
            com.google.android.gms.tasks.Task<List<Node>> mobileDeviceList =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {

                List<Node> nodeList = Tasks.await(mobileDeviceList);
                for (Node n : nodeList) {
                    com.google.android.gms.tasks.Task<Integer> sendMessageTask =

                            //Send the message//
                            Wearable.getMessageClient(MainActivity.this).sendMessage(n.getId(), path, message.getBytes());

                    try {

                        //Block on a task and get the result synchronously//
                        Integer result = Tasks.await(sendMessageTask);
                        sendmessage(message);
                        System.out.println(message + " sent");

                        //if the Task fails, then…..//
                    } catch (ExecutionException exception) {
                        //TO DO: Handle the exception//
                    } catch (InterruptedException exception) {
                        //TO DO: Handle the exception//
                    }
                }

            } catch (ExecutionException exception) {
                //TO DO: Handle the exception//
            } catch (InterruptedException exception) {
                //TO DO: Handle the exception//
            }

        }

        //Use a Bundle to encapsulate our message//
        public void sendmessage(String messageText) {
            Bundle bundle = new Bundle();
            bundle.putString("messageText", messageText);
            System.out.println(myHandler.toString());
            Message msg = myHandler.obtainMessage();
            msg.setData(bundle);
            myHandler.sendMessage(msg);

        }
    }
}
