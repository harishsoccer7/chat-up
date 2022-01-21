//CONTEXT meaning : Interface to global information about an application environment.
package com.example.meetup;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

//My import
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;
import java.util.regex.*;

public class signup extends AppCompatActivity {

    private EditText phone_widget,password_widget;
    private Button button_widget;
    private String phone_number,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if(getIntent().hasExtra("error")){//hasExtra() - Returns true if an extra value is associated with the given name.
            Toast.makeText(getApplicationContext(),getIntent().getStringExtra("error"),Toast.LENGTH_LONG).show();
        }

        //My code
        //Initializing variables for widgets
        phone_widget = findViewById(R.id.phone_number);
        password_widget = findViewById(R.id.password);
        button_widget = findViewById(R.id.signup);

        button_widget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone_number = phone_widget.getText().toString();
                password = password_widget.getText().toString();
                if (phone_number.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Don't leave empty field", Toast.LENGTH_SHORT).show();
                }
                else if(validate_phone_number(phone_number) || validate_password(password)){
                        return;
                }
                else{
                    phone_number = "+91" + phone_number;
                    Intent navigate = new Intent(signup.this,verifyOTP.class);
                    navigate.putExtra("phone_number",phone_number);
                    navigate.putExtra("password",password);
                    startActivity(navigate);
                }
            }
        });// end of the button widget
    }

    private Boolean validate_phone_number(String phone_number){//Starts phone number validation
        if(phone_number.length()!=10){
            phone_widget.setError("Phone number must be exactly 10 numbers");
            phone_widget.requestFocus();
            return true;
        }
        else{
            String pattern = "[7-9][0-9]{9}";
            if(!phone_number.matches(pattern)){
                phone_widget.setError("Invalid phone number");
                phone_widget.requestFocus();
                return true;
            }
            return false;
        }
    }//end phone number validation
    private Boolean validate_password(String password){
        return false;
    }
}