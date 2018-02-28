package edu.dartmouth.cs.moodyalarm;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;


import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;


import android.content.Intent;

import android.util.Log;



import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SpotifyPlayer.NotificationCallback, ConnectionStateCallback{

    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "d7732baf6fed4aa887a95397bcd83152";

    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "http://localhost:5000/api/v1/invalid";

    private final int NUMBER_DEFAULT_PLAYLISTS = 9;

    private Player mPlayer;

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;


    public static String accessToken = "";
    public Boolean finishedDataRetrieval= false;
    public EntryDbHelper dataStorage;
    public static ArrayList<String> mImageUrls;
    public String uri = "";






    // list view
    private String[] lv_arr = {};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Spotify Login code
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);


        // set toolbar instead of app bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_add_alarm_black_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent setAlarm = new Intent(getApplicationContext(), SetAlarmActivity.class);
                setAlarm.putExtra(AlarmsFragment.NEWALARM, true);
                startActivity(setAlarm);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



//        FragmentManager fragmentManager = this.getFragmentManager();
//        AlarmsFragment alarmFrag = new AlarmsFragment();
//
//        fragmentManager.beginTransaction().replace(R.id.content_frame, alarmFrag).commit();

        displaySelectedScreen(R.id.viewAlarms, true);




        //this.deleteDatabase(EntryDbHelper.DATABASE_NAME);






    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        displaySelectedScreen(item.getItemId(), false);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        // The next 19 lines of the code are what you need to copy & paste! :)
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Log.d("onActivityResult: ", "access token is: " + response.getAccessToken());
                accessToken = response.getAccessToken();
                Config playerConfig = new Config(this, accessToken, CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addNotificationCallback(MainActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
                new RetrieveDataAsyncTask().execute();
                //new SpotifyAsyncTask().execute();

//                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
//                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
//                    @Override
//                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
//                        mPlayer = spotifyPlayer;
//                        mPlayer.addConnectionStateCallback(MainActivity.this);
//                        mPlayer.addNotificationCallback(MainActivity.this);
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
//                    }
//                });
                //new RetrieveDataAsyncTask().execute();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");





        // This is the line that plays a song.
        if(finishedDataRetrieval) {
            mPlayer.playUri(null, uri, 0, 0);
        }

    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error var1) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }



    private void displaySelectedScreen(int itemId, boolean onCreate) {

        //creating fragment object
        Fragment fragment = null;
        Log.d("displaySelectedScreen", Integer.toString(itemId));
        //initializing the fragment object which is selected

        switch (itemId) {
            case R.id.viewAlarms:

                fragment = new AlarmsFragment();
                break;
            case R.id.editAlarm:
                fragment = new AlarmSettings();
                break;
//            case R.id.editSpotify:
//                Log.d("displaySelectedScreen", "results case");
//
//                //fragment = new SpotifySettings();
//                break;
            case R.id.editSnooze:
                fragment = new SnoozeSettings();
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }



    private class PlayPlaylistAsyncTask extends AsyncTask<Long, Void, Void> {
        EntryDbHelper dataStorage;
        // ui calling possible
        protected void onPreExecute() {
            Log.d("onPreExecute", "retreivedataasync task");
        }

        // run threads
        @Override
        protected Void doInBackground(Long... params) {
            Long dayId = params[0];

            dataStorage= new EntryDbHelper(getApplicationContext());
            dataStorage.open();
            Day day = dataStorage.fetchEntryByIndexDay(dayId);


            SpotifyPlaylist playlist = day.getSpotifyPlaylist();
            String spotifySongs = playlist.getTrackInfo();
            Log.d("PlayPlaylistAsyncTask", "do in background spotifytrack is: " + spotifySongs);





            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("onPostExecute", "playPlaylistAsyncTask");



        }
    }

    private class RetrieveDataAsyncTask extends AsyncTask<Void, Void, Void> {
        EntryDbHelper dataStorage;
        ArrayList<SpotifyPlaylist> playlists;
        // ui calling possible
        protected void onPreExecute() {
            Log.d("onPreExecute", "retreivedataasync task");
        }

        // run threads
        @Override
        protected Void doInBackground(Void... params) {

            dataStorage= new EntryDbHelper(getApplicationContext());
            dataStorage.open();

            playlists = dataStorage.fetchSpotifyEntries();


            if (playlists.size() < NUMBER_DEFAULT_PLAYLISTS){
                 finishedDataRetrieval = false;
            } else{
                finishedDataRetrieval = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("onPostExecute", "main activity playlists length is "+playlists.size());


            //dataStorage.close();
            if(!finishedDataRetrieval){
                Log.d("retrieve data asynctask", "did not all data");
                fetchDefaultPlaylists(getApplicationContext());
            } else {
                Log.d("retrieve data asynctask", "got all data");
                finishedDataRetrieval = true;
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);

                switch (day) {
                    case Calendar.MONDAY:
                        // Current day is Sunday

                    case Calendar.TUESDAY:
                        // Current day is Monday

                    case Calendar.WEDNESDAY:
                        Log.d("retrieve data asynctask", "day is wednesday");
                        Day today = dataStorage.fetchEntryByIndexDay(3);
                        SpotifyPlaylist todayPlaylist = today.getSpotifyPlaylist();
                        Log.d("playlist id is ", todayPlaylist.getPlaylistId());
                        String tracks = todayPlaylist.getTrackInfo();
                        try {

                            JSONObject jsonObject = new JSONObject(tracks);


                            JSONArray arr = jsonObject.getJSONArray("items");

                            JSONObject item = arr.getJSONObject(0);
                            JSONObject track = item.getJSONObject("track");
                            JSONObject album = track.getJSONObject("album");
                            String songName = album.getString("name");
                            Log.d("song name is ", songName);
                            uri = album.getString("uri");
                            Log.d("track uri is ", uri);

                        } catch(JSONException e){Log.d("error", e.toString());}

                        // etc.
                }



            }
        }
    }






    public void fetchDefaultPlaylists(Context input){

        RequestQueue queue = Volley.newRequestQueue(input);

        //String url = "https://api.spotify.com/v1/browse/categories/mood/playlists?limit=50";

        final String [] default_playlists = new String[9];

        default_playlists[0] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DWSiZVO2J6WeI";
        default_playlists[1] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DX5Q5wA1hY6bS";
        default_playlists[2] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DWUNIrSzKgQbP";
        default_playlists[3] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DX3YSRoSdA634";
        default_playlists[4] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DX6ALfRKlHn1t";
        default_playlists[5] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DXbvABJXBIyiY";
        default_playlists[6] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DWVV27DiNWxkR";
        default_playlists[7] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DWU0ScTcjJBdj";
        default_playlists[8] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DX3rxVfibe1L0";


        final ArrayList<String> imageUrls = new ArrayList<String>();
        final ArrayList<SpotifyPlaylist> playlists = new ArrayList<>();
        Log.d("days display", "in fetch default playlists access token is "+ MainActivity.accessToken);


        for (int i = 0; i< default_playlists.length;i++) {


            StringRequest jsObjRequest = new StringRequest
                    (Request.Method.GET, default_playlists[i], new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("fetch default playlists Response", response);
                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                String id = jsonObject.getString("id");

                                JSONArray arr = jsonObject.getJSONArray("images");
                                for (int i = 0; i < arr.length(); i++)
                                {
                                    String imageUrl= arr.getJSONObject(i).getString("url");
                                    Log.d("image url", imageUrl);
                                    SpotifyPlaylist entry = new SpotifyPlaylist();
                                    entry.setPlaylistId(id);
                                    entry.setImageUrl(imageUrl);

                                    playlists.add(entry);
                                    imageUrls.add(imageUrl.toString());
                                    Log.d("imageUrls size: ", "size is " + imageUrls.size());

                                    if(playlists.size() == default_playlists.length){
                                        Log.d("fetch default playlists", "imageURls size equals default");
                                        try {
                                            new SpotifyAsyncSave().execute(playlists);
                                        } catch(Exception e){
                                            Log.d("error", e.toString());
                                        }

                                    }

                                }

                            } catch (JSONException e) {
                                Log.d("json error", e.toString());
                            }


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



    }


    public void fetchPlaylistTracks(String id, final SpotifyPlaylist playlist, final Day day){
        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        String url = "https://api.spotify.com/v1/users/spotify/playlists/"+ id + "/tracks";
        final ArrayList<String> imageUrls = new ArrayList<String>();
        Log.d("Spotify service", "in fetch playlist tracks access token is "+ MainActivity.accessToken);

        StringRequest jsObjRequest = new StringRequest
                (Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("fetchPlaylisttracks Response", response);
                        playlist.setTrackInfo(response);
                        new SaveSongsAsyncTask(playlist, day).execute();

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



    private class SpotifyAsyncSave extends AsyncTask<ArrayList<SpotifyPlaylist>, Void, ArrayList<SpotifyPlaylist>> {

        String [] dayArr = {"Monday", "Tuesday","Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        EntryDbHelper dataStorage;
        // ui calling possible
        protected void onPreExecute() {

        }

        // run threads
        @Override
        protected ArrayList<SpotifyPlaylist> doInBackground(ArrayList<SpotifyPlaylist>... params) {
            ArrayList<SpotifyPlaylist> playlists = params[0];


            dataStorage= new EntryDbHelper(getApplicationContext());
            dataStorage.open();


            for (int i = 0; i < playlists.size(); i++){
                Log.d("Spotifyasyncsave", "do in background, playlist id is " + playlists.get(i).getPlaylistId());
                playlists.get(i).setId(dataStorage.insertSpotifyEntry(playlists.get(i)).getId());


            }


            return playlists;

        }

        @Override
        protected void onPostExecute(ArrayList<SpotifyPlaylist> result) {
            Day day = null;
            for (int i = 0; i < result.size(); i++){
                if(i < dayArr.length){
                    day = new Day();
                    day.setName(dayArr[i]);
                    day.setId(dataStorage.insertDayEntry(day).getId());
                } else{
                    day = null;
                }

                fetchPlaylistTracks(result.get(i).getPlaylistId(), result.get(i), day);
            }
        }

    }


    private class SaveSongsAsyncTask extends AsyncTask<Void, Void, Void> {

        // ui calling possible
        EntryDbHelper dataStorage;

        SpotifyPlaylist playlist;
        Day day;

        public SaveSongsAsyncTask(SpotifyPlaylist p,Day d){
            this.playlist = p;
            this.day = d;
        }

        protected void onPreExecute() {

        }

        // run threads
        @Override
        protected Void doInBackground(Void... params) {
            dataStorage= new EntryDbHelper(getApplicationContext());
            dataStorage.open();
            dataStorage.updateSpotifyEntry(this.playlist);

            if(this.day != null) {
                Log.d("savesongsasynctask", "playlist set for day: " + this.day.getName());
                this.day.setSpotifyPlaylist(this.playlist);
                dataStorage.updateDayEntry(this.day);
            } else{
                finishedDataRetrieval = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Log.d("savesongsasynctask", "onpostexecute ");

            if (finishedDataRetrieval){
                Log.d("savesong async task", "finisehd data retrieval, playing song");

                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);

                switch (day) {
                    case Calendar.MONDAY:
                        // Current day is Sunday

                    case Calendar.TUESDAY:
                        // Current day is Monday

                    case Calendar.WEDNESDAY:
                        Log.d("savesong asynctask", "day is wednesday");
                        Day today = dataStorage.fetchEntryByIndexDay(3);
                        SpotifyPlaylist todayPlaylist = today.getSpotifyPlaylist();
                        Log.d("playlist id is ", todayPlaylist.getPlaylistId());
                        String tracks = todayPlaylist.getTrackInfo();
                        try {

                            JSONObject jsonObject = new JSONObject(tracks);


                            JSONArray arr = jsonObject.getJSONArray("items");

                                JSONObject item = arr.getJSONObject(0);
                                JSONObject track = item.getJSONObject("track");
                                JSONObject album = track.getJSONObject("album");
                                String songName = album.getString("name");
                                Log.d("song name is ", songName);
                                uri = album.getString("uri");
                                Log.d("track uri is ", uri);

                        } catch(JSONException e){Log.d("error", e.toString());}

                        // etc.
                }

                mPlayer.playUri(null, uri, 0, 0);
            }

        }

    }

}
