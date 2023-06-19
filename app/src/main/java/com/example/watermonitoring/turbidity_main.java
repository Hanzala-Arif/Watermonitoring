package com.example.watermonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Timer;
import java.util.TimerTask;

public class turbidity_main extends AppCompatActivity {
    Intent intent;
    int count=0 ;
    Timer timer;
    Handler handle= new Handler();
    public static int SPLASH_TIME_OUT=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turbidity_main);

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

                        if (count == 95) {
                            startActivity(new Intent(turbidity_main.this, Turbidity.class));
                            finish();
                        }

                    }
                };
                timer.schedule(tt, 0, 95);


            }

        },SPLASH_TIME_OUT);

    }
}