package edu.dartmouth.cs.moodyalarm;


import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;


import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
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
import java.util.HashMap;
import java.util.Map;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SpotifyPlayer.NotificationCallback, ConnectionStateCallback{

    // TODO: Replace with your client ID
    private static final String CLIENT_ID = "d7732baf6fed4aa887a95397bcd83152";

    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "http://localhost:5000/api/v1/invalid";

    public static final String WEATHER_API_KEY = "APPID=d5233bd27811890e0b347bc47782312c";

    private final int NUMBER_DEFAULT_PLAYLISTS = 9;

    public static Player mPlayer;

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;


    public static String accessToken = "";
    public Boolean finishedDefaultDataRetrieval = false;
    public Boolean finishedUserDataRetrieval = false;
    public static EntryDbHelper dataStorage;

    public String uri = "";

    private static final String TAG = "vj";

    public static FloatingActionButton fab;

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataStorage = new EntryDbHelper(this);
        dataStorage.open();


        // Spotify Login code
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);


        // set toolbar instead of app bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setImageResource(R.drawable.ic_add_alarm_black_24dp);
        fab.setVisibility(VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent setAlarm = new Intent(getApplicationContext(), SetAlarmActivity.class);
//                setAlarm.putExtra(AlarmsFragment.NEWALARM, true);
//                startActivity(setAlarm);
                Fragment fragment = new SetAlarmActivityRedesign();


                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("main").commit();



            }
        });

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        displaySelectedScreen(R.id.alarms, true);


        //this.deleteDatabase(EntryDbHelper.DATABASE_NAME);

