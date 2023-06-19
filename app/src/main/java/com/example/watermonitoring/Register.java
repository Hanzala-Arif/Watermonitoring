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

public class Register extends AppCompatActivity {
    EditText name,password,email,espnum,cnic;
    String token,epnum;
    private SharedPreferences sharedPreferences;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://notification-56c0a-default-rtdb.firebaseio.com/");    @SuppressLint("MissingInflatedId")
    public static final String EXTRA_TEXT =  " com.example.Login.extra.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        name=(EditText) findViewById(R.id.name);
        password=(EditText) findViewById(R.id.password);
        email=(EditText) findViewById(R.id.email);
        espnum=(EditText) findViewById(R.id.espnum);
        cnic=(EditText) findViewById(R.id.cnic);

               sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        token= sharedPreferences.getString("token","null");
        Toast.makeText(Register.this,token,Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("tokens", token);
        editor.apply();
    }



    public void Register(View view) {
        String Name=name.getText().toString();
        String Password=password.getText().toString();
        String Email=email.getText().toString();
        String Espnum=espnum.getText().toString();
        String Cnic=cnic.getText().toString();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(Name.isEmpty() || Password.isEmpty() || Email.isEmpty() ||Espnum.isEmpty() || Cnic.isEmpty()){
                    Toast.makeText(Register.this, "Fill it Completely", Toast.LENGTH_SHORT).show();
                }
                else {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        if (snapshot1.getKey().equals(Espnum)) {
                            ref.child(Espnum).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(Cnic)) {
                                        Toast.makeText(Register.this, "Account already created", Toast.LENGTH_SHORT).show();
                                         startActivity(new Intent(getApplicationContext(),Login.class));
                                        finish();
                                    } else {
                                        ref.child(Espnum).child(Cnic).child("Name").setValue(Name);
                                        ref.child(Espnum).child(Cnic).child("Password").setValue(Password);
                                        ref.child(Espnum).child(Cnic).child("Email").setValue(Email);
                                        ref.child(Espnum).child(Cnic).child("Token").setValue(token);

                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        editor.putString("esp", Espnum);
                                        editor.apply();

                                        Toast.makeText(Register.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                                      startActivity(new Intent(getApplicationContext(),Login.class));
                                    finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            break;
                        } else {
                            Toast.makeText(Register.this, "Esp not exist", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}