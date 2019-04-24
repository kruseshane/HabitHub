package com.example.shane_kruse.habbithub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

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
    private boolean nameSelected, colorSelected, iconSelected;
    Intent nextIntent;


    // 51% scale size
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
            R.mipmap.ic_launcher_foreground_wine_task_icon,
            R.mipmap.ic_launcher_foreground_attack_icon,
            R.mipmap.ic_launcher_foreground_bathtub_icon,
            R.mipmap.ic_launcher_foreground_beach_icon,
            R.mipmap.ic_launcher_foreground_cake_icon,
            R.mipmap.ic_launcher_foreground_bot_icon,
            R.mipmap.ic_launcher_foreground_bug_icon,
            R.mipmap.ic_launcher_foreground_bus_icon,
            R.mipmap.ic_launcher_foreground_carrot_icon,
            R.mipmap.ic_launcher_foreground_eat_healthy_task_icon,
            R.mipmap.ic_launcher_foreground_card_icon,
            R.mipmap.ic_launcher_foreground_tooth_icon,
            R.mipmap.ic_launcher_foreground_floss_icon,
            R.mipmap.ic_launcher_foreground_dumbbell_icon,
            R.mipmap.ic_launcher_foreground_facebook_icon,
            R.mipmap.ic_launcher_foreground_github_icon,
            R.mipmap.ic_launcher_foreground_grapes_icon,
            R.mipmap.ic_launcher_foreground_timer_task_icon,
            R.mipmap.ic_launcher_foreground_hamburger_task_icon,
            R.mipmap.ic_launcher_foreground_icecream_task_icon,
            R.mipmap.ic_launcher_foreground_insta_task_icon,
            R.mipmap.ic_launcher_foreground_youtube_task_icon,
            R.mipmap.ic_launcher_foreground_rent_task_icon,
            R.mipmap.ic_launcher_foreground_cart_task_icon,
            R.mipmap.ic_launcher_foreground_car_task_icon,
            R.mipmap.ic_launcher_foreground_code_task_icon,
            R.mipmap.ic_launcher_foreground_check_computer_task_icon,
            R.mipmap.ic_launcher_foreground_report_computer_task_icon,
            R.mipmap.ic_launcher_foreground_bike_task_icon,
            R.mipmap.ic_launcher_foreground_twitter_task_icon,
            R.mipmap.ic_launcher_foreground_wash_hands_task_icon
    };


    Integer[] colorIDs = {
            R.mipmap.foreground_color_red_icon,
            R.mipmap.foreground_color_pink_icon,
            R.mipmap.foreground_color_light_purple_icon,
            R.mipmap.foreground_color_dark_blue_icon,
            R.mipmap.foreground_color_light_blue_icon,
            R.mipmap.foreground_color_orange_icon,
            R.mipmap.foreground_color_yellow_icon,
            R.mipmap.foreground_color_green_icon,
            R.mipmap.foreground_color_light_green_icon,
            R.mipmap.foreground_color_teal_icon
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        hand = new DbHandler(EditActivity.this);
        nextIntent = new Intent(EditActivity.this, ScheduleActivity.class);

        // Initialize Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        // Next Button
        nextText = findViewById(R.id.next);
        nextText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextIntent.putExtra("desc", descEdit.getText().toString());
                nextIntent.putExtra("icon", icon);
                nextIntent.putExtra("hex", hex);
                startActivity(nextIntent);
            }
        });

        // Back button
        backText = findViewById(R.id.back);
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        descEdit = findViewById(R.id.edit_desc_txtBox);
        descEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    descEdit.setHint("");
                }
                else {
                    descEdit.setHint(getString(R.string.edit_task_desc_hint));
                    if (!descEdit.getText().toString().equals("")) {
                        nameSelected = true;
                    }
                    if (nameSelected && colorSelected && iconSelected) {
                        nextText.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        colorSelection = findViewById(R.id.color_selector_view);
        colorSelection.setAdapter(new ColorAdapterGridView(this));

        colorSelection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get hex based off item selected
                System.out.println(position);
                switch(position) {
                    case 0:
                        hex = "#D34545"; // red
                    case 1:
                        hex = "#E562AA"; // pink
                        break;
                    case 2:
                        hex = "#CA7FDB"; // light purple
                        break;
                    case 3:
                        hex = "#167199"; // dark blue
                        break;
                }
                colorSelected = true;
                if (nameSelected && colorSelected && iconSelected) {
                    nextText.setVisibility(View.VISIBLE);
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

                iconSelected = true;
                if (nameSelected && colorSelected && iconSelected) {
                    nextText.setVisibility(View.VISIBLE);
                }
            }
        });

        // Load existing task info if a task is being edited
        Intent editIntent = getIntent();
        String editDescr = editIntent.getStringExtra("descr");

        if (editDescr != null) {
            descEdit.setText(editDescr);
            nameSelected = true;

            nextIntent.putExtra("id", editIntent.getIntExtra("id", -1));
            nextIntent.putExtra("goal", editIntent.getIntExtra("goal", -1));
            nextIntent.putExtra("due_date", editIntent.getStringExtra("due_date"));
            nextIntent.putExtra("interval", editIntent.getStringExtra("interval"));
            nextIntent.putExtra("repeat", editIntent.getBooleanExtra("repeat", false));
            nextIntent.putExtra("on_watch", editIntent.getBooleanExtra("on_watch", false));
            nextIntent.putExtra("abbrev", editIntent.getStringExtra("abbrev"));
        }
    }


    class IconAdapterGridView extends BaseAdapter {
        private Context context;

        IconAdapterGridView(Context context) {
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

        ColorAdapterGridView(Context context) {
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
