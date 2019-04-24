package com.example.shane_kruse.habbithub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class InformationActivity extends AppCompatActivity {

    TextView infoText;
    TextView q1, q2, q3;
    TextView a1, a2, a3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        infoText = findViewById(R.id.info_text);
        infoText.setText("Thank you for downloading HabitHub. Our hope is that by using this app" +
                ", you will be able to better your lifestyle in anyway you choose. Please refer to " +
                "the tips below to understand how to utilize some of HabitHub's features");

        q1 = findViewById(R.id.question1);
        q1.setText("Q: How do I add a task?");

        a1 = findViewById(R.id.answer1);
        a1.setText("A: Use the '+' icon in the top left hand corner");

        q2 = findViewById(R.id.question2);
        q2.setText("Q: How do I delete a task?");

        a2 = findViewById(R.id.answer2);
        a2.setText("Long press the task which you would like to edit and select the DELETE option " +
                "from the presented options");

        q3 = findViewById(R.id.question3);
        q3.setText("Q: How do I edit a task after creating one?");

        a3 = findViewById(R.id.answer3);
        a3.setText("A: Long press the task which you would like to delete and select the EDIT option " +
                "from the presented options");

    }
}
