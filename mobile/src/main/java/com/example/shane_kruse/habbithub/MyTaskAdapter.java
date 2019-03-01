package com.example.shane_kruse.habbithub;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.ViewHolder> {
    private int listItemLayout;
    private ArrayList<Task> taskList;

    public MyTaskAdapter (int layoutID, ArrayList<Task> data) {
        listItemLayout = layoutID;
        taskList = data;
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
    public void onBindViewHolder(@NonNull ViewHolder myViewHolder, int i) {
        myViewHolder.task_desc.setText(taskList.get(i).getDescr());

        String goal_str = String.valueOf(taskList.get(i).getGoal());
        String current_goal_str = String.valueOf(taskList.get(i).getProg());
        myViewHolder.task_goal.setText(current_goal_str + "/" + goal_str);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView task_desc;
        public TextView task_goal;

        public ViewHolder (View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            task_desc = (TextView) itemView.findViewById(R.id.task_desc);
            task_goal = (TextView) itemView.findViewById(R.id.task_goal);
        }

        public void onClick(View view) {
            System.out.println(task_desc.getText());
        }
    }
}
