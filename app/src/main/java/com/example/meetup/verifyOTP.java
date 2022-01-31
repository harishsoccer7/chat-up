package com.example.meetup;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
//My imports
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;//class to specify the units such as SECONDS

public class verifyOTP extends AppCompatActivity {
    private String phone_number,verification_id,otp_code;
    private FirebaseAuth auth_object = FirebaseAuth.getInstance();//To get an instance and perform authentication
    private PhoneAuthProvider.ForceResendingToken resend_token;
    private Button verify_otp_submit,verify_otp_signup;
    private EditText otp_text;
    private TextView resend_otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        //Get the phone number from the intent
        phone_number = getIntent().getStringExtra("phone_number");
        //getIntent() returns the intent that started this activity
        sendVerificationCode();//call otp sending function

        verify_otp_signup = findViewById(R.id.verify_otp_signup);
        verify_otp_signup.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               setResult(321);
               finish();
           }
        });
        verify_otp_submit = findViewById(R.id.verify_otp_submit);
        verify_otp_submit.setEnabled(false);//This button is enabled false in the beginning to avoid verification id null exception so it will be enabled in the onCodeSent callback function only to avoid that exception
        verify_otp_submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                resend_otp.setEnabled(false);
                verify_otp_submit.setEnabled(false);
                otp_text = findViewById(R.id.otp_text);
                otp_code = otp_text.getText().toString();
                if(otp_code.isEmpty() || otp_code.length()!=6){
                    Toast.makeText(getApplicationContext(),"Invalid OTP Code",Toast.LENGTH_LONG).show();
                    resend_otp.setEnabled(true);
                    verify_otp_submit.setEnabled(true);
                }
                else{
                    // below line is used for getting getting
                    // credentials from our verification id and code.
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification_id,otp_code);
                    signInMethod(credential);
                }
            }
        });//end of otp_button widget
        resend_otp = findViewById(R.id.resend_otp);
        resend_otp.setEnabled(false);
    }

    //Function to send the OTP code to the particular phone number
    private void sendVerificationCode(){
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth_object) //returns phone auth options builder class
                .setPhoneNumber(phone_number) //returns phone auth options builder class
                .setTimeout(2L, TimeUnit.MINUTES) //returns phone auth options builder class
                //setTimeout is the time to wait for auto retrieval of sms code and produce phoneAuth credential
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
            signInMethod(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Intent temp=new Intent();
            //set the error message in an intent and send it to previous activity where the current activity is launched ie.,signup
            temp.putExtra("error",e.getMessage());
            setResult(-123,temp);
            finish();
        }// Exception is thrown when there is an error in phone number

        @Override
        public void onCodeSent(String s,PhoneAuthProvider.ForceResendingToken token){
            verification_id = s;
            resend_token = token;
            //enable the verifyOTP button
            verify_otp_submit.setEnabled(true);
            resend_otp.setEnabled(true);
        }
    };// end of Callback methods

    private void signInMethod(PhoneAuthCredential credential){
        auth_object.signInWithCredential(credential)//Returns task(means asynchronous operation) object
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                                setResult(123);
                                finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            resend_otp.setEnabled(true);
                            verify_otp_submit.setEnabled(true);
                        }//The task is unsuccessful when people enter invalid or wrong OTP code
                    }// end of onComplete function
                });//end of addOnCompleteListener
    }//end of signInMethod

    public void resend_otp_function(View v){
        resend_otp.setEnabled(false);
        verify_otp_submit.setEnabled(false);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth_object)//returns phone auth options builder object
                .setPhoneNumber(phone_number)
                .setTimeout(120L,TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callback_object)
                .setForceResendingToken(resend_token)
                .build();//returns phone auth options object

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}