package com.example.schedulingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.File;

public class makeBlocks extends AppCompatActivity {
private EditText sH, sM, eH, eM;
private Spinner days;
private Intent intent;
private Button create, back;
private File JSON_File;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_blocks);
        back = findViewById(R.id.back);
        sH = findViewById(R.id.sH);
        JSON_File = getFileStreamPath("FILE_LOCATION");
        sM = findViewById(R.id.sM);
        eH = findViewById(R.id.eH);
        eM = findViewById(R.id.eM);
        intent = getIntent();
        final String key = intent.getStringExtra("scheduleKey");
        String s = intent.getStringExtra("schedulePos");
        final int pos = Integer.parseInt(s);

        //Spinner to represent what day the block is to be created for
        days = findViewById(R.id.spinner);

        //Listener for the back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createScheduleIntent = new Intent(makeBlocks.this, SchedulePage.class);
                createScheduleIntent.putExtra("scheduleKey",key);
                createScheduleIntent.putExtra("schedulePos", pos);
                startActivity(createScheduleIntent);

            }
        });

        //Listener for the create block button
        create = findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sH.getText().equals("") && !sM.getText().equals("") && !eH.getText().equals("") && !eM.getText().equals("")){
                    JsonAdapter.addBlock(JSON_File, new TimeBlock(Integer.parseInt(sH.getText().toString()), Integer.parseInt(sM.getText().toString()),
                            Integer.parseInt(eH.getText().toString()), Integer.parseInt(eH.getText().toString())),
                            days.getSelectedItemPosition(), pos);
                }
            }
        });
    }
}
