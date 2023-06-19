package com.example.watermonitoring;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class waterapp extends AppCompatActivity {
    Intent intent;
    int count=0 ;
    Timer timer;
    Handler handle= new Handler();
    public static int SPLASH_TIME_OUT=1000;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://notification-56c0a-default-rtdb.firebaseio.com/");
    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waterapp);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_waterapp);
       ProgressBar progressBar = findViewById(R.id.spin_kit);


        progressBar.setVisibility(View.VISIBLE);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(Login.PREFS_NAME, 0);
                boolean hasLoggedIn = sharedPreferences.getBoolean("hasLoggedIn", false);
                if (hasLoggedIn) {

                    timer =new Timer();
                    TimerTask tt = new TimerTask() {
                        @Override
                        public void run() {


                            count++;
                            progressBar.setProgress(count);

                            if (count == 50) {
                                startActivity(new Intent(waterapp.this,Home.class));
                                finish();
                            }

                        }
                    };
                    timer.schedule(tt, 0, 50);


                }
                else{
                    timer =new Timer();
                    TimerTask tt = new TimerTask() {
                        @Override
                        public void run() {


                            count++;
                            progressBar.setProgress(count);

                            if (count == 50) {
                                startActivity(new Intent(waterapp.this,Login.class));
                                finish();
                            }

                        }
                    };
                    timer.schedule(tt, 0, 50);

                }
            }

        },SPLASH_TIME_OUT);

    }
}