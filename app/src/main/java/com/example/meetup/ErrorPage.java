package com.example.meetup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ErrorPage extends AppCompatActivity {
    private TextView error_widget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_page);
        error_widget = findViewById(R.id.error_widget);
        if(getIntent().hasExtra("error")){
            error_widget.setText(getIntent().getStringExtra("error"));
        }
        else{
            error_widget.setText("Something went wrong \n Please contact development team");
        }
    }
}