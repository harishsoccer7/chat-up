package com.example.meetup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class EnterPassword extends AppCompatActivity {
    private User user_object;
    private EditText enter_password_widget;
    private Button submit_enter_password,signout_enter_password;
    private TextView user_name_enter_password;
    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>(){
                @Override
                public void onActivityResult(ActivityResult result){
                    if(result.getResultCode() == 123){
                        setResult(123);
                        finish();
                    }
                    else if(result.getResultCode() == Activity.RESULT_CANCELED){
                        finish();
                    }
                }
            }
            );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);
        Toast.makeText(getApplicationContext(),"Signed in successfully",Toast.LENGTH_LONG).show();
        //Show hi username
        user_object=(User)getIntent().getSerializableExtra("user_object");
        user_name_enter_password = findViewById(R.id.user_name_enter_password);
        user_name_enter_password.setText("Hi "+user_object.user_name+"!!");

        submit_enter_password = findViewById(R.id.submit_enter_password);
        submit_enter_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enter_password_widget = findViewById(R.id.enter_password);
                String enter_password = enter_password_widget.getText().toString();
                //if we compare two strings using '==' operator them references will be compared.so that developer used equals() which compares the value of the strings
                if(enter_password.equals(user_object.password)){
                    Intent navigate = new Intent(EnterPassword.this,login.class);
                    navigate.putExtra("user_object",user_object);
                    navigate.putExtra("phone_number",getIntent().getStringExtra("phone_number"));
                    launcher.launch(navigate);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Password mismatched",Toast.LENGTH_LONG).show();
                }
            }
        });
        signout_enter_password = findViewById(R.id.signout_enter_password);
        signout_enter_password.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View v){
               FirebaseAuth.getInstance().signOut();//signout the user
               setResult(123);
               finish();
           }
        });
    }
}