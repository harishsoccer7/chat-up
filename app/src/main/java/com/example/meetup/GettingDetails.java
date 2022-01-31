package com.example.meetup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class GettingDetails extends AppCompatActivity {
    private EditText user_name_widget,password_widget;
    private RadioButton password_protection_status_widget;
    private Button finish,signout_getting_details;
    private User user_object;
    private String user_name,password;
    private boolean password_protection_status,check=false;
    //check variable is initialized here because check status has to be noted from the starting of the activity.If i declare it inside the onclick function then when  each time radio button is clicked ,check is initialized newly ie.,starting value
    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>(){
                @Override
                public void onActivityResult(ActivityResult result){
                    if(result.getResultCode()==123){
                        setResult(123);
                        finish();
                    }
                    else if(result.getResultCode()== Activity.RESULT_CANCELED){
                        finish();
                    }
                }// end of onActivityResult function
            }// end of ActivityResultCallback interface implemented andnymous class
    );// end of registerForActivityResult function

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getting_details);
        Toast.makeText(getApplicationContext(),"Signed in successfully",Toast.LENGTH_LONG).show();
        password_protection_status_widget = findViewById(R.id.password_protection_status);
        password_protection_status_widget.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(check){//Returns true when the radio button is checked
                    password_protection_status_widget.setChecked(false);
                    check=false;
                }
                else{
                    //This one is done automatically
                    //password_protection_status_widget.setChecked(true);
                    check=true;
                }
            }// end of onClick() function
        });// end of onClickListener
        signout_getting_details = findViewById(R.id.signout_getting_details);
        signout_getting_details.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               FirebaseAuth.getInstance().signOut();//signOut the user
               //set the result and finish the current GettingDetails activity
               setResult(123);
               finish();
           }
        });
        finish = findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Declaring widgets
                user_name_widget = findViewById(R.id.user_name);
                password_widget = findViewById(R.id.password);
                //Getting the values from the widgets
                user_name = user_name_widget.getText().toString();
                password = password_widget.getText().toString();
                if(user_name.isEmpty() || password.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Don't leave the fields empty",Toast.LENGTH_LONG).show();
                    return;
                }
                else if(validate_password()){
                    return;
                }
                else {
                    if(password_protection_status_widget.isChecked()){
                        password_protection_status = true;
                    }
                    else{
                        password_protection_status = false;
                    }
                    //store the data
                    //step:1 create a user-defined user object
                    user_object = new User(user_name,password,password_protection_status,true);
                    //step:2 store the user object in the firebase realtime database
                    String phone_number = getIntent().getStringExtra("phone_number");
                    FirebaseDatabase.getInstance()
                            .getReference("USERS")
                            .child(phone_number)
                            .setValue(user_object);//Sets the value on the database reference
                    //navigate to login page after storing the user data
                    Intent navigate = new Intent(GettingDetails.this,login.class);
                    navigate.putExtra("user_object",user_object);
                    navigate.putExtra("phone_number",phone_number);
                    launcher.launch(navigate);
                }
            }//end of the onClick function

            boolean validate_password(){
                if(password.length()<6){//checking if it contains 6 characters
                    password_widget.setError("Password must be atleast 6 characters");
                    password_widget.requestFocus();
                    return true;
                }
                //checking if it is in the correct format
                boolean upper=false,lower=false,special=false,digit=false;
                for(int i=0;i<password.length();i++){
                    int ascii = password.charAt(i);
                    if(ascii>=65 && ascii<=90){
                        upper = true;
                    }
                    else if(ascii>=97 && ascii<=122){
                        lower = true;
                    }
                    else if(ascii>=48 && ascii<=57){
                        digit = true;
                    }
                    else if((ascii>=32 && ascii<=47) || (ascii>=58 && ascii<=64) || (ascii>=91 && ascii<=96) || (ascii>=123 && ascii<=126)){
                        special = true;
                    }

                    if(upper && lower && digit && special){
                        return false;
                    }
                }
                password_widget.setError("Password must contain atleast 1 upper case letter,1 lower case letter,one digit and 1 special character");
                password_widget.requestFocus();
                return true;
            }//end of validate_password function
        });
    }
}