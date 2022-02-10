package com.example.meetup;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Login extends AppCompatActivity {
    private Button session_out,login_signout;
    private User user_object;
    private String phone_number;
    private DatabaseReference databaseReference;
    private RecyclerView contactList;
    //private ContactListAdapter contactListAdapter;
    private HashMap<String,String> contacts=new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PackageManager.PERMISSION_GRANTED);
        user_object = (User)getIntent().getSerializableExtra("user_object");
        Toast.makeText(getApplicationContext(), user_object.user_name+" is logged inside the chat room successfully...", Toast.LENGTH_LONG).show();

        //Get the phone number from intent and retrieve database reference using it
        phone_number = getIntent().getStringExtra("phone_number");
        databaseReference = FirebaseDatabase.getInstance().getReference("USERS/"+phone_number);

        //getSerializable() returns Serializable object so typecast to User class type
        if(user_object.session==false) {
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
        Contact_retrieval();
    }
    @SuppressLint("Range")
    private void Contact_retrieval() {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("users_list");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    /**
                     * What is cursor object ? This interface provides random read-write access to the result set returned by a database query.
                     *  getContentResolver() - Each Activity within an Android application has a content resolver that can be returned using getContentResolver(). The content resolver determines the correct content provider based on the URI of the request and then routes the request to the appropriate content provider.
                     *  query() - Query the given URI, returning a Cursor over the result set.
                     *  getColumnIndex(String ColumnName) - Returns the zero-based index for the given column name, or -1 if the column doesn't exist.
                     *  getString(int ColumnIndex) - Returns the value of the requested column as a String.
                     *  close() in cursor - Closes the Cursor, releasing all of its resources and making it completely invalid.
                     *  ContactsContract -defines an extensible database for contact related information
                     */
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
                    while (phones.moveToNext())
                    {
                        String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumber=phoneNumber.replaceAll(" ","").replaceAll("\\+91","");
                        if(snapshot.hasChild(phoneNumber) && !contacts.containsKey(phoneNumber)){
                            contacts.put(phoneNumber,name);
                        }
                    }//end of while loop
                    for (String s : contacts.values()) {
                        Log.i("data_in_contact_main", s);
                    }
                    phones.close();
                    if(contacts.isEmpty()){
                        //show no contacts
                        return;
                    }
                    //contactList = findViewById(R.id.contact_list);//recyclerView object
                    // contactListAdapter = new ContactListAdapter(contacts,getApplicat);
                    //contactList.setAdapter(contactListAdapter);// An Adapter object acts as a bridge between an AdapterView and the underlying data for that view. The Adapter provides access to the data items. The Adapter is also responsible for making a View for each item in the data set.
                    //contactList.setLayoutManager(new LinearLayoutManager(Login.this));// LayoutManager is a class that tells Adapters how to arrange the items in the recycler view.

                }// end of if
                else{
                    Intent navigate = new Intent(Login.this, ErrorPage.class);
                    startActivity(navigate);
                }
            }//end of the function onDataChange
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Intent navigate = new Intent(Login.this, ErrorPage.class);
                navigate.putExtra("error",error.getMessage());
                startActivity(navigate);
            }
        });// end of the event listener in the database
    }
}