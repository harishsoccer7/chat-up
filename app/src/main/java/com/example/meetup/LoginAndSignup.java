//CONTEXT meaning : Interface to global information about an application environment.
package com.example.meetup;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

//My import
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;


public class LoginAndSignup extends AppCompatActivity {

    private EditText phone_widget;
    private Button button_widget;
    private String phone_number;
    private ActivityResultLauncher<Intent> launcher= registerForActivityResult(
      new ActivityResultContracts.StartActivityForResult(),
      new ActivityResultCallback<ActivityResult>(){
          @Override
          public void onActivityResult(ActivityResult result){
              if(result.getResultCode()==-123){
                  Toast.makeText(getApplicationContext(),result.getData().getStringExtra("error"),Toast.LENGTH_LONG).show();
              }//Code to run if any error occurs in the launched activity
              else if(result.getResultCode()==321){
                  Log.i("LoginAndSignup","back to LoginAndSignup");
              }//Nothing happen when pressing LoginAndSignup button
              else if(result.getResultCode()== Activity.RESULT_CANCELED){
                  finish();
              }//Code to run when pressing back button
              else if(result.getResultCode()==123){
                  setResult(123);
                  finish();
              }//Code to run when OTP verification is successful
          }// end of onActivity function
      }// end of callback interface
    );// end of launcher function

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_signup);

        //My code
        //Initializing variables for widgets
        phone_widget = findViewById(R.id.phone_number);
        button_widget = findViewById(R.id.signup);

        button_widget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone_number = phone_widget.getText().toString();
                if (phone_number.isEmpty()) {
                    phone_widget.setError("Don't leave field empty");
                    phone_widget.requestFocus();
                }
                else if(phone_number.length()!=10){
                        phone_widget.setError("Length of the phone number must be 10");
                        phone_widget.requestFocus();
                }
                else{
                    phone_number = "+91" + phone_number;
                    Intent navigate = new Intent(LoginAndSignup.this, AuthenticateOTP.class);
                    navigate.putExtra("phone_number",phone_number);
                    launcher.launch(navigate);
                }
            }//end of onClick() function
            /**boolean validate_phone_number(String phone_number){//Starts phone number validation
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
            }//end phone number validation**/
        });// end of the button widget
    }
}