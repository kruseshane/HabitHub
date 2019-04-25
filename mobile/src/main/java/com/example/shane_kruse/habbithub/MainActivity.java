package com.example.shane_kruse.habbithub;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Task> tasks = new ArrayList<>();
    private RecyclerView taskRecycler;
    private MyTaskAdapter mAdapter;
    private Toolbar mToolbar;
    private Handler myHandler; // was protected
    private TextView emptyView;
    DonutProgress progressBarOverall;
    private ImageView addTask;
    private ImageView info;
    public static DbHandler dbh;
    private Button todayButton;
    private Button upcomingButton;
    private Button completedButton;
    private String newTaskMsg;
    private int totalGoal;
    private int totalProg;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbh  = new DbHandler(MainActivity.this, MainActivity.this);

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

        addTask = findViewById(R.id.edit_add_task);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editScreen = new Intent(MainActivity.this, EditActivity.class);
                startActivityForResult(editScreen, 101);
            }
        });

        info = findViewById(R.id.info_img);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, InformationActivity.class);
                startActivity(i);
            }
        });

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

        loadProgress();

        // Default to today
        updateRecycler(dbh.loadToday(), "TODAY");

        todayButton = findViewById(R.id.today_button);
        todayButton.setBackgroundColor(Color.parseColor("#A0A0A0"));
        upcomingButton = findViewById(R.id.upcoming_button);
        completedButton = findViewById(R.id.completed_button);

        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todayButton.setBackgroundColor(Color.parseColor("#A0A0A0"));
                upcomingButton.setBackgroundColor(getColor(R.color.silver));
                completedButton.setBackgroundColor(getColor(R.color.silver));
                updateRecycler(dbh.loadToday(), "TODAY");
            }
        });

        upcomingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todayButton.setBackgroundColor(getColor(R.color.silver));
                upcomingButton.setBackgroundColor(Color.parseColor("#A0A0A0"));
                completedButton.setBackgroundColor(getColor(R.color.silver));
                updateRecycler(dbh.loadUpcoming(), "UPCOMING");
            }
        });

        completedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todayButton.setBackgroundColor(getColor(R.color.silver));
                upcomingButton.setBackgroundColor(getColor(R.color.silver));
                completedButton.setBackgroundColor(Color.parseColor("#A0A0A0"));
                updateRecycler(dbh.loadHistory(), "COMPLETED");
            }
        });
    }

    void updateRecycler(ArrayList<Task> newTaskList, String type) {
        mAdapter = new MyTaskAdapter(R.layout.task_recycler, newTaskList, MainActivity.this, type);
        taskRecycler = (RecyclerView) findViewById(R.id.task_list);
        taskRecycler.setLayoutManager(new LinearLayoutManager(this));
        taskRecycler.setItemAnimator(new DefaultItemAnimator());
        taskRecycler.setAdapter(mAdapter);

        Context context = taskRecycler.getContext();
        LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.list_animation_fall_down);
        taskRecycler.setLayoutAnimation(controller);
        taskRecycler.scheduleLayoutAnimation();

        emptyView = findViewById(R.id.empty_view);
        if (newTaskList.isEmpty()) {
            taskRecycler.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            taskRecycler.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    void incrementProg() {
        if (totalProg < totalGoal) {
            totalProg++;
            updateGoalProgress(totalProg, totalGoal);
        }
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

            String message = intent.getStringExtra("message");
            System.out.println(message + " fuck");
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            if (message.split(",")[0].equals("$")) {
                String [] data = message.split(",");
                System.out.println(data[0] + "," + data[1]);
                if (dbh.incrementTask(Integer.parseInt(data[1]))) {
                    updateRecycler(dbh.loadToday(), "TODAY");
                    String updateMsg = dbh.getWatchTasks();

                    // Send update to watch
                    new NewThread("/my_path", updateMsg).start();

                    System.out.println("UPDATE SENT");
                    try {
                        Thread.sleep(1500);
                    } catch(InterruptedException ex) {
                        Toast.makeText(MainActivity.this, "Toast", Toast.LENGTH_SHORT).show();
                        ex.printStackTrace();
                    }
                }
                //mAdapter.notifyItemChanged(Integer.parseInt(data[3]));
                mAdapter.notifyDataSetChanged();
            }
            if (message.equals("REQUEST_UPDATE")) {
                System.out.println("REQUEST_UPDATE RECEIVED");
                //dbh.getWatchTasks();
                sendUpdateToWatch();
            }
        }
    }

    public void loadProgress() {
        int[] goalProg = dbh.getDailyProgress();
        totalGoal = goalProg[0];
        totalProg = goalProg[1];
        updateGoalProgress(totalProg, totalGoal);
    }

    public void updateGoalProgress(float sum, int total) {
        float prog = (sum/total) * 100;
        if (Float.isNaN(prog)) prog = (float) 100.0;

        DecimalFormat df = new DecimalFormat("#.#");
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

    public void sendUpdateToWatch() {
        String updateMsg = dbh.getWatchTasks();

        // Send update to watch
        new NewThread("/my_path", updateMsg).start();

        System.out.println("UPDATE SENT");
    }
}
