package com.example.watermonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Turbidity extends AppCompatActivity implements SensorEventListener {
    String token,espnum,hhh;
    private Runnable destroyDataRunnable;
    private Handler handler;
    public static String PREFS_NAME="MyPrefs";
    DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://notification-56c0a-default-rtdb.firebaseio.com/");
    BarChart barChart;
    HalfGauge meter;
    ArcGauge meter2;
    com.ekn.gruzer.gaugelibrary.Range r1, r2, r3;
    int esp;
    int distance,value,tur,ph;
    private LineChart mChart;
    private Thread thread;
    private boolean plotData = true;
    public static final String EXTRA_TEXT =  " com.example.Login.extra.MESSAGE";
    private static final String TAG = "MainActivity";
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor sensors;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turbidity);

        float distances = 100f;
        float dis = 0f;

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        token= sharedPreferences.getString("tokenss", "null");
        espnum = sharedPreferences.getString("esp", "null");

        meter = findViewById(R.id.meter);
        meter2 = findViewById(R.id.meter3);
        navigationView= (NavigationView) findViewById(R.id.navigation_view) ;
        navigationView.setItemIconTintList(null);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
//        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        ArrayList<BarEntry> information = new ArrayList<>();

        r1 = new Range();
        r2 = new Range();
        r3 = new Range();
        r1.setTo(0);
        r1.setFrom(20);
        r2.setTo(20);
        r2.setFrom(80);
        r3.setTo(80);
        r3.setFrom(100);
        r1.setColor(Color.RED);
        r2.setColor(Color.parseColor("#82D6E1"));
        r3.setColor(Color.parseColor("#D2DD64"));
        meter.setMinValue(0);
        meter.setMaxValue(100);
        meter.setValue(0);
        meter.addRange(r1);
        meter.addRange(r2);
        meter.addRange(r3);
        meter2.addRange(r1);
        meter2.addRange(r2);
        meter2.addRange(r3);
        meter2.setMinValue(0);
        meter2.setMaxValue(100);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                distance = snapshot.child(espnum).child("Water_POINT").getValue(Integer.class);
                tur= snapshot.child(espnum).child("Water_Turbidity").getValue(Integer.class);
                ph= snapshot.child(espnum).child("PH_Condition").getValue(Integer.class);
                esp=snapshot.child(espnum).child("ESP").getValue(Integer.class);

                meter.setValue((tur));
                meter2.setValue(tur);
                value = Integer.valueOf(tur);
                String tr=String.valueOf(distance);
                String Ph=String.valueOf(ph);
                String dt=String.valueOf(snapshot.child(espnum).child("Water_Turbidity").getValue(Integer.class));
                //Metain a record for 24 hour
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String val=(String.valueOf(esp));
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Log.d("Value", snapshot1.getKey().toString());
                            if (snapshot1.getKey().equals(distance)  ) {
                                /// Toast.makeText(MainActivity.this, "This order is already done", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (snapshot1.getKey().equals(tur)  ) {
                                // Toast.makeText(MainActivity.this, "This order is already done", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (snapshot1.getKey().equals(ph)  ) {
                                //Toast.makeText(MainActivity.this, "This order is already done", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            ref.child("Week").child(val).child("Distance").child(tr).setValue(distance);
                            ref.child("Week").child(val).child("Turbidity").child(dt).setValue(tur);
                            ref.child("Week").child(val).child("PH").child(Ph).setValue(ph);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





            }




            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for (int i = 0; i < sensors.size(); i++) {
            Log.d(TAG, "onCreate: Sensor " + i + ": " + sensors.get(i).toString());
        }

        if (mAccelerometer != null) {
            mSensorManager.registerListener(Turbidity.this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        mChart = (LineChart) findViewById(R.id.chart1);

        // enable description text
        mChart.getDescription().setEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.parseColor("#82D6E1"));

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(false);

        leftAxis.setAxisMaximum(distances);
        leftAxis.setAxisMinimum(dis);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);

        feedMultiple();

    }

    private void addEntry(SensorEvent event) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

