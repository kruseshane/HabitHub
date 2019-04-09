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
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import com.github.lzyzsd.circleprogress.DonutProgress;
import org.w3c.dom.Text;


public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.ViewHolder> {
    private int listItemLayout;
    private static ArrayList<Task> taskList;
    private static DbHandler dbh;
    private Context mContext;

    public MyTaskAdapter (int layoutID, ArrayList<Task> data, Context context) {
        listItemLayout = layoutID;
        taskList = data;
        mContext = context;
        dbh = new DbHandler(mContext);
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

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

        void setIndex(int index) {
            this.index = index;
        }

        public void onClick(View view) {
            System.out.println(task_desc.getText());
            Task t = taskList.get(index);
            dbh.incrementTask(t);
            hold_goal = view.findViewById(R.id.task_goal);
            hold_goal.setText(t.getProg() + "/" + t.getGoal());

            //hold_goal.setText(dbh.getCurrentProg(t.getDescr()) + "/" + t.getGoal());

            int total = 0;
            float sum = 0;
            for (int i = 0; i < taskList.size(); i++) {
                total += taskList.get(i).getGoal();
                sum += taskList.get(i).getProg();
            }
            ((MainActivity) view.getContext()).updateGoalProgress(sum, total);
        }
    }
}
