package edu.dartmouth.cs.moodyalarm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vivianjiang on 2/25/18.
 */

public class DayDisplay extends Fragment {

    public View view;
    Button btn;
    public static long dayId;

    public static ArrayList<SpotifyPlaylist> playlists;
    private ListView listView;
    private ArrayList<Day> days;
    public static CustomAdapter adapter;
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.day_display, container, false);

        super.onCreate(savedInstanceState);
        listView = view.findViewById(R.id.days_list);
        playlists = new ArrayList<SpotifyPlaylist>();

        new DayAsyncTask().execute();

        return view;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Alarm Settings");
    }

    @Override
    public void onResume() {
        super.onResume();
        //you can set the title for your toolbar here for different fragments different titles
        Log.d("daydisplay", "on resume");
        new DayAsyncTask().execute();
//        if (adapter != null) {
//            adapter.notifyDataSetChanged();
//        }

    }


    public void showPlaylistDialog(View v, Long id) {
        DialogFragment fragment = new SpotifySettings();
        Long dayId = id;

        fragment.show(getFragmentManager(), "playlistPicker");
    }



    public class CustomAdapter extends ArrayAdapter<Day> {


        Context mContext;
        ArrayList<Day> mDays;


        public CustomAdapter(Context context, ArrayList<Day> days) {
            super(context, R.layout.weekday, days);
            this.mDays = days;
            this.mContext=context;
            Log.d("customAdapter", "days length is " + this.mDays.size());

        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            final Day d = getItem(position);
            //Log.d("in getView", "getting d with name " + d.getName());
            // Check if an existing view is being reused, otherwise inflate the view

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.weekday, parent, false);

            TextView dayName = (TextView) convertView.findViewById(R.id.day);
            //Button select = convertView.findViewById(R.id.button_day);

            ImageView playlistImg = convertView.findViewById(R.id.playlist_img);
            if (d.getSpotifyPlaylist() != null) {

                    SpotifyPlaylist playlist = d.getSpotifyPlaylist();

                Picasso.with(getActivity().getApplicationContext()).
                        load(playlist.getImageUrl()).into(playlistImg);

            } else {
                playlistImg.setImageResource(R.drawable.spotify_logo);
            }

            dayName.setText(d.getName());
            if(d.getName().equals("Wednesday")){
                dayName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f);
            }
            return convertView;
        }


    }




    private class DayAsyncTask extends AsyncTask<Void, Void, Void>{

        // ui calling possible


        boolean initiallyEmpty;
        protected void onPreExecute() {

        }

        // run threads
        @Override
        protected Void doInBackground(Void... params) {



            days = MainActivity.dataStorage.fetchDayEntries();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter= new CustomAdapter(getActivity().getApplicationContext(), days);

            //adapter.addAll(days);
            //adapter.notifyDataSetChanged();
            Log.d("day async task", "on post execute days size is: " + days.size());

            Log.d("onPostExecute", "day async task adapter count is " + adapter.getCount());
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Log.d("item click listener", "position is "+ position);
                    Day d= days.get(position);
                    if(d == null){
                        Log.d("on item click listener", "d is null");
                    }else{
                        Log.d("on item click listener", "d name and id is" + d.getName() + ", " + d.getId());
                    }
                    dayId = d.getId();
                    showPlaylistDialog(view, dayId);


                }
            });




        }

    }



}





