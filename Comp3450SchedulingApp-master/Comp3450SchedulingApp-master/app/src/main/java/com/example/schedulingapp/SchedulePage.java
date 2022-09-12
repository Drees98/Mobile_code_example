package com.example.schedulingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

//Page that displays a particular schedule when selected from the main activity
public class SchedulePage extends AppCompatActivity {
    private File JSON_File;
    private Intent intent;
    private TextView text, schedName, schedule;
    private static JSONArray jarray;
    private Button prev, next, back, create, qr;
    private String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_page);

        //Variables
        intent = getIntent();
        JSON_File = getFileStreamPath("FILE_LOCATION");
        final String key = intent.getStringExtra("scheduleKey");
        final int pos = intent.getIntExtra("schedulePos",0);
        schedName = findViewById(R.id.name);
        schedName.setText(key);
        text = findViewById(R.id.day);

        //Default value for first day seen
        text.setText("Monday");

        schedule = findViewById(R.id.Schedule);

        //JSON Data loaded from file
        final String jsonFile = JsonAdapter.loadJSONFromAsset(JSON_File);
        try {
            //Get the JSON data
            JSONObject jobj = new JSONObject(jsonFile);
            JSONArray jArray = (JSONArray) jobj.get("Schedules");
            JSONObject j = (JSONObject)jArray.get(pos);
            JSONObject schedObject = (JSONObject)j.get(key);
            jarray = (JSONArray) schedObject.get("days");

            //Display the timeblocks
            schedule.setText(displayTimeBlocks(jarray.get(0).toString()));

            //Next button cycles through days
            next = findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   count++;
                   count = count%7;
                   text.setText(days[count]);
                   try {
                       schedule.setText(displayTimeBlocks(jarray.get(count).toString()));
                   }catch(JSONException e){
                       e.printStackTrace();
                    }
                }
            });

            //Previous button cycles through days
            prev = findViewById(R.id.prev);
            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    count--;
                    count = count%7;
                    if(count<0){
                        count = 6;
                    }
                    text.setText(days[count]);
                    try {
                        schedule.setText(displayTimeBlocks(jarray.get(count).toString()));
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });

            //Moves to the makeBlocks page to create new timeblock(s)
            create = findViewById(R.id.add);
            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent createBlockIntent = new Intent(SchedulePage.this, makeBlocks.class);
                    String sendPos = pos + "";
                    createBlockIntent.putExtra("schedulePos", sendPos);
                    createBlockIntent.putExtra("scheduleKey", key);
                    startActivity(createBlockIntent);
                }
            });

            //Goes back to the schedule page
            back = findViewById(R.id.back2);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent createScheduleIntent = new Intent(SchedulePage.this, MainActivity.class);
                    startActivity(createScheduleIntent);
                }
            });

            //Generates a QR code of the entire schedule for sharing
            qr = findViewById(R.id.qr);
            qr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent createScheduleIntent = new Intent(SchedulePage.this, QRGeneratorActivity.class);
                    try {
                        JSONObject jobj = new JSONObject(jsonFile);
                        JSONArray jArray = jobj.getJSONArray("Schedules");
                        JSONObject schedObject = (JSONObject) jArray.get(pos);
                        createScheduleIntent.putExtra("SCHEDULE_INFO", schedObject.toString());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    startActivity(createScheduleIntent);
                }
            });

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    private String displayTimeBlocks(String data){
        try {
            JSONObject jObj = new JSONObject(data);
            JSONArray blockArr = jObj.getJSONArray(jObj.keys().next());
            String output = "", temp;
            StringBuilder stringBuilder = new StringBuilder(output);

            int sH, sM, eH, eM;

            for(int i = 0; i < blockArr.length(); i++){
                sH = blockArr.getJSONObject(i).getInt("sH");
                sM = checkLengthInt(blockArr.getJSONObject(i).getInt("sM"));
                eH = blockArr.getJSONObject(i).getInt("eH");
                eM = checkLengthInt(blockArr.getJSONObject(i).getInt("eM"));
                temp = "Block " + i + ": " + sH + ":" + sM + " - " + eH + ":" + eM + "\n";
                stringBuilder.append(temp);
            }
            return stringBuilder.toString();
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Something has gone wrong!", Toast.LENGTH_SHORT)
                    .show();
        }
        return "";
    }

    private int checkLengthInt(int i){
        int r = i;
        if(r < 10){
            r+=10;
            return r;
        }
        else{
            return i;
        }
    }
}
