package com.example.watermonitoring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class Login extends AppCompatActivity {
    EditText cnic,password,esp;
    String tokens;
    String espnum;
    SharedPreferences sharedPreferences;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://notification-56c0a-default-rtdb.firebaseio.com/");
    public static String PREFS_NAME="MyPrefsFile";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent=new Intent();
        password=(EditText) findViewById(R.id.password);
        cnic=(EditText) findViewById(R.id.cnic);
        esp=(EditText) findViewById(R.id.espnum);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(Login.this,"Not generated",Toast.LENGTH_SHORT).show();

                }
                Log.d("data",task.getResult().toString());
                tokens=task.getResult();
                //Toast.makeText(Login.this, tokens, Toast.LENGTH_SHORT).show();
               sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", tokens);
                editor.apply();
            }
        });


    }

    public void login(View view) {
        String Cnic=cnic.getText().toString();
        String Password=password.getText().toString();
        String Esp=esp.getText().toString();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    if(snapshot1.getKey().equals(Esp)){
                        ref.child(Esp).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChild(Cnic)){
                                    String pass=snapshot.child(Cnic).child("Password").getValue().toString();
                                    if(pass.equals(Password)){
                                        // startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                     sharedPreferences = getSharedPreferences("MyPrefs", 0);
                                        SharedPreferences.Editor editor=sharedPreferences.edit();
                                        editor.putBoolean("hasLoggedIn",true);
                                        editor.commit();
ref.child(Esp).child(Cnic).addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        String tk=snapshot.child("Token").getValue(String.class);
        if(tk.equals(tokens)){
           //Toast.makeText(Login.this,tokens,Toast.LENGTH_SHORT).show();
            sharedPreferences = getSharedPreferences("MyPrefs", 0);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("tokens",tokens);
            editor.putString("esp",Esp);
            editor.apply();
            Toast.makeText(Login.this,Esp,Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),Home.class));
            finish();
        }
        else{
            ref.child(Esp).child(Cnic).child("Token").setValue(tokens);
            sharedPreferences = getSharedPreferences("MyPrefs", 0);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("tokens",tokens);
            editor.putString("esp",Esp);
            editor.apply();
            Toast.makeText(Login.this,Esp,Toast.LENGTH_SHORT).show();
           startActivity(new Intent(getApplicationContext(),Home.class));
            finish();
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});

                                    }
                                    else{
                                        Toast.makeText(Login.this, " Incorrect password", Toast.LENGTH_SHORT).show();

                                    }
                                }
                                else{
                                    Toast.makeText(Login.this, " Incorrect Cnic", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    }
                    else{
                        Toast.makeText(Login.this, "Esp not exist", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void back(View view) {
        startActivity(new Intent(getApplicationContext(),Register.class));
    }


}