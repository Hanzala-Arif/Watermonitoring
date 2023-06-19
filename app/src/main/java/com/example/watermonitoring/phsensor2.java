package com.example.watermonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class phsensor2 extends AppCompatActivity {
    BarChart barChart;
    PieChart pieChart;
    private Runnable destroyDataRunnable;
    String token,esp;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    private Handler handler;
    int distance,tur,ph;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://notification-56c0a-default-rtdb.firebaseio.com/");
    ArrayList<BarEntry> information = new ArrayList<>();
    ArrayList<PieEntry> pieinformation = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phsensor2);
        barChart = findViewById(R.id.piechart);
        pieChart=findViewById(R.id.chart2);
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(ContextCompat.getColor(this, R.color.purple_200));
        colors.add(ContextCompat.getColor(this, R.color.teal_200));
        colors.add(ContextCompat.getColor(this, R.color.black));
        colors.add(ContextCompat.getColor(this, R.color.teal_700));
        pieChart.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("tokens", "null");
        esp = sharedPreferences.getString("esp", "null");
        Toast.makeText(phsensor2.this,esp, Toast.LENGTH_SHORT).show();
        ref.child("Week").child(esp).child("PH").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    int dis=snapshot1.getValue(Integer.class);
                    information.add(new BarEntry(dis, dis));
                    BarDataSet dataSet = new BarDataSet(information, "BarValue");
                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    dataSet.setValueTextColor(Color.BLACK);
                    dataSet.setValueTextSize(3f);
                    BarData barData = new BarData(dataSet);
                    barChart.setFitBars(true);
                    barChart.setData(barData);
                    barChart.getDescription().setText("Bar report");
                    barChart.animateY(2000);

                    pieinformation.add(new PieEntry(dis,dis));
                    PieDataSet pieDataSet=new PieDataSet(pieinformation,"PieValue");
                    pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    pieDataSet.setValueTextColor(Color.BLACK);
                    pieDataSet.setValueTextSize(5f);
                    PieData piedata=new PieData(pieDataSet);
                    pieChart.setData(piedata);
                    pieChart.getDescription().setEnabled(true);
                    pieChart.setCenterText("Turbidity");
                    pieChart.animate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                distance = snapshot.child(esp).child("Water_POINT").getValue(Integer.class);
                tur = snapshot.child(esp).child("Water_Turbidity").getValue(Integer.class);
                ph = snapshot.child(esp).child("PH_Condition").getValue(Integer.class);


                String tr = String.valueOf(tur);
                String Ph = String.valueOf(ph);
                String dt = String.valueOf(distance);
                //Metain a record for 24 hour
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String val = (String.valueOf(esp));
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Log.d("Value", snapshot1.getKey().toString());
                            if (snapshot1.getKey().equals(distance)) {
                                /// Toast.makeText(MainActivity.this, "This order is already done", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (snapshot1.getKey().equals(tur)) {
                                // Toast.makeText(MainActivity.this, "This order is already done", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (snapshot1.getKey().equals(ph)) {
                                //Toast.makeText(MainActivity.this, "This order is already done", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            ref.child("Week").child(val).child("Distance").child(dt).setValue(distance);
                            ref.child("Week").child(val).child("Turbidity").child(tr).setValue(tur);
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









        // Toast.makeText(this, esp, Toast.LENGTH_SHORT).show();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String hhh = snapshot.child(esp).child("Token").getValue(String.class);


                if(distance < 20){
                    sendNotification("Level is low",hhh,"Distance of water");
                }
                if(distance > 80 && distance <=90){
                    sendNotification("Alert!Tank going to be full ",hhh,"Distance of water");
                }
                if(distance > 90 && distance <=100){
                    sendNotification("Tank is full ",hhh,"Distance of water");
                }
                if(tur < 20){
                    sendNotification("Water is Impure  ",hhh,"Turbidity of water");
                }
                if(tur > 20){
                    sendNotification("Water is Fine  ",hhh,"Turbidity of water");
                }
                if(ph<5){
                    sendNotification("Acidic ",hhh,"PH of water");
                }
                if(ph>8){
                    sendNotification("Basic ",hhh,"PH of water");
                }


//
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                        String hhh = snapshot.child("101").child("Token").getValue(String.class);
                        String dis=String.valueOf(distance);
                        if(distance < 20){
                            sendNotification("Level is low",hhh,"Distance of water");
                        }
                        if(distance > 80 && distance <=90){
                            sendNotification("Alert!Tank going to be full ",hhh,"Distance of water");
                        }
                        if(distance > 90 && distance <=100){
                            sendNotification("Tank is full ",hhh,"Distance of water");
                        }

                        if(tur <20){
                            sendNotification("Water is Impure  ",hhh,"Turbidity of water");
                        }
                        if(ph<5){
                            sendNotification("Acidic ",hhh,"PH of water");
                        }
                        if(ph>8){
                            sendNotification("Basic ",hhh,"PH of water");
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
            RequestQueue requestQueue = Volley.newRequestQueue(phsensor2.this);
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
                    String key = "Key=AAAA3IhGvhM:APA91bFwC4btobOTf3Ha1ox00YpowOoZEdFyG93nktlyZCoe0src3bGxrBlGhSEUrcTd_8qQ7dNXNFKnImc8JTnKJP2xwsz3HmgZNqSDdL3V-hihGoKJNzVBK-N2TshV4KIuj1f8k2Ws";
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






    public void move(MenuItem item) {
        startActivity(new Intent(getApplicationContext(),phsensor.class));

    }

    public void own(MenuItem item) {
        startActivity(new Intent(getApplicationContext(),phsensor.class));
    }


    public void logout(MenuItem item) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String hhh = snapshot.child("101").child("3740594265719").child("Token").getValue(String.class);
                //String dis=String.valueOf(distance);
                if(distance < 20){
                    sendNotification("Level is low",hhh,"Distance of water");
                }

                if(distance > 20 && distance <=70){
                    sendNotification("Level is fine ",hhh,"Distance of water");
                }
                if(distance > 80 && distance <=90){
                    sendNotification("Alert!Tank going to be full ",hhh,"Distance of water");
                }
                if(distance > 90 && distance <=100){
                    sendNotification("Tank is full ",hhh,"Distance of water");
                }

//
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    public void distance(MenuItem item) {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    public void tur(MenuItem item) {
        startActivity(new Intent(getApplicationContext(),turbidity_main.class));
    }

    public void ph(MenuItem item) {
        startActivity(new Intent(getApplicationContext(),ph_main.class));
    }

    public void Logout(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        return true;
                    case R.id.menu_item2:
                        // Action for menu item 2
                        startActivity(new Intent(getApplicationContext(),turbidity_main.class));
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


    public void Daily(View view) {
        pieChart.setVisibility(View.GONE);
        barChart.setVisibility(View.VISIBLE);

    }

    public void Weekly(View view) {
        barChart.setVisibility(View.GONE);
        pieChart.setVisibility(View.VISIBLE);
    }
}