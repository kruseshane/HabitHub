package com.example.shane_kruse.habbithub;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MyTaskAdapter extends RecyclerView.Adapter<MyTaskAdapter.ViewHolder> {
    private int listItemLayout;
    private Task[] taskList;

    public MyTaskAdapter (int layoutID, Task[] data) {
        listItemLayout = layoutID;
        taskList = data;
    }

    public int getItemCount() {
        return taskList.length;
    }

    public ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder myViewHolder, int i) {
        TextView item = myViewHolder.item;
        item.setText(taskList[i].getDescr());
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView item;
        public ViewHolder (View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            item = (TextView) itemView.findViewById(R.id.task_main);
        }

        public void onClick(View view) {
            System.out.println(item.getText());
        }
    }
}
