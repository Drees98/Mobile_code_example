package com.example.schedulingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

public class createSchedule extends AppCompatActivity {
    private Button create, back;
    private File JSON_File;
    private EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);
        //File to store JSON data
        JSON_File = getFileStreamPath("FILE_LOCATION");

        create = findViewById(R.id.button);
        edit = findViewById(R.id.editText);
        back = findViewById(R.id.button2);

        //Set the listener for the back button
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(createSchedule.this, MainActivity.class));
            }});

        //Set the listener for the create button, saving the new block and returning to the schedule
        create.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                JsonAdapter.addNewJSONToFile(JSON_File, edit.getText().toString());
                startActivity(new Intent(createSchedule.this, MainActivity.class));
            }});
    }
}
