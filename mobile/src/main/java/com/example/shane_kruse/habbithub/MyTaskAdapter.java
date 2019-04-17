package com.example.shane_kruse.habbithub;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import com.github.lzyzsd.circleprogress.DonutProgress;
import org.w3c.dom.Text;


public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.ViewHolder> {
    private int listItemLayout;
    private static ArrayList<Task> taskList;
    private static DbHandler dbh;
    private static Context mContext;
    private static MainActivity mainAct;
    private static boolean clickable;
    private int lastPosition = -1;

    public MyTaskAdapter (int layoutID, ArrayList<Task> data, Context context, boolean clickable) {
        listItemLayout = layoutID;
        taskList = data;
        mContext = context;
        mainAct = (MainActivity) mContext;
        dbh = new DbHandler(mContext, mainAct);
        this.clickable = clickable;
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

        //String goal_str = String.valueOf(taskList.get(i).getGoal());
        //String current_goal_str = String.valueOf(taskList.get(i).getProg());
        //myViewHolder.task_goal.setText(current_goal_str + "/" + goal_str);
        myViewHolder.task_goal.setText(taskList.get(i).getProg() + "/" + taskList.get(i).getGoal());
        myViewHolder.task_icon.setBackgroundResource(R.drawable.task_icon_background_shape_circle);
        myViewHolder.task_icon.getBackground().setColorFilter(Color.parseColor(taskList.get(i).getColor()), PorterDuff.Mode.SRC);
        myViewHolder.task_icon.setImageResource(Integer.parseInt(taskList.get(i).getIcon()));
        myViewHolder.setIndex(i);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView task_desc;
        public TextView task_goal;
        public TextView hold_goal;
        public ImageView task_icon;
        private int index;

        ViewHolder (View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            task_desc = (TextView) itemView.findViewById(R.id.task_desc);
            task_goal = (TextView) itemView.findViewById(R.id.task_goal);
            task_icon = (ImageView) itemView.findViewById(R.id.icon);
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
            if (clickable) {
                Task t = taskList.get(index);
                boolean completed = dbh.incrementTask(t.getRow_id());

                //if (completed) mainAct.removeCompleted();
                if (completed) {
                    deleteItem(index);
                    //mainAct.updateRecycler(dbh.loadToday(), true);
                }

                else {
                    hold_goal = view.findViewById(R.id.task_goal);
                    hold_goal.setText(t.getProg() + "/" + t.getGoal());
                }
            }
        }
    }
}
