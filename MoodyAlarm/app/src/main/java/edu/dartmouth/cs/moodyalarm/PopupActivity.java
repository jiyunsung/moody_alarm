package edu.dartmouth.cs.moodyalarm;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.spotify.sdk.android.player.Spotify;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Calendar;

public class PopupActivity extends AppCompatActivity implements ServiceConnection {

    public static Alarmhandler alarm = new Alarmhandler();
    public static final String WEATHER_API_KEY = "APPID=d5233bd27811890e0b347bc47782312c";
    boolean mIsBound;
    private ServiceConnection mConnection = this;
    private Messenger mServiceMessenger = null;
    private static final String TAG = "vj";
    private final Messenger mMessenger = new Messenger(new IncomingMessageHandler());

    private static final int PERMISSION_REQUEST_CODE = 1;

    private Weather weather;
    private String uri="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        Log.d("popup oncreate", "day is " + day);
        Log.d("popup oncreate", "pos is " + AlarmSettings.position);
        mIsBound = false; // by default set this to unbound

        if(AlarmSettings.position == 1) {
            Log.d("popUp activity", "pos is 1");
            uri = playPlaylistByDay(day, this);
            alarm.start_alert(this, uri);
        } else{
            Log.d("popUp activity", "pos is 0 fetch by weather");
            automaticBind();
            doBindService();
            startService(new Intent(PopupActivity.this, LocationService.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "C:onDestroy()");
        try {
            doUnbindService();
            stopService(new Intent(PopupActivity.this, LocationService.class));
            mIsBound = false;
        } catch (Throwable t) {
            Log.e(TAG, "Failed to unbind from the service", t);
        }
    }

    @Override
    public void onBackPressed() {
        alarm.stop_alert(this);

        super.onBackPressed();
        if(mIsBound) {
            try {
                doUnbindService();
                stopService(new Intent(PopupActivity.this, LocationService.class));
            } catch (Throwable t) {
                Log.e(TAG, "Failed to unbind from the service", t);
            }
        }
        finishActivity(0);
    }

    public String playPlaylistByDay(int id, Context context){
        String uri = "";
        Log.d("playPlaylist by day", "id is " + id);

        Day today = MainActivity.dataStorage.fetchEntryByIndexDay(id);
        SpotifyPlaylist todayPlaylist = today.getSpotifyPlaylist();
        Log.d("playlist id is ", todayPlaylist.getPlaylistId());
        String tracks = todayPlaylist.getTrackInfo();
        try {

            JSONObject jsonObject = new JSONObject(tracks);


            JSONArray arr = jsonObject.getJSONArray("items");

            JSONObject item = arr.getJSONObject(0);
            JSONObject track = item.getJSONObject("track");
            JSONObject album = track.getJSONObject("album");
            JSONArray images = album.getJSONArray("images");
            JSONObject image = images.getJSONObject(1);
            String imageUrl = image.getString("url");

            String songName = album.getString("name");
            //Log.d("song name is ", songName);
            TextView song = findViewById(R.id.song_name);
            song.setText(songName);

            TextView day = findViewById(R.id.field);
            day.setText(today.getName());
            uri = album.getString("uri");


            ImageView songPhoto = findViewById(R.id.playlist_img);
            Picasso.with(getApplicationContext()).
                    load(imageUrl).into(songPhoto);


            Log.d("track uri is ", uri);

        } catch(JSONException e){Log.d("error", e.toString());}
        return uri;


    }

    //******** Check run time permission for locationManager. This is for v23+  ********
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }



    private class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LocationService.MSG_LOCATION:
                    Log.d(TAG, "received MSG_LOCATION");
                    Location l  = msg.getData().getParcelable("location");
                    if(l!= null) {
                        if(mIsBound){
                            try {
                                doUnbindService();
                                stopService(new Intent(PopupActivity.this, LocationService.class));
                            } catch (Throwable t) {
                                Log.e(TAG, "Failed to unbind from the service", t);
                            }

                        }
                        Log.d(TAG, "location lat is " + l.getLatitude() + "and long is " + l.getLongitude());
                        //updateMap(entry);
                        fetchWeather(l);
                    }

                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    private void automaticBind() {
        if (LocationService.isRunning()) {
            Log.d(TAG, "C:MyService.isRunning: doBindService()");
            doBindService();
        }
    }

