package com.example.shane_kruse.habbithub;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class EditActivity extends AppCompatActivity {
    private EditText descEdit;
    private EditText goalEdit;
    private Toolbar mToolbar;
    private GridView iconSelection;
    private GridView colorSelection;
    private TextView backText;
    private TextView nextText;
    private IconAdapterGridView mAdapter;
    private String icon;
    private String hex;
    private DbHandler hand;


    Integer[] iconIDs = {
            R.mipmap.ic_launcher_foreground_dog_task_icon,
            R.mipmap.ic_launcher_foreground_water_task_icon,
            R.mipmap.ic_launcher_foreground_heart_task_icon,
            R.mipmap.ic_launcher_foreground_run_task_icon,
            R.mipmap.ic_launcher_foreground_sit_ups_task_icon,
            R.mipmap.ic_launcher_foreground_sleep_task_icon,
            R.mipmap.ic_launcher_foreground_broom_task_icon,
            R.mipmap.ic_launcher_foreground_dishwasher_task_icon,
            R.mipmap.ic_launcher_foreground_beer_task_icon,
            R.mipmap.ic_launcher_foreground_coffee_task_icon,
            R.mipmap.ic_launcher_foreground_wine_task_icon
    };


    Integer[] colorIDs = {
            R.mipmap.foreground_color_color_red_icon,
            R.mipmap.foreground_color_light_blue_icon,
            R.mipmap.foreground_color_light_green_icon,
            R.mipmap.foreground_color_light_purple_icon
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        hand = new DbHandler(EditActivity.this);

        // Initialize Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        backText = findViewById(R.id.back);
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // This is the save text for now
        nextText = findViewById(R.id.next);
        nextText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                System.out.println(hex);
                Task t = new Task(String.valueOf(descEdit.getText()), 1, 0, date, icon, false, "Today", hex);
                hand.insertTask(t);
                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        descEdit = findViewById(R.id.edit_desc_txtBox);
        descEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    descEdit.setHint("");
                else
                    descEdit.setHint(getString(R.string.edit_task_desc_hint));
            }
        });

        Intent intent = getIntent();
        int position = intent.getIntExtra("task_pos", 0);
        //int goal = intent.getIntExtra("task_goal", 0);
        String desc = intent.getStringExtra("task_desc");

        descEdit.setText(desc);
        //goalEdit.setText(String.valueOf(goal));

        colorSelection = findViewById(R.id.color_selector_view);
        colorSelection.setAdapter(new ColorAdapterGridView(this));

        colorSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get hex based off item selected
                System.out.println(position);
                switch(position) {
                    case 0:
                        hex = "#D33D3D";
                    case 1:
                        hex = "#289BAF";
                        break;
                    case 2:
                        hex = "#90F24B";
                        break;
                    case 3:
                        hex = "#E38BFC";
                        break;
                }
            }
        });

        iconSelection = findViewById(R.id.icon_selector_view);
        mAdapter = new IconAdapterGridView(this);
        iconSelection.setAdapter(mAdapter);

        iconSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get id of icon selected
                icon = String.valueOf(iconIDs[position]);
            }
        });
    }


    private class IconAdapterGridView extends BaseAdapter {
        private Context context;

        public IconAdapterGridView(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return iconIDs.length;
        }

        @Override
        public Object getItem(int position) {
            return iconIDs[position];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv;

            if (convertView == null) {
                iv = new ImageView(context);
                iv.setLayoutParams(new GridView.LayoutParams(200, 200));
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setPadding(16, 16, 16, 16);


            } else {
                iv = (ImageView) convertView;
            }

            iv.setImageResource(iconIDs[position]);
            iv.setId(iconIDs[position]);


            return iv;
        }
    }


    private class ColorAdapterGridView extends BaseAdapter {
        private Context context;

        public ColorAdapterGridView(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return colorIDs.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv;

            if (convertView == null) {
                iv = new ImageView(context);
                iv.setLayoutParams(new GridView.LayoutParams(165, 165));
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setPadding(16, 16, 16, 16);
            } else {
                iv = (ImageView) convertView;
            }

            iv.setImageResource(colorIDs[position]);
            return iv;
        }
    }
}
