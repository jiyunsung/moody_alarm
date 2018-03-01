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

public class WeatherDisplay extends Fragment {

    public View view;
    Button btn;
    public static long weatherId;

    public static ArrayList<SpotifyPlaylist> playlists;
    private ListView listView;
    private ArrayList<Weather> weathers;
    public static CustomAdapter adapter;
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.weather_display, container, false);

        super.onCreate(savedInstanceState);
        listView = view.findViewById(R.id.weather_list);
        playlists = new ArrayList<SpotifyPlaylist>();

        new WeatherAsyncTask().execute();

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
        Log.d("weatherdisplay", "on resume");
        new WeatherAsyncTask().execute();
//        if (adapter != null) {
//            adapter.notifyDataSetChanged();
//        }

    }


    public void showPlaylistDialog(View v, Long id) {
        DialogFragment fragment = new SpotifySettings();


        fragment.show(getFragmentManager(), "playlistPicker");
    }



    public class CustomAdapter extends ArrayAdapter<Weather> {


        Context mContext;
        ArrayList<Weather> mWeathers;


        public CustomAdapter(Context context, ArrayList<Weather> weathers) {
            super(context, R.layout.weekday, weathers);
            this.mWeathers = weathers;
            this.mContext=context;
            Log.d("customAdapter", "weathers length is " + this.mWeathers.size());

        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            final Weather w = getItem(position);
            //Log.d("in getView", "getting d with name " + d.getName());
            // Check if an existing view is being reused, otherwise inflate the view

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.weather, parent, false);

            TextView dayName = (TextView) convertView.findViewById(R.id.weather);
            //Button select = convertView.findViewById(R.id.button_day);

            ImageView playlistImg = convertView.findViewById(R.id.playlist_img);
            if (w.getSpotifyPlaylist() != null) {

                SpotifyPlaylist playlist = w.getSpotifyPlaylist();

                Picasso.with(getActivity().getApplicationContext()).
                        load(playlist.getImageUrl()).into(playlistImg);


            } else {
                playlistImg.setImageResource(R.drawable.spotify_logo);
            }

            dayName.setText(w.getName());
//            if(w.getName().equals("Wednesday")){
//                dayName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f);
//            }
//            select.setOnClickListener(new View.OnClickListener(){
//                public void onClick(View v){
//                    showPlaylistDialog(v, d.getId());
//                    dayId = d.getId();
//                }
//            });

            // Return the completed view to render on screen
            return convertView;
        }


    }




    private class WeatherAsyncTask extends AsyncTask<Void, Void, Void>{

        // ui calling possible

        private EntryDbHelper database;
        boolean initiallyEmpty;
        protected void onPreExecute() {

        }

        // run threads
        @Override
        protected Void doInBackground(Void... params) {
            database = new EntryDbHelper(getActivity().getApplicationContext());
            database.open();


            weathers = database.fetchWeatherEntries();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {



            adapter= new CustomAdapter(getActivity().getApplicationContext(), weathers);

            //adapter.addAll(days);
            //adapter.notifyDataSetChanged();
            Log.d("weather async task", "on post execute weathers size is: " + weathers.size());

            Log.d("onPostExecute", "weather async task adapter count is " + adapter.getCount());
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Log.d("item click listener", "position is "+ position);
                    Weather w= weathers.get(position);
                    if(w == null){
                        Log.d("on item click listener", "w is null");
                    }else{
                        Log.d("on item click listener", "w name and id is" + w.getName() + ", " + w.getId());
                    }
                    weatherId = w.getId();
                    showPlaylistDialog(view, weatherId);


                }
            });




        }

    }


}





