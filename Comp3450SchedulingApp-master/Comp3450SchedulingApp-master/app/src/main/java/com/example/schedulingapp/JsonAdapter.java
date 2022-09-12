package com.example.schedulingapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class JsonAdapter {
    //Converts a schedule to a JSON string
    static String toJson(Schedule schedule){
        try{
            //Create the JSON object, an array to hold the days and times.
            JSONObject obj = new JSONObject(), scheduleObject = new JSONObject();
            JSONArray jArrayDay = new JSONArray(), jArray;

            //For each day add it to the JSON as well as time blocks
            for(int i = 0; i<7; i++){
                jArray = new JSONArray();

                //For each timeblock, put the data in
                for(TimeBlock tb : schedule.getTimeBlock(i)){
                    //Creates a JSON object to hold the time blocks
                    JSONObject tbObj = new JSONObject();
                    tbObj.put("sH",tb.getStartHour());
                    tbObj.put("sM",tb.getStartMinute());
                    tbObj.put("eH", tb.getEndHour());
                    tbObj.put("eM", tb.getEndMinute());
                    //tbObj.put("Desc", tb.getDescription());
                    jArray.put(tbObj);
                }

                //Switches the day number into actual text abbreviations and stores them
                JSONObject days = new JSONObject();
                switch(i){
                    case 0:
                        days.put("Mon", jArray);
                        break;

                    case 1:
                        days.put("Tue", jArray);
                        break;

                    case 2:
                        days.put("Wed", jArray);
                        break;

                    case 3:
                        days.put("Thu", jArray);
                        break;

                    case 4:
                        days.put("Fri", jArray);
                        break;

                    case 5:
                        days.put("Sat", jArray);
                        break;

                    case 6:
                        days.put("Sun", jArray);
                        break;

                }
                jArrayDay.put(days);
            }
            obj.put("days",jArrayDay);
            scheduleObject.put(schedule.getName(), obj);
            return scheduleObject.toString();
        }
        catch(JSONException ex){
            ex.printStackTrace();
        }
        //If JSON creation fails, return NULL
        return null;
    }
    //Loads the JSON data from the file
    static String loadJSONFromAsset(File file) {
        String json= "";
        //Output buffer
        StringBuffer output = new StringBuffer();
        try{
            //File writer wrapped in buffered writer for added functionality
            FileReader reader = new FileReader(file.getAbsoluteFile());
            BufferedReader br = new BufferedReader(reader);
            //While there is JSON data to be read, append it to the output buffer
            while((json = br.readLine())!= null){
                output.append(json);
            }
            //Get the data from the buffer
            json = output.toString();
            br.close();
            return json;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return json;
    }

    //Append a schedule object to a specified file.
    static void addJSONToFile(File file, Schedule schedule){
        String existingJson;
        JSONObject jsonObj;
        if(file.exists()){
            try{
                //Get the existing JSON from the file
                existingJson = loadJSONFromAsset(file);
                jsonObj = new JSONObject(existingJson);

                //Put the new Schedule into the JSONArray of schedules
                jsonObj.getJSONArray("Schedules").put(new JSONObject(toJson(schedule)));

                //Overwrite the file with the new data
                saveJSONtoAsset(file, jsonObj.toString());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //Adds a JSON string to a file
    static void addJSONToFile(File file, String data) {
        Schedule schedule = createScheduleFromString(data);
        addJSONToFile(file, schedule);

    }

    //Adds a JSON string to a file
    static void addNewJSONToFile(File file, String data) {
        Schedule schedule = new Schedule(data);
        addJSONToFile(file, schedule);

    }

    //Saves a schedule to a JSON file, overwriting the entire file
    static void saveJSONtoAsset(File file, String schedule){
        FileWriter fw;
        BufferedWriter bw;
        if(!file.exists()){
            try{
                file.createNewFile();
                fw = new FileWriter(file.getAbsoluteFile());
                bw = new BufferedWriter(fw);
                bw.write("{}");
                bw.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        try{
            fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
            fw.write(schedule);
            bw.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //Adds a timeblock to the specified schedule in the schedule array
    static void addBlock(File file, TimeBlock time, int day, int scheduleLocation){
        String json = loadJSONFromAsset(file);
        try{
            //Get the relevant JSON data
        JSONObject mainObj = new JSONObject(json);
        JSONArray schedulesArr = mainObj.getJSONArray("Schedules");
        JSONObject schedule = (JSONObject) schedulesArr.get(scheduleLocation);
        JSONObject scheduleObj = schedule.getJSONObject(schedule.keys().next());
        JSONArray dayArray = scheduleObj.getJSONArray("days");

        //Create JSON objects for new data
        JSONObject add;
        JSONArray addTo;
        JSONObject tbObj = new JSONObject();

        //Depending on the day, get relevant values
        switch(day){
            case 0:
                add = (JSONObject) dayArray.get(0);
                addTo = (JSONArray)add.get("Mon");
                break;

            case 1:
                add = (JSONObject) dayArray.get(1);
                addTo = (JSONArray)add.get("Tue");
                break;
            case 2:
                add = (JSONObject) dayArray.get(2);
                addTo = (JSONArray)add.get("Wed");
                break;
            case 3:
                add = (JSONObject) dayArray.get(3);
                addTo = (JSONArray)add.get("Thu");
                break;
            case 4:
                add = (JSONObject) dayArray.get(4);
                addTo = (JSONArray)add.get("Fri");
                break;
            case 5:
                add = (JSONObject) dayArray.get(5);
                addTo = (JSONArray)add.get("Sat");
                break;
            case 6:
                add = (JSONObject) dayArray.get(6);
                addTo = (JSONArray)add.get("Sun");
                break;
            default:
                addTo = null;
        }
            //put the timeblock data in the objects
            tbObj.put("sH",time.getStartHour());
            tbObj.put("sM",time.getStartMinute());
            tbObj.put("eH", time.getEndHour());
            tbObj.put("eM", time.getEndMinute());
            addTo.put(tbObj);

            //Save the data
            saveJSONtoAsset(file, mainObj.toString());

        //schedulesArr.put(scheduleLocation, schedule);
        //saveJSONtoAsset(file, mainObj.toString());
        }
        catch(JSONException e){
            e.printStackTrace();

        }
    }

    //Converts a JSON string to a schedule object
    static Schedule createScheduleFromString(String data){
        Schedule schedule;
        String schName;
        JSONObject mainObj, schObj;
        JSONArray daysArr;
        JSONArray[] arrOfDays = new JSONArray[7];
        try{
            //Get the JSON objects from the JSON string
            mainObj = new JSONObject(data);
            schName = mainObj.keys().next();
            schObj = mainObj.getJSONObject(schName);
            daysArr = schObj.getJSONArray("days");

            //Get the days
            arrOfDays[0] = daysArr.getJSONObject(0).getJSONArray("Mon");
            arrOfDays[1] = daysArr.getJSONObject(1).getJSONArray("Tue");
            arrOfDays[2] = daysArr.getJSONObject(2).getJSONArray("Wed");
            arrOfDays[3] = daysArr.getJSONObject(3).getJSONArray("Thu");
            arrOfDays[4] = daysArr.getJSONObject(4).getJSONArray("Fri");
            arrOfDays[5] = daysArr.getJSONObject(5).getJSONArray("Sat");
            arrOfDays[6] = daysArr.getJSONObject(6).getJSONArray("Sun");

            //Create variables for new data
            schedule = new Schedule(schName);
            int sH, sM, eH, eM, tempI;

            //For each day, get the timeblock data and add it to the schedule
            for(int i = 0; i < 7; i ++){
                tempI = arrOfDays[i].length();
                for(int j = 0; j < tempI; j++) {
                    JSONObject tmpX = arrOfDays[i].getJSONObject(j);
                    sH = tmpX.getInt("sH");
                    sM = tmpX.getInt("sM");
                    eH = tmpX.getInt("eH");
                    eM = tmpX.getInt("eM");

                    switch(i) {
                        case 0:
                            schedule.monday.add(new TimeBlock(sH, sM, eH, eM));
                            break;
                        case 1:
                            schedule.tuesday.add(new TimeBlock(sH, sM, eH, eM));
                            break;
                        case 2:
                            schedule.wednesday.add(new TimeBlock(sH, sM, eH, eM));
                            break;
                        case 3:
                            schedule.thursday.add(new TimeBlock(sH, sM, eH, eM));
                            break;
                        case 4:
                            schedule.friday.add(new TimeBlock(sH, sM, eH, eM));
                            break;
                        case 5:
                            schedule.saturday.add(new TimeBlock(sH, sM, eH, eM));
                            break;
                        case 6:
                            schedule.sunday.add(new TimeBlock(sH, sM, eH, eM));
                            break;

                    }
                }
            }
            return schedule;
        }
        catch (Exception e){
            e.printStackTrace();
            return new Schedule("red");
        }
    }

    //Gets the list of JSON schedule objects from the file
    static List<String> getJSONObjects(String data) {
        try {
            //Get the JSON data
            JSONObject mainObj = new JSONObject(data), tempObj;
            JSONArray schedulesArr = mainObj.getJSONArray("Schedules");

            //Create a new list and add all of the schedules to it
            List<String> resultArr = new ArrayList<>();
            for(int i = 0; i < schedulesArr.length();i++) {
               tempObj = new JSONObject(schedulesArr.getString(i));
               resultArr.add(tempObj.toString());
            }
            return resultArr;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
