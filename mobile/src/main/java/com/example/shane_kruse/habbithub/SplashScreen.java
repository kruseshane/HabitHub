package com.example.shane_kruse.habbithub;

import android.content.Intent;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = findViewById(R.id.logo_view);

        Timer timer = new Timer(3000, 3000);
        timer.start();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_logo_anim);
        logo.startAnimation(animation);
    }

    private class Timer extends CountDownTimer {

        Timer(long millisLength, long millisUpdate) {
            super(millisLength, millisUpdate);
        }

        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
