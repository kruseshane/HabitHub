package com.example.shane_kruse.habbithub;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class MainActivity extends WearableActivity {

    private PieChart pieChart;
    private float[] yData = {16.67f, 16.67f, 16.67f, 16.67f, 16.66f, 16.66f};
    private String[] xData = {"run", "water", "coffee", "read", "code", "hw"};
    private PieData pieData;
    private ConstraintLayout background;
    private Handler myHandler; // was protected

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enables Always-on
        setAmbientEnabled();

        //Create a message handler//
        myHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle stuff = msg.getData();
                return true;
            }
        });

        background = findViewById(R.id.pie_background);
        pieChart = findViewById(R.id.pie_chart);

        background.setBackgroundColor(Color.parseColor("#F5F5F5"));

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < yData.length; i++) {
            entries.add(new PieEntry(yData[i], xData[i]));
        }

        //create the data set
        final PieDataSet pieDataSet = new PieDataSet(entries, null);

        background.setBackgroundColor(Color.parseColor("#F5F5F5"));

        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(255, 140, 0));
        colors.add(Color.BLUE);
        colors.add(Color.rgb(255,105,180));
        colors.add(Color.rgb(192, 14, 14));
        colors.add(Color.rgb(35, 145, 49));
        colors.add(Color.rgb(128, 0, 128));
        colors.add(Color.MAGENTA);

        pieDataSet.setColors(colors);

        pieChart.setDrawEntryLabels(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setEntryLabelTextSize(18f);
        pieChart.setDrawSlicesUnderHole(false);
        pieChart.setHoleRadius(3f);
        pieChart.setTransparentCircleRadius(4f);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry pe = (PieEntry) e;
                int backColor = pieDataSet.getColor(pieDataSet.getEntryIndex(pe));
                Intent intent = new Intent(MainActivity.this, GoalStatusActivity.class);
                intent.putExtra("task", pe.getLabel());
                intent.putExtra("slice_color", backColor);
                startActivity(intent);

                //Sending a message can block the main UI thread, so use a new thread//
                new NewThread("/my_path", pe.getLabel()).start();

                finish();
            }

            @Override
            public void onNothingSelected() { }
        });

        //create pie data object
        pieData = new PieData(pieDataSet);
        pieDataSet.setDrawValues(false);
        pieChart.setData(pieData);
        pieChart.invalidate();
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

            //Retrieve the connected devices, known as nodes//
            Task<List<Node>> mobileDeviceList =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {

                List<Node> nodeList = Tasks.await(mobileDeviceList);
                for (Node n : nodeList) {
                    Task<Integer> sendMessageTask =

                            //Send the message//
                            Wearable.getMessageClient(MainActivity.this).sendMessage(n.getId(), path, message.getBytes());

                    try {

                        //Block on a task and get the result synchronously//
                        Integer result = Tasks.await(sendMessageTask);
                        sendmessage(message);

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
            Message msg = myHandler.obtainMessage();
            msg.setData(bundle);
            myHandler.sendMessage(msg);

        }
    }
}
