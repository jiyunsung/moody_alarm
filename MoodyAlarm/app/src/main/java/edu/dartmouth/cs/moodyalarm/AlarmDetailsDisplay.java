package edu.dartmouth.cs.moodyalarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by vivianjiang on 2/25/18.
 */

public class AlarmDetailsDisplay extends Fragment {

    public View view;
    Button btn;
    SharedPreferences preferences;
    public String time = "";
    public AlarmEntry entry;
    public static String setting="weather";
    public Boolean[]daysOfWeek;
    private LinearLayout weekdays;
    public Boolean isNew;
    public Context context;

    private static final String TIME = "none";


    public static AlarmDetailsDisplay newInstance(String time, AlarmEntry e, Boolean isNew) {
        Bundle args = new Bundle();
        args.putString(TIME, time);
        Log.d("newInstance alarmdet", "time is " + time)
;
        args.putSerializable("alarm", e);
        args.putBoolean("isNew", isNew);
        AlarmDetailsDisplay fragment = new AlarmDetailsDisplay();
        fragment.setArguments(args);

        return fragment;
    }


    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.expanded, container, false);
        context = getActivity();
        super.onCreate(savedInstanceState);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        int hour = entry.getHour();

        int minute = entry.getMinute();

        String min = "";
        if ((entry.getMinute() < 10)){
            min = "0"+Integer.toString(minute);

        } else{
            min = Integer.toString(minute);

        }


        String t = entry.getHour() + ":" + min;
        Log.d("alarm details", "before string t is " + t);
        TextView timeDisplay = view.findViewById(R.id.labelExpanded);
        timeDisplay.setText(t);
        final String time = timeDisplay.getText().toString();
        Log.d("alarm details", "after string time is " + time);
        Log.d("onResume", "time is " + time);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View v = view;

        Bundle args = getArguments();
        time = args.getString(TIME);

        entry = (AlarmEntry) args.getSerializable("alarm");

        isNew = args.getBoolean("isNew");
        if (isNew){
            setting = "weather";
        } else{
            setting = entry.getSetting();
        }


        daysOfWeek = entry.getDaysofweek();
        if (daysOfWeek == null){
            Log.d("alarm details display", "days of week is null");
            daysOfWeek= new Boolean[]{false, false, false, false, false, false, false};
        } else{
            Log.d("alarm details display", "days of week not null");
        }

        Button weather = view.findViewById(R.id.weatherPlaylist);
        Button day = view.findViewById(R.id.dayPlaylist);

        Switch onOff = view.findViewById(R.id.switchOnOff);
        if(entry.getOnOff() == 1)
            onOff.setChecked(true);
        else
            onOff.setChecked(false);

        weekdays = (LinearLayout) view.findViewById(R.id.weekday);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox_repeat);
        if (entry.getRepeated() == 1) {
            weekdays.setVisibility(View.VISIBLE);
            checkBox.setChecked(true);
        } else {
            weekdays.setVisibility(View.INVISIBLE);
            checkBox.setChecked(false);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {
                    entry.setRepeat(1);
                    weekdays.setVisibility(View.VISIBLE);
                } else {
                    entry.setRepeat(0);
                    weekdays.setVisibility(View.INVISIBLE);
                }
            }
        });

        CheckBox vibrate = (CheckBox) view.findViewById(R.id.checkbox_vibrate);
        if (entry.getVibrate() == 1) {
            vibrate.setChecked(true);
        } else {
            vibrate.setChecked(false);
        }

        vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {
                    entry.setVibrate(1);
                } else {
                    entry.setVibrate(0);
                }
            }
        });

        if(entry.getSetting().equals("weather")) {



//        if(setting.equals("weather")) {

            weather.setPaintFlags(weather.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            weather.setTextColor(Color.parseColor("#ffffff"));
            day.setPaintFlags( day.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
            day.setTextColor(Color.parseColor("#24202E"));
        } else{
            day.setPaintFlags(weather.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            day.setTextColor(Color.parseColor("#ffffff"));

            weather.setPaintFlags( day.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
            weather.setTextColor(Color.parseColor("#24202E"));

        }

        TextView timeDisplay  = view.findViewById(R.id.labelExpanded);

        int hour = entry.getHour();
        String ampm;
        if (hour < 12){
            ampm = "AM";
        } else{
            ampm = "PM";
        }

        int minute = entry.getMinute();
        Log.d("alarmdetails display", "minute is " + minute);

        String min = "";
        if ((entry.getMinute() < 10)){
            min = "0"+Integer.toString(minute);
            Log.d("alarm details", "min less than ten " + min);
        } else{
            min = Integer.toString(minute);
            Log.d("alarm details", "min greater than ten " + min);
        }

        Log.d("alarm details", "before setting text min is " + min);
        String t = entry.getHour() + ":" + min;
        Log.d("alarm details", "before string t is " + t);
        timeDisplay.setText(t);
        final String time = timeDisplay.getText().toString();
        Log.d("alarm details", "after string time is " + time);


        timeDisplay.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Fragment fragment = new SetAlarmActivityRedesign().newInstance(entry.getId(), time);


                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("details").commit();

            }
        });

        TextView amPm = view.findViewById(R.id.ampm);
        amPm.setText(ampm);

//        ImageView weather = view.findViewById(R.id.weatherPlaylist);
//        ImageView day = view.findViewById(R.id.dayPlaylist);


//        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
//        fab.setVisibility(view.INVISIBLE);

        weather.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){

                Fragment fragment = new WeatherDisplay();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("details").commit();

                return true;

            }
        });

        weather.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                Button weather = v.findViewById(R.id.weatherPlaylist);
                weather.setPaintFlags(weather.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                weather.setTextColor(Color.parseColor("#ffffff"));
                setting="weather";
                entry.setSetting("weather");



                Button day = v.findViewById(R.id.dayPlaylist);
                day.setPaintFlags( day.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                day.setTextColor(Color.parseColor("#24202E"));

                Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                intent.putExtra("setting", setting);
//                entry.cancelSchedule(getActivity());
//                entry.setSchedule(getActivity());


            }
        });

        day.setOnLongClickListener( new View.OnLongClickListener(){
            public boolean onLongClick(View v){



                Fragment fragment = new DayDisplay();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("details").commit();

                return true;
            }
        });

        day.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                Button day = v.findViewById(R.id.dayPlaylist);
                day.setPaintFlags(day.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                day.setTextColor(Color.parseColor("#ffffff"));
                setting="day";
                entry.setSetting("day");



                Button weather = v.findViewById(R.id.weatherPlaylist);
                weather.setPaintFlags( day.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                weather.setTextColor(Color.parseColor("#24202E"));

                Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                intent.putExtra("setting", setting);

//                entry.cancelSchedule(getActivity());
//                entry.setSchedule(getActivity());

            }
        });


        Button buttonSun = (Button) view.findViewById(R.id.buttonSun);
        if(daysOfWeek[0]){
            buttonSun.setPaintFlags(buttonSun.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            buttonSun.setTextColor(Color.parseColor("#ffffff"));
        }
        buttonSun.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Button buttonSun = v.findViewById(R.id.buttonSun);
                
                int color = buttonSun.getCurrentTextColor();
                if(color != Color.parseColor("#ffffff")){
                    buttonSun.setPaintFlags(buttonSun.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                    buttonSun.setTextColor(Color.parseColor("#ffffff"));
                    daysOfWeek[0] = true;
                    entry.setDaysofweek(daysOfWeek);
                } else{
                    buttonSun.setPaintFlags( buttonSun.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                    buttonSun.setTextColor(Color.parseColor("#24202E"));
                    daysOfWeek[0] = false;
                    entry.setDaysofweek(daysOfWeek);
                }


            }
        });
        
        //buttonSun.setChecked(daysList[0]);
        Button buttonM =  view.findViewById(R.id.buttonM);
        if(daysOfWeek[1]){
            buttonM.setPaintFlags(buttonSun.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            buttonM.setTextColor(Color.parseColor("#ffffff"));
        }
        buttonM.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Button buttonM = v.findViewById(R.id.buttonM);

                int color = buttonM.getCurrentTextColor();
                if(color != Color.parseColor("#ffffff")){
                    buttonM.setPaintFlags(buttonM.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                    buttonM.setTextColor(Color.parseColor("#ffffff"));
                    daysOfWeek[1] = true;
                    entry.setDaysofweek(daysOfWeek);
                } else{
                    buttonM.setPaintFlags( buttonM.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                    buttonM.setTextColor(Color.parseColor("#24202E"));
                    daysOfWeek[1] = false;
                    entry.setDaysofweek(daysOfWeek);
                }


            }
        });
        //buttonM.setChecked(daysList[1]);
        Button buttonTue = view.findViewById(R.id.buttonTue);
        if(daysOfWeek[2]){
            buttonTue.setPaintFlags(buttonSun.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            buttonTue.setTextColor(Color.parseColor("#ffffff"));
        }
        buttonTue.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Button buttonTue = v.findViewById(R.id.buttonTue);

                int color = buttonTue.getCurrentTextColor();
                if(color != Color.parseColor("#ffffff")){
                    buttonTue.setPaintFlags(buttonTue.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                    buttonTue.setTextColor(Color.parseColor("#ffffff"));
                    daysOfWeek[2] = true;
                    entry.setDaysofweek(daysOfWeek);
                } else{
                    buttonTue.setPaintFlags( buttonTue.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                    buttonTue.setTextColor(Color.parseColor("#24202E"));
                    daysOfWeek[2]  = false;
                    entry.setDaysofweek(daysOfWeek);
                }


            }
        });


        //buttonTue.setChecked(daysList[2]);
        Button buttonW =  view.findViewById(R.id.buttonW);
        if(daysOfWeek[3]){
            buttonW.setPaintFlags(buttonSun.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            buttonW.setTextColor(Color.parseColor("#ffffff"));
        }
        buttonW.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Button buttonWed = v.findViewById(R.id.buttonW);

                int color = buttonWed.getCurrentTextColor();
                if(color != Color.parseColor("#ffffff")){
                    buttonWed.setPaintFlags(buttonWed.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                    buttonWed.setTextColor(Color.parseColor("#ffffff"));
                    daysOfWeek[3] = true;
                    entry.setDaysofweek(daysOfWeek);
                } else{
                    buttonWed.setPaintFlags( buttonWed.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                    buttonWed.setTextColor(Color.parseColor("#24202E"));
                    daysOfWeek[3] = false;
                    entry.setDaysofweek(daysOfWeek);
                }


            }
        });
        //buttonW.setChecked(daysList[3]);
        Button buttonThur =view.findViewById(R.id.buttonThur);
        if(daysOfWeek[4]){
            buttonThur.setPaintFlags(buttonSun.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            buttonThur.setTextColor(Color.parseColor("#ffffff"));
        }
        buttonThur.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Button buttonThur = v.findViewById(R.id.buttonThur);

                int color = buttonThur.getCurrentTextColor();
                if(color != Color.parseColor("#ffffff")){
                    buttonThur.setPaintFlags(buttonThur.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                    buttonThur.setTextColor(Color.parseColor("#ffffff"));
                    daysOfWeek[4] = true;
                    entry.setDaysofweek(daysOfWeek);
                } else{
                    buttonThur.setPaintFlags( buttonThur.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                    buttonThur.setTextColor(Color.parseColor("#24202E"));
                    daysOfWeek[4] = false;
                    entry.setDaysofweek(daysOfWeek);
                }


            }
        });
        //buttonThur.setChecked(daysList[4]);
        Button buttonF = view.findViewById(R.id.buttonF);
        if(daysOfWeek[5]){
            buttonF.setPaintFlags(buttonSun.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            buttonF.setTextColor(Color.parseColor("#ffffff"));
        }
        buttonF.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Button buttonF = v.findViewById(R.id.buttonF);

                int color = buttonF.getCurrentTextColor();
                if(color != Color.parseColor("#ffffff")){
                    buttonF.setPaintFlags(buttonF.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                    buttonF.setTextColor(Color.parseColor("#ffffff"));
                    daysOfWeek[5] = true;
                    entry.setDaysofweek(daysOfWeek);
                } else{
                    buttonF.setPaintFlags( buttonF.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                    buttonF.setTextColor(Color.parseColor("#24202E"));
                    daysOfWeek[5] = false;
                    entry.setDaysofweek(daysOfWeek);
                }


            }
        });
        //buttonF.setChecked(daysList[5]);
        Button buttonSat = view.findViewById(R.id.buttonSat);
        if(daysOfWeek[6]){
            buttonSat.setPaintFlags(buttonSun.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            buttonSat.setTextColor(Color.parseColor("#ffffff"));
        }
        buttonSat.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Button buttonSat = v.findViewById(R.id.buttonSat);

                int color = buttonSat.getCurrentTextColor();
                if(color != Color.parseColor("#ffffff")){
                    buttonSat.setPaintFlags(buttonSat.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                    buttonSat.setTextColor(Color.parseColor("#ffffff"));
                    daysOfWeek[6] = true;
                    entry.setDaysofweek(daysOfWeek);
                } else{
                    buttonSat.setPaintFlags( buttonSat.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                    buttonSat.setTextColor(Color.parseColor("#24202E"));
                    daysOfWeek[6] = false;
                    entry.setDaysofweek(daysOfWeek);
                }


            }
        });

        Button save = view.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(isNew){
                    new writeSchema().execute();
                } else{
                    new updateSchema().execute();
                }
                Fragment fragment = new AlarmsFragment();

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();


            }
        });
        //buttonSat.setChecked(daysList[6]);

        Button cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                getFragmentManager().popBackStack();
            }
        });


    }



    private class writeSchema extends AsyncTask<Void, Void, Void> {

        // ui calling possible
        protected void onPreExecute() {

        }

        // run threads
        @Override
        protected Void doInBackground(Void... arg0) {
            entry.setId((MainActivity.dataStorage.insertAlarmEntry(entry).getId()));
            Log.d("writeSchema", "do in background");

            entry.setSchedule(context);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }

    }

    private class updateSchema extends AsyncTask<Void, Void, Void> {

        // ui calling possible
        protected void onPreExecute() {

        }

        // run threads
        @Override
        protected Void doInBackground(Void... arg0) {

            MainActivity.dataStorage.updateAlarmEntry(entry);

            entry.setSchedule(context);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }

    }





}