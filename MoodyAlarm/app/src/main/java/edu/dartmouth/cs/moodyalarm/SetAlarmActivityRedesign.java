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
import android.support.v4.app.FragmentTransaction;
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

    public String setting;
    public TextView alarm;
    public int timesIndex = 0;
    public long id;
    public AlarmEntry entry;

    public static SetAlarmActivityRedesign newInstance(Long id, AlarmEntry entry) {
        Bundle args = new Bundle();
        args.putLong("id", id);
        args.putSerializable("alarm", entry);

        SetAlarmActivityRedesign fragment = new SetAlarmActivityRedesign();
        fragment.setArguments(args);

        return fragment;
    }




    Button btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.set_alarm_redesign, container, false);
        MainActivity.fab.setVisibility(View.INVISIBLE);

        Bundle args = getArguments();
        if (args != null) {
            id = args.getLong("id");
            entry = (AlarmEntry) args.getSerializable("alarm");
        } else{
            id = -1;
        }
        LinearLayout screenContainer = view.findViewById(R.id.linear_layout);
        final LinearLayout alarmContainer = view.findViewById(R.id.alarm_container);
        alarm = view.findViewById(R.id.alarm);
        final String [] times = {"00:00","00:30", "1:00", "1:30" ,"2:00", "2:30", "3:00", "3:30","4:00","4:30",
                "5:00", "5:30", "6:00", "6:30", "7:00","7:30", "8:00","8:30","9:00","9:30","10:00","10:30","11:00","11:30",
                "12:00","12:30","13:00","13:30","14:00","14:30","15:00","15:30","16:00",
                "16:30","17:00","17:30","18:00","18:30","19:00","19:30","20:00","20:30","21:00","21:30","22:00", "22:30","23:00","23:30","24:00"};


        alarm.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if(!alarm.getText().equals("Scroll to set time")) {
                    AlarmEntry entry = new AlarmEntry();
                    String[] arr = alarm.getText().toString().split(":");

                    int hour = Integer.parseInt(arr[0]);

                    String [] minArr = arr[1].split(" ");

                    int minute = Integer.parseInt(minArr[0]);
                    entry.setHour(hour);
                    entry.setMinute(minute);
                    String time = hour + ":" + minute;
                    entry.setOnOff(1);
                    entry.setRepeat(0);
                    entry.setSetting("weather");
                    entry.setVibrate(1);
                    //entry.setId(id);
                    AlarmDetailsDisplay alarmDetails;
                    if(id == -1) {
                        alarmDetails = new AlarmDetailsDisplay().newInstance(time, entry, true);
                    } else{
                        entry.setId(id);
                        alarmDetails = new AlarmDetailsDisplay().newInstance(time, entry, false);
                    }

                    FragmentTransaction ft = getActivity().getSupportFragmentManager()
                            .beginTransaction();


                    ft.replace(R.id.content_frame, alarmDetails)
                            .commit();
                }
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
                        Log.d("screenContainer", "action up duration is " + eventDuration + "and e raw y is " + e.getRawY() +
                        "and alarm container y is " + alarmContainer.getY());

                        if (eventDuration < 100) {
                            if (e.getRawY() < alarmContainer.getY()+250) {
                                String[] arr = alarm.getText().toString().split(":");

                                int hour = Integer.parseInt(arr[0]);

                                String [] minArr = arr[1].split(" ");

                                int minute = Integer.parseInt(minArr[0]);
                                minute--;
                                if (minute < 0) {
                                    minute = 60 + minute;
                                    hour--;
                                }
                                alarm.setText(formatTime(hour, minute));
                            } else if (e.getRawY() > alarmContainer.getY()) {
                                String[] arr = alarm.getText().toString().split(":");

                                int hour = Integer.parseInt(arr[0]);

                                String [] minArr = arr[1].split(" ");

                                int minute = Integer.parseInt(minArr[0]);
                                minute++;
                                if (minute == 60) {
                                    minute = 60 - minute;
                                    hour++;
                                }
                                alarm.setText(formatTime(hour, minute));
                            }
                        }else {
                            if (e.getRawY() < alarmContainer.getY()+250) {
                                String[] arr = alarm.getText().toString().split(":");

                                int hour = Integer.parseInt(arr[0]);

                                String [] minArr = arr[1].split(" ");

                                int minute = Integer.parseInt(minArr[0]);
                                minute = minute-5;
                                if (minute < 0) {
                                    minute = 60 + minute;
                                    hour--;
                                }
                                alarm.setText(formatTime(hour, minute));

                            } else if (e.getRawY() > alarmContainer.getY()) {
                                String[] arr = alarm.getText().toString().split(":");

                                int hour = Integer.parseInt(arr[0]);

                                String [] minArr = arr[1].split(" ");

                                int minute = Integer.parseInt(minArr[0]);
                                minute = minute + 5;
                                if (minute >= 60) {
                                    minute = 60 - minute;
                                    hour++;
                                }
                                alarm.setText(formatTime(hour, minute));
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
                        if (id == -1) {
                            alarm.setText("12:00 PM");
                        } else {
                            int hour = entry.getHour();
                            int minute = entry.getMinute();
                            alarm.setText(formatTime(hour, minute));
                        }
                        dY = v.getY() - e.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if((e.getRawY()) <2300 && (e.getRawY()) >= 300) {

                            v.animate()
                                    .y(e.getRawY() + dY)
                                    .setDuration(0)
                                    .start();
                            float y = e.getRawY();
                            for (int i = 0; i < times.length; i++) {
                                if (y >= (40 * i + 300) && y < (i * 40 + 400)) {
                                    String[] arr = times[i].split(":");

                                    int hour = Integer.parseInt(arr[0]);
                                    if (hour < 12)
                                        alarm.setText(times[i] + " AM");
                                    else
                                        alarm.setText(times[i] + " PM");
                                    timesIndex = i;
                                }
                            }
                            Log.d("ACTION MOVE", "y is " + y);

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


    }

    private String formatTime(int hour, int minute) {
        String time = "";
        if (minute < 10) {
            time = hour + ":" + "0" + minute;
        } else {
            time = hour + ":" + minute;
        }

        if (hour < 12) {
            return time + " AM";
        } else {
            return time + " PM";
        }
    }
}