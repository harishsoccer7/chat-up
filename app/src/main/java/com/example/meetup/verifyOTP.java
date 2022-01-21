package com.example.meetup;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
//My imports
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;//This class is the entry point for all server-side Firebase Authentication actions
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;//class to represent options object
import com.google.firebase.auth.PhoneAuthProvider;//class to represent the phone number authentication mechanism.

import java.util.concurrent.TimeUnit;//class to specify the units such as SECONDS

public class verifyOTP extends AppCompatActivity {
    private String phone_number,password,verification_id,otp_code;
    private FirebaseAuth auth_object = FirebaseAuth.getInstance();//To get an instance and perform authentication
    private PhoneAuthOptions options;
    private Button otp_button;
    private EditText otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        //Define the widgets
        otp_button = findViewById(R.id.otp_button);
        otp = findViewById(R.id.otp_text);
        //Get the phone number and password from the intent
        phone_number = getIntent().getStringExtra("phone_number");
        //getIntent() returns the intent that started this activity
        password = getIntent().getStringExtra("password");
        otp_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                otp_code = otp.getText().toString();
                if(otp_code.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter the OTP",Toast.LENGTH_SHORT).show();
                }
                else{
                    // below line is used for getting getting
                    // credentials from our verification id and code.
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification_id,otp_code);
                    signInMethod(credential);
                }
            }
        });//end of otp_button widget
        sendVerificationCode(phone_number);//call otp sending function
    }

    //Function to send the OTP code to the particular phone number
    private void sendVerificationCode(String phone_number){
        Log.i("OTP_sending","OTP is sending now");
        options = PhoneAuthOptions.newBuilder(auth_object) //returns phone auth options builder class
                .setPhoneNumber(phone_number) //returns phone auth options builder class
                .setTimeout(2L, TimeUnit.MINUTES) //returns phone auth options builder class
                .setActivity(this)//returns phone auth options builder class
                .setCallbacks(callback_object) //returns phone auth options builder class
                .build(); //returns phone auth options class

        PhoneAuthProvider.verifyPhoneNumber(options);// Starts the phone verification process with the settings defined in PhoneAuthOptions.
    }//end of the send otp function

    //Callback methods after sending the OTP
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callback_object
            = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            Log.i("verification_completed","Verification completed");
            signInMethod(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            String error = e.getMessage();
            Intent navigate = new Intent(verifyOTP.this,signup.class);
            navigate.putExtra("error",error);
            startActivity(navigate);
        }

        @Override
        public void onCodeSent(String s,PhoneAuthProvider.ForceResendingToken token){
            Log.i("OTP_sent","OTP is sending now and waiting");
            verification_id = s;
        }
    };// end of Callback methods

    private void signInMethod(PhoneAuthCredential credential){
        Log.i("sign_in_method","In the signin method");
        auth_object.signInWithCredential(credential)//Returns task(means asynchronous operation) object
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent navigate = new Intent(verifyOTP.this,login.class);
                            startActivity(navigate);
                            //code to login screen
                        }
                        else{
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }//end of signInMethod

}