//        mIsBound = false; // by default set this to unbound
//        automaticBind();
        if (!checkPermission()){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        displaySelectedScreen(item.getItemId(), false);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        // The next 19 lines of the code are what you need to copy & paste! :)
        Log.d("onActivityResult", "in on activity result");
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

            }
        }
    }


    @Override
    protected void onResume(){
        super.onResume();
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
        Log.d(TAG, "C:onDestroy()");
//        try {
//            doUnbindService();
//            stopService(new Intent(MainActivity.this, LocationService.class));
//        } catch (Throwable t) {
//            Log.e(TAG, "Failed to unbind from the service", t);
//        }
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
            case R.id.alarms:

                fragment = new AlarmsFragment();
                fab.setVisibility(VISIBLE);
                break;

            case R.id.playlist_settings:
                fragment = new AlarmSettings();
                fab.setVisibility(View.INVISIBLE);

                break;
            case R.id.snooze_settings:
                fragment = new SnoozeSettings();
                fab.setVisibility(View.INVISIBLE);
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
    }



    private class RetrieveDataAsyncTask extends AsyncTask<Void, Void, Void> {

        ArrayList<SpotifyPlaylist> playlists_default;
        ArrayList<SpotifyPlaylist> playlists_user;
        // ui calling possible
        protected void onPreExecute() {
            Log.d("onPreExecute", "retreivedataasync task");
        }

        // run threads
        @Override
        protected Void doInBackground(Void... params) {

            playlists_default = dataStorage.fetchSpotifyEntriesDefault();
            if (playlists_default.size() < NUMBER_DEFAULT_PLAYLISTS){
                 finishedDefaultDataRetrieval = false;
            } else{
                finishedDefaultDataRetrieval = true;
            }


            playlists_user = dataStorage.fetchSpotifyEntriesUser();
            if (playlists_user.size() == 0){
                finishedUserDataRetrieval = false;
            } else{
                finishedUserDataRetrieval = true;
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d("onPostExecute", "main activity playlists_default length is "+ playlists_default.size());


            //dataStorage.close();
            if(!finishedDefaultDataRetrieval){
                Log.d("retrieve data asynctask", "did not all data");
                fetchDefaultPlaylists(getApplicationContext());
            } else {
                Log.d("retrieve data asynctask", "got all data");
                finishedDefaultDataRetrieval = true;

                }

            if(!finishedUserDataRetrieval){
                Log.d("retrieve data asynctask", "did not all user data");
                fetchUserPlaylists(getApplicationContext());
            } else {
                Log.d("retrieve data asynctask", "got all data");
                finishedUserDataRetrieval = true;

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
        Log.d("main activity", "in fetch default playlists_default access token is "+ MainActivity.accessToken);


        for (int i = 0; i< default_playlists.length;i++) {


            StringRequest jsObjRequest = new StringRequest
                    (Request.Method.GET, default_playlists[i], new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("fetch default playlists_default Response", response);
                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                String id = jsonObject.getString("id");
                                JSONObject owner = jsonObject.getJSONObject("owner");
                                String userId = owner.getString("id");
                                Log.d("fetchdefaultPlaylists", "user id is " + userId);

                                JSONArray arr = jsonObject.getJSONArray("images");
                                for (int i = 0; i < arr.length(); i++)
                                {
                                    String imageUrl= arr.getJSONObject(i).getString("url");
                                    Log.d("image url", imageUrl);
                                    SpotifyPlaylist entry = new SpotifyPlaylist();
                                    entry.setPlaylistId(id);
                                    entry.setImageUrl(imageUrl);
                                    entry.setUserId(userId);

                                    playlists.add(entry);
                                    imageUrls.add(imageUrl.toString());
                                    Log.d("imageUrls size: ", "size is " + imageUrls.size());

                                    if(playlists.size() == default_playlists.length){
                                        Log.d("fetch default playlists_default", "imageURls size equals default");
                                        try {
                                            new SpotifyAsyncSaveDefault().execute(playlists);
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


    public void fetchUserPlaylists(Context input){

        RequestQueue queue = Volley.newRequestQueue(input);

        String url = "https://api.spotify.com/v1/me/playlists";


        final ArrayList<String> imageUrls = new ArrayList<String>();
        final ArrayList<SpotifyPlaylist> playlists = new ArrayList<>();
        Log.d("main activity", "in fetch user playlists_default access token is "+ MainActivity.accessToken);


        StringRequest jsObjRequest = new StringRequest
                (Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("fetch user playlists_user Response", response);
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray items = jsonObject.getJSONArray("items");
                            for (int i = 0; i < items.length(); i++){
                                JSONObject item = items.getJSONObject(i);
                                String id = item.getString("id");
                                JSONObject owner = item.getJSONObject("owner");
                                String userId = owner.getString("id");
                                Log.d("fetch user playlists", "user id is "+ userId + "for playlist " + item.getString("name"));
                                JSONArray images = item.getJSONArray("images");
                                JSONObject image = images.getJSONObject(0);
                                String url = image.getString("url");
                                Log.d("fetch user playlists", "url is " + url);

                                SpotifyPlaylist entry = new SpotifyPlaylist();
                                entry.setPlaylistId(id);
                                entry.setImageUrl(url);
                                entry.setUserId(userId);
                                playlists.add(entry);
                            }

                                new SpotifyAsyncSaveUser().execute(playlists);



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


    public void fetchPlaylistTracksDefault(String id, final SpotifyPlaylist playlist, final Day day, final Weather weather){
        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        String url = "https://api.spotify.com/v1/users/" + playlist.getUserId() + "/playlists/"+ id + "/tracks";
        final ArrayList<String> imageUrls = new ArrayList<String>();
        Log.d("Spotify service", "in fetch playlist tracks access token is "+ MainActivity.accessToken);

        StringRequest jsObjRequest = new StringRequest
                (Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("fetchPlaylisttracks Response", response);
                        playlist.setTrackInfo(response);
                        new SaveSongsAsyncTaskDefault(playlist, day, weather).execute();

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

    public void fetchPlaylistTracksUser(String id, final SpotifyPlaylist playlist){
        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        String url = "https://api.spotify.com/v1/users/" + playlist.getUserId() + "/playlists/"+ id + "/tracks";
        final ArrayList<String> imageUrls = new ArrayList<String>();
        Log.d("Spotify service", "in user fetch playlist tracks access token is "+ MainActivity.accessToken);

        StringRequest jsObjRequest = new StringRequest
                (Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("fetchPlaylisttracks user Response", response);
                        playlist.setTrackInfo(response);
                        new SaveSongsAsyncTaskUser(playlist).execute();

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



    private class SpotifyAsyncSaveDefault extends AsyncTask<ArrayList<SpotifyPlaylist>, Void, ArrayList<SpotifyPlaylist>> {

        String [] dayArr = { "Sun","Mon", "Tues","Wed", "Thurs", "Fri", "Sat"};
        String [] weatherArr = {"Clear", "Rainy","Stormy", "Snowy", "Cloudy", "Foggy", "Windy"};

        // ui calling possible
        protected void onPreExecute() {

        }

        // run threads
        @Override
        protected ArrayList<SpotifyPlaylist> doInBackground(ArrayList<SpotifyPlaylist>... params) {
            ArrayList<SpotifyPlaylist> playlists = params[0];

            for (int i = 0; i < playlists.size(); i++){
                Log.d("Spotifyasyncsave", "do in background, playlist id is " + playlists.get(i).getPlaylistId());
                playlists.get(i).setId(dataStorage.insertSpotifyEntryDefault(playlists.get(i)).getId());
            }
            return playlists;

        }

        @Override
        protected void onPostExecute(ArrayList<SpotifyPlaylist> result) {
            Day day = null;
            Weather weather = null;
            for (int i = 0; i < result.size(); i++){
                if(i < dayArr.length){
                    day = new Day();
                    weather = new Weather();
                    day.setName(dayArr[i]);
                    weather.setName(weatherArr[i]);
                    day.setId(dataStorage.insertDayEntry(day).getId());
                    weather.setId(dataStorage.insertWeatherEntry(weather).getId());
                } else{
                    day = null;
                    weather = null;
                }

                fetchPlaylistTracksDefault(result.get(i).getPlaylistId(), result.get(i), day, weather);
            }
        }

    }


    private class SpotifyAsyncSaveUser extends AsyncTask<ArrayList<SpotifyPlaylist>, Void, ArrayList<SpotifyPlaylist>> {


        // ui calling possible
        protected void onPreExecute() {

        }

        // run threads
        @Override
        protected ArrayList<SpotifyPlaylist> doInBackground(ArrayList<SpotifyPlaylist>... params) {
            ArrayList<SpotifyPlaylist> playlists = params[0];

            for (int i = 0; i < playlists.size(); i++){
                Log.d("Spotifyasyncsave user", "do in background, playlist id is " + playlists.get(i).getPlaylistId());
                playlists.get(i).setId(dataStorage.insertSpotifyEntryUser(playlists.get(i)).getId());
            }
            return playlists;

        }

        @Override
        protected void onPostExecute(ArrayList<SpotifyPlaylist> result) {

            for (int i = 0; i < result.size(); i++){


                fetchPlaylistTracksUser(result.get(i).getPlaylistId(), result.get(i));
            }
        }

    }

    private class SaveSongsAsyncTaskDefault extends AsyncTask<Void, Void, Void> {

        SpotifyPlaylist playlist;
        Day day;
        Weather weather;

        public SaveSongsAsyncTaskDefault(SpotifyPlaylist p,Day d, Weather w){
            this.playlist = p;
            this.day = d;
            this.weather= w;
        }

        protected void onPreExecute() {

        }

        // run threads
        @Override
        protected Void doInBackground(Void... params) {

            dataStorage.updateSpotifyEntryDefault(this.playlist);

            if(this.day != null) {
                Log.d("savesongsasynctask", "playlist set for day: " + this.day.getName());
                this.day.setSpotifyPlaylist(this.playlist);
                dataStorage.updateDayEntry(this.day);

                Log.d("savesongsasynctask", "playlist set for weather: " + this.weather.getName());
                this.weather.setSpotifyPlaylist(this.playlist);
                dataStorage.updateWeatherEntry(this.weather);
            } else{
                finishedDefaultDataRetrieval = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Log.d("savesongsasynctask", "onpostexecute ");

            if (finishedDefaultDataRetrieval){
                Log.d("savesong async task", "finished data retrieval");
//
//
            }

        }

    }

    private class SaveSongsAsyncTaskUser extends AsyncTask<Void, Void, Void> {

        SpotifyPlaylist playlist;


        public SaveSongsAsyncTaskUser(SpotifyPlaylist p){
            this.playlist = p;

        }

        protected void onPreExecute() {

        }

        // run threads
        @Override
        protected Void doInBackground(Void... params) {

            dataStorage.updateSpotifyEntryUser(this.playlist);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            Log.d("savesongsasynctask", "user onpostexecute ");



        }

    }







    //******** Check run time permission for locationManager. This is for v23+  ********
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }
//
//
//
//    private class IncomingMessageHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case LocationService.MSG_LOCATION:
//                    Log.d(TAG, "received MSG_EXERCISE_ENTRY");
//                    Location l  = msg.getData().getParcelable("location");
//                    if(l!= null) {
//                        if(mIsBound){
//                            try {
//                                doUnbindService();
//                                stopService(new Intent(MainActivity.this, LocationService.class));
//                            } catch (Throwable t) {
//                                Log.e(TAG, "Failed to unbind from the service", t);
//                            }
//
//                        }
//                        Log.d(TAG, "location lat is " + l.getLatitude() + "and long is " + l.getLongitude());
//                        //updateMap(entry);
//                        fetchWeather(l);
//                    }
//
//                    break;
//
//                default:
//                    super.handleMessage(msg);
//            }
//        }
//    }
//
//    private void automaticBind() {
//        if (LocationService.isRunning()) {
//            Log.d(TAG, "C:MyService.isRunning: doBindService()");
//            doBindService();
//        }
//    }
//
//    private void doBindService() {
//
//
//        //http://stackoverflow.com/questions/1916253/bind-service-to-activity-in-android
//
//        Log.d(TAG, "MainActivity in doBindService");
//        bindService(new Intent(this, LocationService.class), mConnection,Context.BIND_AUTO_CREATE);//http://stackoverflow.com/questions/14746245/use-0-or-bind-auto-create-for-bindservices-flag
//        mIsBound = true;
//
//    }
//
//    private void doUnbindService() {
//        Log.d(TAG, "C:doUnBindService()");
//        if (mIsBound) {
//            // If we have received the service, and hence registered with it,
//            // then now is the time to unregister.
//            if (mServiceMessenger != null) {
//                try {
//                    Message msg = Message.obtain(null,LocationService.MSG_UNREGISTER_CLIENT);
//                    Log.d(TAG, "C: TX MSG_UNREGISTER_CLIENT");
//                    msg.replyTo = mMessenger;
//                    mServiceMessenger.send(msg);
//                } catch (RemoteException e) {
//                    // There is nothing special we need to do if the service has
//                    // crashed.
//                }
//            }
//            // Detach our existing connection.
//            unbindService(mConnection);
//            mIsBound = false;
//
//        }
//    }
//
//
//    public void onServiceConnected(ComponentName name, IBinder service) {
//        Log.d(TAG, "C:onServiceConnected()");
//        mServiceMessenger = new Messenger(service);
//
//        try {
//            Message msg = Message.obtain(null, LocationService.MSG_REGISTER_CLIENT);
//            msg.replyTo = mMessenger; //u tell the server the return Messenger: by sending through this Messenger the msg will get to this client.
//
//            mServiceMessenger.send(msg);
//        } catch (RemoteException e) {
//            // In this case the service has crashed before we could even do
//            // anything with it
//        }
//    }
//
//
//    @Override
//    public void onServiceDisconnected(ComponentName name) {
//        //Log.d(TAG, "C:onServiceDisconnected()");
//        // This is called when the connection with the service has been
//        // unexpectedly disconnected - process crashed.
//        mServiceMessenger = null;
//
//    }
//
//    public void fetchWeather (Location loc){
//        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
//        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + loc.getLatitude() + "&lon=" + loc.getLongitude()+"&"+ WEATHER_API_KEY;
//        //String url = "http://api.openweathermap.org/data/2.5/weather?q=London&"+WEATHER_API_KEY;
//        StringRequest jsObjRequest = new StringRequest
//                (Request.Method.GET, url, new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // response
//                        Log.d("fetchWeather Response", response);
//
//                    }
//                },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                // TODO Auto-generated method stub
//                                Log.d("ERROR", "error => " + error.toString());
//                            }
//                        }
//                );
//        queue.add(jsObjRequest);
//    }



}
