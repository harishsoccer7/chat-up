package com.example.meetup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Welcome extends AppCompatActivity {
    private Button start;
    private TextView error_widget;
    private FirebaseUser current_user;
    private DatabaseReference database_reference;
    private User user_object;
    private ActivityResultLauncher<Intent>  launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>(){
                @Override
                public void onActivityResult(ActivityResult result){
                    if(result.getResultCode() == 123){
                        //Code for refresh an activity
                        finish();//Step :1 Finish Welcome
                        startActivity(getIntent());//Step :2Reload the Welcome
                    }
                    else if(result.getResultCode() == Activity.RESULT_CANCELED){
                        //finish the Welcome.java.This is useful when we press the back button
                        finish();
                    }
                }// end of the onActivityResult() function
            }
    );//end of registerForActivityResult() function

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_welcome);
        current_user = FirebaseAuth.getInstance().getCurrentUser();
        if (current_user != null) {
            //getting the phone from the currently signed in user
            String phone_number = current_user.getPhoneNumber();
            database_reference = FirebaseDatabase.getInstance().getReference("USERS/"+phone_number);
            database_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user_object = dataSnapshot.getValue(User.class);
                    if(user_object==null){
                        Intent navigate = new Intent(Welcome.this, SignUp.class);
                        navigate.putExtra("phone_number",phone_number);
                        launcher.launch(navigate);
                    }
                    else{
                        if(user_object.session==true || user_object.password_protected==false){
                            Intent navigate = new Intent(Welcome.this, Login.class);
                            navigate.putExtra("user_object",user_object);
                            navigate.putExtra("phone_number",phone_number);
                            launcher.launch(navigate);
                        }
                        else if(user_object.password_protected){
                            Intent navigate = new Intent(Welcome.this, EnterPasswordLogin.class);
                            navigate.putExtra("user_object",user_object);
                            navigate.putExtra("phone_number",phone_number);
                            launcher.launch(navigate);
                        }
                    }
                }// end of onDataChange() function
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Navigate to error page
                    Intent navigate = new Intent(Welcome.this, ErrorPage.class);
                    navigate.putExtra("error",databaseError.getMessage());
                    launcher.launch(navigate);
                }
            });
        }
        else{
            Intent navigate = new Intent(Welcome.this,LoginAndSignup.class);
            launcher.launch(navigate);

        }
    }// end of onCreate() function
}//end of class

/** Important Note :
 * Even after calling the finish() function the remianing code of the currently executed function will
 * execute
 *
 * Data retrieval listener from firebase realtime database works in the separate thread so it should be executed last to prevent messing up the execution**/