//            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
            data.addEntry(new Entry(set.getEntryCount(), tur + 0), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(100);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(2f);
        set.setColor(Color.WHITE);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.001f);
        return set;
    }

    private void feedMultiple() {

        if (thread != null) {
            thread.interrupt();
        }

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    plotData = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }
    @Override
    public final void onAccuracyChanged(Sensor sensors, int accuracy)
    {

    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if(plotData){
            addEntry(event);
            plotData = false;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        mSensorManager.unregisterListener(Turbidity.this);
        thread.interrupt();
        super.onDestroy();
    }





    protected void onPause(){
        super.onPause();
        handler = new Handler();

        // Create a runnable to destroy the data
        destroyDataRunnable = new Runnable() {
            @Override
            public void run() {

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(distance < 20){
                            sendNotification("Level is low",token,"Distance of water");
                        }
                        if(distance > 80 && distance <=90){
                            sendNotification("Alert!Tank going to be full ",token,"Distance of water");
                        }
                        if(distance > 90 && distance <=100){
                            sendNotification("Tank is full ",token,"Distance of water");
                        }

                        if(tur <20){
                            sendNotification("Water is Impure  ",token,"Turbidity of water");
                        }
                        if(ph<5){
                            sendNotification("Acidic ",token,"PH of water");
                        }
                        if(ph>8){
                            sendNotification("Basic ",token,"PH of water");
                        }
//
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                handler.postDelayed(this, 60000);
            }
        } ;

        startDataRetrieval();
    }


    private void startDataRetrieval() {
        // Post the retrieveDataRunnable for the first time
        handler.post( destroyDataRunnable);
    }
    private void sendNotification(String message, String token, String title) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(Turbidity.this);
            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();
            //JSONObject userDetail = new JSONObject();

            data.put("title", title);
            data.put("body", message);
            // data.put("image", imageAddress);
            // data.put("click_action", "OPEN_CHAT_ACTIVITY");

//            userDetail.put("receiverID", senderID);
//            userDetail.put("receiverName", senderName);
//            userDetail.put("receiverImage", receiverImage);

            JSONObject notificationData = new JSONObject();

            notificationData.put("notification", data);
            notificationData.put("to", token);
            // notificationData.put("data", userDetail);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, notificationData, response -> {
            }, Throwable::printStackTrace) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    String key = "Key=AAAAg7VDM1E:APA91bFTc-kBwGYwVNi4vzsMaRA3wwBSx7clb-rMv_JHmJl4_el4YNm6w9KqlHYLupAlAeWJlI4rxVrUa6zDhtLQeXnVNN_k_rnrg3y9eVXNXiqticgWmkGybLBlcyDI3a0pfd5QKOW-";
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", key);
                    return headers;
                }
            };
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



    public void send(View view) {

    }
    public void distance(MenuItem item) {
        startActivity(new Intent(getApplicationContext(),Mainactivitymain.class));
    }

    public void tur(MenuItem item) {
        startActivity(new Intent(getApplicationContext(),turbidity_main.class));
    }

    public void ph(MenuItem item) {
        startActivity(new Intent(getApplicationContext(),ph_main.class));
    }

    public void own(MenuItem item) {


    }
    public void move(MenuItem item) {
        startActivity(new Intent(getApplicationContext(),Turbidity2.class));

    }

    public void Logout(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void logout(MenuItem item) {
        if(distance < 20){
            sendNotification("Level is low",token,"Distance of water");
        }
        if(distance > 80 && distance <=90){
            sendNotification("Alert!Tank going to be full ",token,"Distance of water");
        }
        if(distance > 90 && distance <=100){
            sendNotification("Tank is full ",token,"Distance of water");
        }

        if(tur <20){
            sendNotification("Water is Impure  ",token,"Turbidity of water");
        }
        if(ph<5){
            sendNotification("Acidic ",token,"PH of water");
        }
        if(ph>8){
            sendNotification("Basic ",token,"PH of water");
        }
    }

    public void popup(View view) {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.spitems, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // Handle menu item click events here
                switch (menuItem.getItemId()) {
                    case R.id.menu_item1:
                        //  startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        return true;
                    case R.id.menu_item2:
                        // Action for menu item 2
                        // startActivity(new Intent(getApplicationContext(),turbidity_main.class));
                        return true;



                    default:
                        return false;
                }
            }

        });

        popupMenu.show();
    }

    public void open(View view) {
        drawerLayout.openDrawer(navigationView);
        Button b3=(Button) findViewById(R.id.b3);

    }


}