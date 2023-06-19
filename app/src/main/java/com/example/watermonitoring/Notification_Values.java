package com.example.watermonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Notification_Values extends AppCompatActivity {
   String token,espnum,distance,tur,ph;
   int dis,tr,Ph;
   TextView t1,t2,t3,t4,t5,t6;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://notification-56c0a-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_values);
        t1=(TextView)findViewById(R.id.t1);
        t2=(TextView)findViewById(R.id.t2);
        t3=(TextView)findViewById(R.id.t3);
        t4=(TextView)findViewById(R.id.t4);
        t5=(TextView)findViewById(R.id.t5);
        t6=(TextView)findViewById(R.id.t6);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        token= sharedPreferences.getString("tokens", "null");
        espnum = sharedPreferences.getString("esp", "null");
       ref.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               dis=snapshot.child(espnum).child("Water_POINT").getValue(Integer.class);
               tr=snapshot.child(espnum).child("Water_Turbidity").getValue(Integer.class);
               Ph=snapshot.child(espnum).child("PH_Condition").getValue(Integer.class);
               t2.setText(String.valueOf(dis));
               t4.setText(String.valueOf(tr));
               t6.setText(String.valueOf(Ph));
               if(dis >=0 && dis<20 || tr >=0 && tr <20 ||Ph >6 && Ph<=8 ){
                   t1.setText("Low");
                   t3.setText(" Fine");
                   t5.setText("Water is fine");
               }
               else if(dis >20 && dis<=80 || tr >20 ||Ph >3 && Ph<=5 ){
                   t1.setText("Level is fine");
                   t3.setText(" High");
                   t5.setText("Acidic");
               }
               else if(dis >80 && dis<=90|| tr >20 ||Ph >8 && Ph<=14 ){
                   t1.setText("Alert");
                   t3.setText(" High");
                   t5.setText("Basic");
               }
               else{
                   t1.setText("Full");
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }
}