    private void doBindService() {
        //http://stackoverflow.com/questions/1916253/bind-service-to-activity-in-android

        Log.d(TAG, "PopupActivity in doBindService");
        bindService(new Intent(this, LocationService.class), mConnection,Context.BIND_AUTO_CREATE);//http://stackoverflow.com/questions/14746245/use-0-or-bind-auto-create-for-bindservices-flag
        mIsBound = true;

    }

    private void doUnbindService() {
        Log.d(TAG, "C:doUnBindService()");
        if (mIsBound) {
            // If we have received the service, and hence registered with it,
            // then now is the time to unregister.
            if (mServiceMessenger != null) {
                try {
                    Message msg = Message.obtain(null,LocationService.MSG_UNREGISTER_CLIENT);
                    Log.d(TAG, "C: TX MSG_UNREGISTER_CLIENT");
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has
                    // crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;

        }
    }


    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "C:onServiceConnected()");
        mServiceMessenger = new Messenger(service);

        try {
            Message msg = Message.obtain(null, LocationService.MSG_REGISTER_CLIENT);
            msg.replyTo = mMessenger; //u tell the server the return Messenger: by sending through this Messenger the msg will get to this client.

            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            // In this case the service has crashed before we could even do
            // anything with it
        }
    }


    @Override
    public void onServiceDisconnected(ComponentName name) {
        //Log.d(TAG, "C:onServiceDisconnected()");
        // This is called when the connection with the service has been
        // unexpectedly disconnected - process crashed.
        mServiceMessenger = null;

    }

    public void fetchWeather (Location loc){
        RequestQueue queue = Volley.newRequestQueue(this.getApplicationContext());
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + loc.getLatitude() + "&lon=" + loc.getLongitude()+"&"+ WEATHER_API_KEY;
        //String url = "http://api.openweathermap.org/data/2.5/weather?q=London&"+WEATHER_API_KEY;
        StringRequest jsObjRequest = new StringRequest
            (Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // response
                    Log.d("fetchWeather Response", response);
                    try {

                        JSONObject jsonObject = new JSONObject(response);


                        JSONArray w = jsonObject.getJSONArray("weather");

                        JSONObject weatherItem= w.getJSONObject(0);
                        String id_string = weatherItem.getString("id");
                        int weatherId = Integer.parseInt(id_string);
                        int id;
                        if(200 <= weatherId && weatherId < 300){
                            id = 3;
                        } else if (300 <= weatherId && weatherId <600){
                            id = 2;
                        } else if (600 <= weatherId && weatherId < 700){
                            id=4;
                        } else if (700 <= weatherId && weatherId < 800){
                            id=6;
                        } else if (weatherId == 800 || weatherId == 801){
                           id=1;
                        } else if (weatherId == 803 || weatherId == 804){
                            id=5;
                        } else{
                            id=7;
                        }

                        playPlaylistByWeather(id, getApplicationContext());

                    } catch(JSONException e){Log.d("error", e.toString());}


                }
            },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub
                                Log.d("ERROR", "error => " + error.toString());
                            }
                        }
                );
        queue.add(jsObjRequest);
    }

    public void playPlaylistByWeather(int id, Context context){
        Log.d("pop up activity", "play by weather");
        Weather weather = MainActivity.dataStorage.fetchEntryByIndexWeather(id);
        SpotifyPlaylist todayPlaylist = weather.getSpotifyPlaylist();
        Log.d("playlist id is ", todayPlaylist.getPlaylistId());
        String tracks = todayPlaylist.getTrackInfo();
        try {

            JSONObject jsonObject = new JSONObject(tracks);


            JSONArray arr = jsonObject.getJSONArray("items");

            JSONObject item = arr.getJSONObject(0);
            JSONObject track = item.getJSONObject("track");
            JSONObject album = track.getJSONObject("album");
            JSONArray images = album.getJSONArray("images");
            JSONObject image = images.getJSONObject(1);
            String imageUrl = image.getString("url");

            String songName = album.getString("name");
            //Log.d("song name is ", songName);
            TextView song = findViewById(R.id.song_name);
            song.setText(songName);

            TextView day = findViewById(R.id.field);
            day.setText(weather.getName());
            uri = album.getString("uri");


            ImageView songPhoto = findViewById(R.id.playlist_img);
            Picasso.with(getApplicationContext()).
                    load(imageUrl).into(songPhoto);


            Log.d("track uri is ", uri);

        } catch(JSONException e){Log.d("error", e.toString());}
        alarm.start_alert(this, uri);

    }



}
