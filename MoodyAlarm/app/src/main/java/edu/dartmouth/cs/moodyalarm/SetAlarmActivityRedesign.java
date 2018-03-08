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
import android.graphics.drawable.GradientDrawable;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
    public String time;
    public boolean isNew;

    public static SetAlarmActivityRedesign newInstance(Long id, AlarmEntry entry, String time) {
        Bundle args = new Bundle();
        args.putLong("id", id);
        args.putSerializable("alarm", entry);
        args.putString("time", time);

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

        final LinearLayout screenContainer = view.findViewById(R.id.linear_layout);

        alarm = view.findViewById(R.id.alarm);
        final String [] times = {"00:00","00:30", "1:00", "1:30" ,"2:00", "2:30", "3:00", "3:30","4:00","4:30",
                "5:00", "5:30", "6:00", "6:30", "7:00","7:30", "8:00","8:30","9:00","9:30","10:00","10:30","11:00","11:30",
                "12:00","12:30","13:00","13:30","14:00","14:30","15:00","15:30","16:00",
                "16:30","17:00","17:30","18:00","18:30","19:00","19:30","20:00","20:30","21:00","21:30","22:00", "22:30","23:00","23:30","24:00"};


        final ArrayList<ArrayList<String>> colors = new ArrayList<>();

        colors.add(new ArrayList<>(Arrays.asList("#141E30","#141E30")));//12
        colors.add(new ArrayList<>(Arrays.asList("#141E30","#141E30")));
        colors.add(new ArrayList<>(Arrays.asList("#141E30","#002d3d")));//1
        colors.add(new ArrayList<>(Arrays.asList("#141E30","#002d3d")));
        colors.add(new ArrayList<>(Arrays.asList("#141E30","#2d2f5c")));//2
        colors.add(new ArrayList<>(Arrays.asList("#141E30","#383952")));
        colors.add(new ArrayList<>(Arrays.asList("#141E30","#605675")));//3
        colors.add(new ArrayList<>(Arrays.asList("#1d1d2a","#8E7497")));//
        colors.add(new ArrayList<>(Arrays.asList("#1d1d2a","#a36681")));//4
        colors.add(new ArrayList<>(Arrays.asList("#1d1d2a","#a36681")));//
        colors.add(new ArrayList<>(Arrays.asList("#322352","#D76D77")));//5
        colors.add(new ArrayList<>(Arrays.asList("#322352","#e97c87")));//
        colors.add(new ArrayList<>(Arrays.asList("#322352","#e97c87")));//6
        colors.add(new ArrayList<>(Arrays.asList("#322352","#8386b9")));//
        colors.add(new ArrayList<>(Arrays.asList("#322352","#5e7eb5")));//7
        colors.add(new ArrayList<>(Arrays.asList("#322352","#5aa1ce")));//
        colors.add(new ArrayList<>(Arrays.asList("#2c2a6a","#5aa1ce")));//8
        colors.add(new ArrayList<>(Arrays.asList("#2c457d","#5aa1ce")));//
        colors.add(new ArrayList<>(Arrays.asList("#266192","#7ab9e1")));//9
        colors.add(new ArrayList<>(Arrays.asList("#2d74ae","#97c9e7")));//
        colors.add(new ArrayList<>(Arrays.asList("#4491cf","#a2c8e7")));//10
        colors.add(new ArrayList<>(Arrays.asList("#71c0da","#cae8f1")));//
        colors.add(new ArrayList<>(Arrays.asList("#71c0da","#EAECC6")));//11
        colors.add(new ArrayList<>(Arrays.asList("#71c0da","#EAECC6")));//
        colors.add(new ArrayList<>(Arrays.asList("#94d9f0","#EAECC6")));//12
        colors.add(new ArrayList<>(Arrays.asList("#94d9f0","#EAECC6")));//
        colors.add(new ArrayList<>(Arrays.asList("#94d9f0","#EAECC6")));//1
        colors.add(new ArrayList<>(Arrays.asList("#94d9f0","#EAECC6")));//
        colors.add(new ArrayList<>(Arrays.asList("#94d9f0","#EAECC6")));//2
        colors.add(new ArrayList<>(Arrays.asList("#94d9f0","#EAECC6")));//
        colors.add(new ArrayList<>(Arrays.asList("#94d9f0","#EAECC6")));//3
        colors.add(new ArrayList<>(Arrays.asList("#94d9f0","#EAECC6")));//
        colors.add(new ArrayList<>(Arrays.asList("#94d9f0","#EAECC6")));//4
        colors.add(new ArrayList<>(Arrays.asList("#94d9f0","#EAECC6")));//
        colors.add(new ArrayList<>(Arrays.asList("#94d9f0","#EAECC6")));//5
        colors.add(new ArrayList<>(Arrays.asList("#94d9f0","#EAECC6")));//
        colors.add(new ArrayList<>(Arrays.asList("#89bfd1","#EAECC6")));//6
        colors.add(new ArrayList<>(Arrays.asList("#bec0a5","#f5f5a8")));//6
        colors.add(new ArrayList<>(Arrays.asList("#fda085","#fedd67")));//7
        colors.add(new ArrayList<>(Arrays.asList("#DF8694","#FDA085")));//
        colors.add(new ArrayList<>(Arrays.asList("#B1759A","#FDA085")));//8
        colors.add(new ArrayList<>(Arrays.asList("#7D6891","#B1759A")));//8
        colors.add(new ArrayList<>(Arrays.asList("#4E5A79","#7D6891")));//9
        colors.add(new ArrayList<>(Arrays.asList("#4E5A79","#7D6891")));//9
        colors.add(new ArrayList<>(Arrays.asList("#4E5A79","#7D6891")));//10
        colors.add(new ArrayList<>(Arrays.asList("#141E30","#605675")));
        colors.add(new ArrayList<>(Arrays.asList("#141E30","#383952")));
        colors.add(new ArrayList<>(Arrays.asList("#141E30","#141E30")));//12
        colors.add(new ArrayList<>(Arrays.asList("#141E30","#141E30")));



        if (args != null) {
            id = args.getLong("id");
            time = args.getString("time");
            if (!time.equals("none")) {
                isNew = false;

                alarm.setText(time);
                Log.d("setalarm activity", "time arg passed in is " + time);
                String [] timeArr = time.split(":");
                int hour = Integer.parseInt(timeArr[0]);
                int min = Integer.parseInt(timeArr[1]);
                int i = hour * 2;
                if (min >= 30){
                    i +=1;

                }
                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[] {Color.parseColor(colors.get(i).get(0)),Color.parseColor(colors.get(i).get(1))});
                gd.setCornerRadius(0f);
                screenContainer.setBackground(gd);
                float y = (float)((float)i/48.0) * 2300 - 15 * i;
                Log.d("setalarm activity", "before alarm y is " + alarm.getY()+ "and now setting it to "  + y);
                alarm.setY(y);
            }

        } else{
            id = -1;
            isNew = true;
            Log.d("setalarm activity", "new alarm");
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[] {Color.parseColor(colors.get(12).get(0)),Color.parseColor(colors.get(12).get(1))});
            gd.setCornerRadius(0f);
            screenContainer.setBackground(gd);

            alarm.setText(times[12] + " AM");
            float y = (float)((float)12/48.0) * 2300 - 15 * 12;

            alarm.setY(y);

        }


        screenContainer.setOnTouchListener(new View.OnTouchListener() {

            private float x1, x2;
            private float dY;
            public boolean onTouch(View v, MotionEvent e){

                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dY = v.getY() - e.getRawY();

                        x1 = e.getX();
                        Log.d("screenContainer", "action down");
                        int h = v.getHeight();

                        return true;

                    case MotionEvent.ACTION_MOVE:
                        x2 = e.getX();
                        float deltaX = Math.abs(x2-x1);
                        if(deltaX >300){
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

                            Calendar today = Calendar.getInstance();
                            SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy EEE");
                            entry.setDate(format.format(today.getTime()));

                            AlarmDetailsDisplay alarmDetails;
                            if(id == -1) {
                                alarmDetails = new AlarmDetailsDisplay().newInstance(time, entry, true);
                            } else{
                                entry.setId(id);
                                alarmDetails = new AlarmDetailsDisplay().newInstance(time, entry, false);
                            }

                            FragmentTransaction ft = getActivity().getSupportFragmentManager()
                                    .beginTransaction();


                            ft.replace(R.id.content_frame, alarmDetails).addToBackStack("main")
                                    .commit();
                            return false;

                        }
                        break;


                    case MotionEvent.ACTION_UP:

                        long eventDuration = e.getEventTime() - e.getDownTime();
                        Log.d("screenContainer", "action up duration is " + eventDuration + "and e raw y is " + e.getRawY() +
                        "and alarm container y is " + alarm.getY());

                        if (eventDuration < 100) {
                            if (e.getRawY() < alarm.getY()+250) {
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
                            } else if (e.getRawY() > alarm.getY()) {
                                String[] arr = alarm.getText().toString().split(":");

                                int hour = Integer.parseInt(arr[0]);
                                if (hour < 24) {

                                    String[] minArr = arr[1].split(" ");

                                    int minute = Integer.parseInt(minArr[0]);
                                    minute++;
                                    if (minute == 60) {
                                        minute = 60 - minute;
                                        hour++;
                                    }
                                    alarm.setText(formatTime(hour, minute));
                                }
                            }
                        }else {
                            if (e.getRawY() < alarm.getY()+250) {
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

                            } else if (e.getRawY() > alarm.getY()) {
                                String[] arr = alarm.getText().toString().split(":");

                                int hour = Integer.parseInt(arr[0]);
                                if (hour < 24) {

                                    String[] minArr = arr[1].split(" ");

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
                }
                return true;
            }

        });

        alarm.setOnTouchListener(new View.OnTouchListener() {
            float dX, dY;


            // Defines the one method for the interface, which is called when the View is long-clicked
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        dY = v.getY() - e.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if ((e.getRawY() + dY) < 1780 && (e.getRawY() + dY) >= 25) {

                            v.animate()
                                    .y(e.getRawY() + dY)
                                    .setDuration(0)
                                    .start();
                            float y = e.getRawY()+ dY;
                            int i = 0;
                            for (i = 0; i < times.length; i++) {
                                if (y >= (33 * i + 25) && y < (i * 33 + 58)) {
                                    String[] arr = times[i].split(":");

                                    GradientDrawable gd = new GradientDrawable(
                                            GradientDrawable.Orientation.TOP_BOTTOM,
                                            new int[] {Color.parseColor(colors.get(i).get(0)),Color.parseColor(colors.get(i).get(1))});
                                    gd.setCornerRadius(0f);
                                    screenContainer.setBackground(gd);

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