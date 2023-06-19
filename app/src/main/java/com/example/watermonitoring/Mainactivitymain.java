package com.example.watermonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class Mainactivitymain extends AppCompatActivity {
    Intent intent;
    int count=0 ;
    Timer timer;
    Handler handle= new Handler();
    public static int SPLASH_TIME_OUT=1000;
    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activitymain);
        ProgressBar progressBar= (ProgressBar) findViewById(R.id.progressBar2);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // setContentView(R.layout.activity_waterapp);
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                timer = new Timer();
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {


                        count++;
                        progressBar.setProgress(count);

                        if (count == 75) {
                            startActivity(new Intent(Mainactivitymain.this, MainActivity.class));
                            finish();
                        }

                    }
                };
                timer.schedule(tt, 0, 75);


            }

        },SPLASH_TIME_OUT);

    }
}
