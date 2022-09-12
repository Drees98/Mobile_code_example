package com.example.schedulingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Schedule> schedules;
    private Button scan;
    private static File JSON_File;
    private static String File_Path;
    private List<String> JSONObjects = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            //The file where JSON information is stored
            JSON_File = getFileStreamPath("FILE_LOCATION");
            File_Path = JSON_File.getAbsolutePath();
            createFile(JSON_File);

            //Button for the QRScanner
            scan = findViewById(R.id.button3);

            //Load JSON data and store
            String jsonData = JsonAdapter.loadJSONFromAsset(JSON_File);

            //Get a list of all the stored schedule names and save them.
            schedules = new ArrayList<>();
            JSONObject jObj;
            if(!jsonData.equals("")) {
                JSONObjects = JsonAdapter.getJSONObjects(jsonData);
                for(int i = 0; i < JSONObjects.size(); i++) {
                        jObj = new JSONObject(JSONObjects.get(i));
                        schedules.add(new Schedule(jObj.keys().next()));
                }
            }

            //Create the recycler view to display the stored schedules
            recyclerView = findViewById(R.id.schedulerView);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(llm);
            ScheduleAdapter adapter = new ScheduleAdapter(schedules);
            recyclerView.setAdapter(adapter);

            //Create the on touch listener for the schedule items so they may be accessed
            recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent detailsIntent = new Intent(MainActivity.this, SchedulePage.class);
                    detailsIntent.putExtra("FILE_LOCATION", File_Path);
                    try {
                        JSONObject jObj = new JSONObject(JSONObjects.get(position));
                        detailsIntent.putExtra("scheduleKey",jObj.keys().next());
                        detailsIntent.putExtra("schedulePos", position);
                    }
                    catch (JSONException e){
                      e.printStackTrace();
                    }
                    startActivity(detailsIntent);
                }

                //Does nothing on long click
                @Override
                public void onItemLongClick(View view, int position) {
                    // ...
                }
            }));

            //Button for the QRScanner
            scan = findViewById(R.id.button3);
            scan.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                   Intent readerIntent = new Intent(MainActivity.this, QRReaderActivity.class);
                   readerIntent.putExtra("FILE_LOCATION", File_Path);
                   startActivity(readerIntent);
                }});

                //New schedule button
                FloatingActionButton fab = findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent createScheduleIntent = new Intent(MainActivity.this, createSchedule.class);
                        createScheduleIntent.putExtra("FILE_LOCATION", File_Path);
                        startActivity(createScheduleIntent);
                    }
                });}
catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Method to create a file and instantiate it with proper data if it does not exist already
    private boolean createFile(File file){
        try {
            if (!file.exists()) {
                FileWriter fw;
                BufferedWriter bw;
                file.createNewFile();
                fw = new FileWriter(file.getAbsoluteFile());
                bw = new BufferedWriter(fw);

                JSONObject obj = new JSONObject();
                JSONArray arr = new JSONArray();

                obj.put("Schedules",arr);
                bw.write(obj.toString());
                bw.close();
            }
            return true;

        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }



}
