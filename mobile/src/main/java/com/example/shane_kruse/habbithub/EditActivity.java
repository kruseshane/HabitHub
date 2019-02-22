package com.example.shane_kruse.habbithub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {
    private EditText descEdit;
    private EditText goalEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        descEdit = findViewById(R.id.edit_desc_txtBox);
        goalEdit = findViewById(R.id.edit_goal_txtBox);

        Intent intent = getIntent();
        int position = intent.getIntExtra("task_pos", 0);
        int goal = intent.getIntExtra("task_goal", 0);
        String desc = intent.getStringExtra("task_desc");

        descEdit.setText(desc);
        goalEdit.setText(String.valueOf(goal));
    }
}
