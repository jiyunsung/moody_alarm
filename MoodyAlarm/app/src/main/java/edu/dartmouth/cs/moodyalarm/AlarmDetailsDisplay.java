package edu.dartmouth.cs.moodyalarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.ImageView;
import android.widget.TextView;

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

    private static final String TIME = "12:00";


    public static AlarmDetailsDisplay newInstance(String time, AlarmEntry e) {
        Bundle args = new Bundle();
        args.putString(TIME, time);

        args.putSerializable("alarm", e);
        AlarmDetailsDisplay fragment = new AlarmDetailsDisplay();
        fragment.setArguments(args);

        return fragment;
    }


    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.expanded, container, false);



        super.onCreate(savedInstanceState);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();


        TextView timeDisplay  = view.findViewById(R.id.labelExpanded);

        timeDisplay.setText(time);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View v = view;



        Bundle args = getArguments();
        time = args.getString(TIME);
        entry = (AlarmEntry) args.getSerializable("alarm");

        Button weather = view.findViewById(R.id.weatherPlaylist);
        Button day = view.findViewById(R.id.dayPlaylist);

        if(entry.getSetting().equals("weather")) {


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

        timeDisplay.setText(time);

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
                MainActivity.dataStorage.updateAlarmEntry(entry);


                Button day = v.findViewById(R.id.dayPlaylist);
                day.setPaintFlags( day.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                day.setTextColor(Color.parseColor("#24202E"));

                Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                intent.putExtra("setting", setting);
//                entry.cancelSchedule(getActivity());
//                entry.setSchedule(getActivity());


            }
        });

        day.setOnLongClickListener(new View.OnLongClickListener(){
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
                MainActivity.dataStorage.updateAlarmEntry(entry);


                Button weather = v.findViewById(R.id.weatherPlaylist);
                weather.setPaintFlags( day.getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                weather.setTextColor(Color.parseColor("#24202E"));

                Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                intent.putExtra("setting", setting);

//                entry.cancelSchedule(getActivity());
//                entry.setSchedule(getActivity());

            }
        });


    }

    public void onClickWeather(View v){


    }


    public void onClickDay(View v){

    }


}