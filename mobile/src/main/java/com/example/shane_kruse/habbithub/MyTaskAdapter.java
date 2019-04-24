package com.example.shane_kruse.habbithub;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.ViewHolder> {
    private int listItemLayout;
    private static ArrayList<Task> taskList;
    private static DbHandler dbh;
    private static Context mContext;
    private static MainActivity mainAct;
    private static String type;
    private int lastPosition = -1;


    public MyTaskAdapter (int layoutID, ArrayList<Task> data, Context context, String type) {
        listItemLayout = layoutID;
        taskList = data;
        mContext = context;
        mainAct = (MainActivity) mContext;
        dbh = new DbHandler(mContext, mainAct);
        this.type = type;
    }

    public int getItemCount() {
        return taskList.size();
    }

    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder (@NonNull ViewHolder myViewHolder, int i) {
        myViewHolder.task_desc.setText(taskList.get(i).getDescr());
        myViewHolder.task_goal.setText(taskList.get(i).getProg() + "/" + taskList.get(i).getGoal());

        myViewHolder.task_icon.setBackgroundResource(R.drawable.task_icon_background_shape_circle);
        myViewHolder.task_icon.getBackground().setColorFilter(Color.parseColor(taskList.get(i).getColor()), PorterDuff.Mode.SRC);
        myViewHolder.task_icon.setImageResource(Integer.parseInt(taskList.get(i).getIcon()));


        String time_str = "";
        Task t = taskList.get(i);

        if (type == "TODAY") {
            LocalTime due_time = t.getDue_date();
            // Check if time is set to Anytime
            if (due_time.toString().equals("23:59"))
                time_str = "Today";
                // Convert from military time
            else {
                String time_period = "AM";
                String hour;
                String minute = String.valueOf(due_time.getMinute());

                if (due_time.getHour() > 12) {
                    hour = String.valueOf(due_time.getHour() - 12);
                    time_period = "PM";
                }
                else
                    hour = String.valueOf(due_time.getHour());

                time_str = hour + ":" + minute + " " + time_period;
            }
        }

        else if (type == "UPCOMING") {
            ArrayList<String> interval = t.getInterval();
            for (String s: interval) {
                time_str += s + ",";
            }
            time_str = time_str.substring(0, time_str.length() - 1);
        }

        else if (type == "COMPLETED") {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd hh:mm a");
            time_str = t.getCompletedTime().format(dtf);
        }

        myViewHolder.task_time.setText(time_str);
        myViewHolder.setIndex(i);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        public TextView task_desc;
        public TextView task_goal;
        public TextView hold_goal;
        public ImageView task_icon;
        public TextView task_time;
        private int index;

        ViewHolder (View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            task_desc = (TextView) itemView.findViewById(R.id.task_desc);
            task_goal = (TextView) itemView.findViewById(R.id.task_goal);
            task_icon = (ImageView) itemView.findViewById(R.id.icon);
            task_time = (TextView) itemView.findViewById(R.id.task_time);
            this.index = -1;
        }


        void deleteItem(int index) {
            taskList.remove(index);
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, taskList.size());
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.task_slide_right_out);
            itemView.startAnimation(animation);
        }

        void setIndex(int index) {
            this.index = index;
        }

        public void onClick(View view) {
            if (type == "TODAY") {
                Task t = taskList.get(index);
                boolean completed = dbh.incrementTask(t.getRow_id());

                if (completed) {
                    deleteItem(index);
                    mainAct.updateRecycler(dbh.loadToday(), "TODAY");
                }

                else {
                    hold_goal = view.findViewById(R.id.task_goal);
                    hold_goal.setText(t.getProg() + "/" + t.getGoal());
                }
            }
        }

        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if (type != "COMPLETED") {
                menu.add(Menu.NONE, 1, 1, "Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int recyclerID = getLayoutPosition();
                        Task t = taskList.get(recyclerID);

                        Intent editTask = new Intent(mContext, EditActivity.class);
                        editTask.putExtra("id", t.getRow_id());
                        editTask.putExtra("descr", t.getDescr());
                        editTask.putExtra("goal", t.getGoal());
                        editTask.putExtra("due_date", t.getDue_date().toString());
                        editTask.putExtra("icon", t.getIcon());

                        String interval_str = "";
                        for (String s : t.getInterval())
                            interval_str += s + ",";
                        interval_str = interval_str.substring(0, interval_str.length() - 1);

                        editTask.putExtra("interval", interval_str);
                        editTask.putExtra("repeat", t.getRepeat());
                        editTask.putExtra("color", t.getColor());
                        editTask.putExtra("on_watch", t.isOnWatch());
                        editTask.putExtra("abbrev", t.getAbbrev());

                        mContext.startActivity(editTask);
                        return false;
                    }
                });

                menu.add(Menu.NONE, 1, 2, "Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int recyclerID = getLayoutPosition();
                        int rowID = taskList.get(recyclerID).getRow_id();

                        dbh.removeTask(rowID);
                        deleteItem(index);
                        mainAct.updateRecycler(dbh.loadToday(), "TODAY");
                        mainAct.loadProgress();
                        return false;
                    }
                });
            }
        }
    }
}
