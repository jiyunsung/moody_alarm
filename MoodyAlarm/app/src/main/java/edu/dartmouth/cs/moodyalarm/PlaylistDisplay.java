package edu.dartmouth.cs.moodyalarm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
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


public class PlaylistDisplay extends AppCompatActivity{

    public View view;
    public String[] DEFAULT_PLAYLISTS;
    private final int NUMBER_DEFAULT_PLAYLISTS = 9;
    private GridView gridview;
    private TextView loading;

    private SpotifyPlaylist entry;
    private Day day;
    private int position;
    private ArrayList<SpotifySong> songs;
    private ListView listView;
    private EntryDbHelper database;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_display);
        position = getIntent().getIntExtra("pos", 0);
        database = new EntryDbHelper(getApplicationContext());
        database.open();
        entry = database.fetchEntryByIndexSpotify(Long.valueOf(position));
        day = database.fetchEntryByIndexDay(DayDisplay.dayId);
        listView = findViewById(R.id.songs_list);
        songs = new ArrayList<SpotifySong>();

        Log.d("playlistdisplay", "fetched entry with playlist id "+ entry.getPlaylistId());
        fetchPlaylistTracks(entry.getPlaylistId());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    // delete button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.select:
                day.setSpotifyPlaylist(entry);
                database.updateDayEntry(day);
                //DayDisplay.adapter.notifyDataSetChanged();
                finish();

                return true;
            case R.id.delete:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onItemClick(AdapterView<?> parent, View v,
                            int position, long id) {





    }

    public void fetchPlaylistTracks(String id){
        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        String url = "https://api.spotify.com/v1/users/spotify/playlists/"+ id + "/tracks";
        final ArrayList<String> imageUrls = new ArrayList<String>();
        Log.d("Spotify service", "in fetch playlist tracks access token is "+ MainActivity.accessToken);

            StringRequest jsObjRequest = new StringRequest
                    (Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Response", response);
                            entry.setTrackInfo(response);
                            new SpotifyAsyncTask().execute();

                        }
                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO Auto-generated method stub
                                    Log.d("ERROR", "error => " + error.toString());
                                }
                            }
                    ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer " + MainActivity.accessToken);
                    params.put("Accept", "application/json");

                    return params;
                }
            };
            queue.add(jsObjRequest);
        }



//
    //custom adapter class for list view
    public class CustomAdapter extends ArrayAdapter<SpotifySong> {


        Context mContext;
        ArrayList<SpotifySong> spotifySongs;
    
    
        public CustomAdapter(Context context, ArrayList<SpotifySong> songs) {
            super(context, R.layout.songs_list, songs);
            this.spotifySongs = songs;
            this.mContext=context;
            Log.d("customAdapter", "songs length is " + this.spotifySongs.size());
    
        }



    @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            SpotifySong e = getItem(position);
            Log.d("in getView", "getting e with name " + e.getSongName());
            // Check if an existing view is being reused, otherwise inflate the view


            final View result;
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.songs_list, parent, false);


            TextView songName = (TextView) convertView.findViewById(R.id.song_name);
            TextView songArtist = (TextView) convertView.findViewById(R.id.song_artist);
            ImageView albumPhoto = convertView.findViewById(R.id.album_photo);
            new DownloadImageTask(albumPhoto)
                .execute(e.getImageUrl());


            songName.setText(e.getSongName());
            songArtist.setText(e.getSongArtist());

            // Return the completed view to render on screen
            return convertView;
        }
    }

    private class SpotifyAsyncTask extends AsyncTask<Void, Void, Void> {

        // ui calling possible
        EntryDbHelper dataStorage;
        protected void onPreExecute() {

        }

        // run threads
        @Override
        protected Void doInBackground(Void... params) {
            dataStorage= new EntryDbHelper(getApplicationContext());
            dataStorage.open();
            dataStorage.updateSpotifyEntry(entry);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            try {

                JSONObject jsonObject = new JSONObject(entry.getTrackInfo());


                JSONArray arr = jsonObject.getJSONArray("items");
                Log.d("onPostExecute", "array length is " + arr.length());
                for (int i = 0; i < arr.length(); i++){
                    JSONObject item = arr.getJSONObject(i);
                    JSONObject track = item.getJSONObject("track");
                    JSONObject album = track.getJSONObject("album");
                    String songName = album.getString("name");
                    Log.d("song name is ", songName);

                    JSONArray artists = track.getJSONArray("artists");
                    JSONObject artist = artists.getJSONObject(0);
                    String artistName = artist.getString("name");
                    Log.d("artist name is ", artistName);

                    JSONArray images = album.getJSONArray("images");
                    JSONObject image = images.getJSONObject(1);
                    String imageUrl = image.getString("url");
                    Log.d("image url is ", imageUrl);
                    
                    SpotifySong song = new SpotifySong(songName, artistName,imageUrl);
                    songs.add(song);


                }
                CustomAdapter adapter= new CustomAdapter(getApplicationContext(), songs);

                adapter.addAll(songs);
                adapter.notifyDataSetChanged();

                Log.d("onPostExecute", "adapter count is " + adapter.getCount());
                listView.setAdapter(adapter);
            } catch(JSONException e){Log.d("error", e.toString());}



            
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    SpotifySong song = songs.get(position);

                    //Log.d("History onItemClick", "entry with id " + entry.getId() + "and input " + entry.getInputType());

                    

                }
            });
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }






}