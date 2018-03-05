package edu.dartmouth.cs.moodyalarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vivianjiang on 2/25/18.
 */


public class SetAlarmActivityRedesign extends Fragment{

    public View view;
    public String[] DEFAULT_PLAYLISTS;
    private final int NUMBER_DEFAULT_PLAYLISTS = 9;
    private GridView gridview;
    RecyclerView horizontal_recycler_view;
    LinearLayoutManager horizontalLayoutManager;

    public ArrayList<SpotifyPlaylist> spotifyEntries;

    public String newUrl;
    public String setting;
    public TextView alarm;



    Button btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.set_alarm_redesign, container, false);

        LinearLayout screenContainer = view.findViewById(R.id.linear_layout);
        final LinearLayout alarmContainer = view.findViewById(R.id.alarm_container);
        alarm = view.findViewById(R.id.alarm);

        alarm.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                new writeSchema().execute();
//                Toast.makeText(getActivity(), "Alarm saved!",
//                        Toast.LENGTH_LONG).show();
                return true;
            }
        });

        screenContainer.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e){

                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("screenContainer", "action down");
                        return true;

                    case MotionEvent.ACTION_UP:

                        long eventDuration = e.getEventTime() - e.getDownTime();
                        Log.d("screenContainer", "action up duration is " + eventDuration);

                        if (eventDuration < 100) {
                            if (e.getRawY() < alarmContainer.getY()) {
                                String[] arr = alarm.getText().toString().split(":");

                                int hour = Integer.parseInt(arr[0]);
                                int minute = Integer.parseInt(arr[1]);
                                minute--;
                                if (minute < 0) {
                                    minute = 60 + minute;
                                    hour--;
                                }
                                if (minute < 10) {
                                    alarm.setText(hour + ":" + "0" + minute);
                                } else {
                                    alarm.setText(hour + ":" + minute);
                                }
                            } else if (e.getRawY() > alarmContainer.getY()) {
                                String[] arr = alarm.getText().toString().split(":");

                                int hour = Integer.parseInt(arr[0]);
                                int minute = Integer.parseInt(arr[1]);
                                minute++;
                                if (minute == 60) {
                                    minute = 60 - minute;
                                    hour++;
                                }
                                if (minute < 10) {
                                    alarm.setText(hour + ":" + "0" + minute);
                                } else {
                                    alarm.setText(hour + ":" + minute);
                                }
                            }
                        }else {
                            if (e.getRawY() < alarmContainer.getY()) {
                                String[] arr = alarm.getText().toString().split(":");

                                int hour = Integer.parseInt(arr[0]);
                                int minute = Integer.parseInt(arr[1]);
                                minute = minute-5;
                                if (minute < 0) {
                                    minute = 60 + minute;
                                    hour--;
                                }
                                if (minute < 10) {
                                    alarm.setText(hour + ":" + "0" + minute);
                                } else {
                                    alarm.setText(hour + ":" + minute);
                                }
                            } else if (e.getRawY() > alarmContainer.getY()) {
                                String[] arr = alarm.getText().toString().split(":");

                                int hour = Integer.parseInt(arr[0]);
                                int minute = Integer.parseInt(arr[1]);
                                minute = minute + 5;
                                if (minute >= 60) {
                                    minute = 60 - minute;
                                    hour++;
                                }
                                if (minute < 10) {
                                    alarm.setText(hour + ":" + "0" + minute);
                                } else {
                                    alarm.setText(hour + ":" + minute);
                                }
                            }
                        }
                }


                return true;
            }

        });

        alarmContainer.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;

            // Defines the one method for the interface, which is called when the View is long-clicked
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        dY = v.getY() - e.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:

                        v.animate()
                                .y(e.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        float y = e.getRawY();
                        Log.d("ACTION MOVE", "y is "+ y);
                        if(y<=235 && y > 200) {
                            alarm.setText("12:00");
                        }

                        if(y<=270 && y > 235) {
                            alarm.setText("12:30");
                        }

                        if(y<=305 && y > 270){
                            alarm.setText("1:00");
                        }
                        if(y<=340 && y > 305) {
                            alarm.setText("1:30");
                        }

                        if(y<=375 && y > 340){
                            alarm.setText("2:00");
                        }

                        if(y<=410 && y > 375) {
                            alarm.setText("2:30");
                        }

                        if(y<=445 && y > 410){
                            alarm.setText("3:00");
                        }
                        if(y<=480 && y > 445) {
                            alarm.setText("3:30");
                        }

                        if(y<=53 && y > 480){
                            alarm.setText("4:00");
                        }
                        if(y<=580 && y > 530) {
                            alarm.setText("4:30");
                        }


                        if(y<=630 && y > 580){
                            alarm.setText("5:00");
                        }
                        if(y<=680 && y > 630) {
                            alarm.setText("5:30");
                        }

                        if(y<=730 && y > 680){
                            alarm.setText("6:00");
                        }
                        if(y<=780 && y > 730){
                            alarm.setText("6:30");
                        }
                        if(y<=830 && y > 780){
                            alarm.setText("7:00");
                        }
                        if(y<=890 && y > 830){
                            alarm.setText("7:30");
                        }
                        if(y<=950 && y > 890){
                            alarm.setText("8:00");
                        }
                        if(y<=1010 && y > 950){
                            alarm.setText("8:30");
                        }
                        if(y<=1070 && y > 1010){
                            alarm.setText("9:00");
                        }
                        if(y<=1130 && y > 1070){
                            alarm.setText("9:30");
                        }
                        if(y<=1170 && y >1130 ){
                            alarm.setText("10:00");
                        }
                        if(y<=1210 && y >1170 ){
                            alarm.setText("10:30");
                        }
                        if(y<=1250 && y > 1210){
                            alarm.setText("11:00");
                        }
                        if(y<=1290 && y > 1250){
                            alarm.setText("11:30");
                        }
                        if(y<=1330 && y > 1290){
                            alarm.setText("12:00");
                        }
                        if(y<=1370 && y > 1330){
                            alarm.setText("12:30");
                        }
                        if(y<=1410 && y > 1370){
                            alarm.setText("13:00");
                        }
                        if(y<=1450 && y > 1410){
                            alarm.setText("13:30");
                        }
                        if(y<=1490 && y > 1450){
                            alarm.setText("14:00");
                        }
                        if(y<=1530 && y > 1490){
                            alarm.setText("14:30");
                        }
                        if(y<=1570 && y > 1530){
                            alarm.setText("15:00");
                        }
                        if(y<=1610 && y > 1570){
                            alarm.setText("15:30");
                        }
                        if(y<=1650 && y > 1610){
                            alarm.setText("16:00");
                        }
                        if(y<=1690 && y > 1650){
                            alarm.setText("16:30");
                        }
                        if(y<=1730 && y > 1690){
                            alarm.setText("17:00");
                        }
                        if(y<=1770 && y > 1730){
                            alarm.setText("17:30");
                        }
                        if(y<=1810 && y > 1770){
                            alarm.setText("18:00");
                        }
                        if(y<=1850 && y > 1810){
                            alarm.setText("18:30");
                        }
                        if(y<=1890 && y > 1850){
                            alarm.setText("19:00");
                        }
                        if(y<=1930 && y > 1890){
                            alarm.setText("19:30");
                        }
                        if(y<=1970 && y > 1930){
                            alarm.setText("20:00");
                        }
                        if(y<=2050 && y > 2010){
                            alarm.setText("20:30");
                        }
                        if(y<=2090 && y > 2050){
                            alarm.setText("21:00");
                        }
                        if(y<=2130 && y > 2090){
                            alarm.setText("21:30");
                        }
                        if(y<=2170 && y > 2130){
                            alarm.setText("22:00");
                        }
                        if(y<=2210 && y > 2170){
                            alarm.setText("22:30");
                        }
                        if(y<=2250 && y > 2210){
                            alarm.setText("23:00");
                        }
                        if(y<=2290 && y > 2250){
                            alarm.setText("23:30");
                        }
                        if(y<=2310 && y > 2290){
                            alarm.setText("24:00");
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

        //listView.setOnItemClickListener(updateAlarm);

        return view;

    }


    @Override
    public void onResume(){
        super.onResume();
        Log.d("spotify settings", "on resume");


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Spotify Settings");
    }


    private class writeSchema extends AsyncTask<Void, Void, Void> {

        // ui calling possible
        protected void onPreExecute() {

        }

        // run threads
        @Override
        protected Void doInBackground(Void... arg0) {
            AlarmEntry entry = new AlarmEntry();
            String [] arr = alarm.getText().toString().split(":");

            int hour = Integer.parseInt(arr[0]);
            int minute = Integer.parseInt(arr[1]);
            entry.setHour(hour);
            entry.setMinute(minute);
            entry.setOnOff(1);
            entry.setRepeat(0);
            entry.setVibrate(1);
            entry.setSetting("weather");

            entry.setId((MainActivity.dataStorage.insertAlarmEntry(entry).getId()));
            Log.d("writeSchema", "do in background");

            entry.setSchedule(getActivity().getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }

    }


}