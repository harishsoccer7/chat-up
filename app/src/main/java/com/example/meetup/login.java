package com.example.meetup;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {
    private Button session_out,login_signout;
    private User user_object;
    private String phone_number;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user_object = (User)getIntent().getSerializableExtra("user_object");
        Log.i("check0","Inside login");
        Toast.makeText(getApplicationContext(), user_object.user_name+" is logged inside the chat room successfully...", Toast.LENGTH_LONG).show();

        //Get the phone number from intent and retrieve database reference using it
        phone_number = getIntent().getStringExtra("phone_number");
        databaseReference = FirebaseDatabase.getInstance().getReference("USERS/"+phone_number);

        //getSerializable() returns Serializable object so typecast to User class type
        if(user_object.session==false) {
            Log.i("check0","Inside change session value");
            databaseReference.child("session").setValue(true);
        }

        session_out = findViewById(R.id.session_out);
        session_out.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //user object is not a database referenced user object So we have to setvalue in the database using database referenced object
                databaseReference.child("session").setValue(false);
                finish();
            }
        });
        login_signout = findViewById(R.id.login_signout);
        login_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("session").setValue(false);
                FirebaseAuth.getInstance().signOut();
                //Code to send for previous activity
                setResult(123);
                finish();
            }
        });
    }
}