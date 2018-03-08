package edu.dartmouth.cs.moodyalarm;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
    private Context context;
    private AlarmEntry alarmEntry;
    SharedPreferences prefs;
    private ArrayList<String> challenges;

    private ImageView voiceX;
    private ImageView sudokuX;
    private ImageView puzzleX;
    private ImageView mathX;
    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("pop up activity", "oncreate called");
        setContentView(R.layout.activity_popup);

    }

    @Override
    protected void onResume(){
        super.onResume();
        prefs = getSharedPreferences(SnoozeSettings.PREFS_NAME, 0);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        Log.d("popup onresume", "day is " + day);
        Log.d("popup onresume", "pos is " + AlarmSettings.position);
        mIsBound = false; // by default set this to unbound

        Intent intent = getIntent();
        Long id = intent.getLongExtra("pos", 1);
        alarmEntry = MainActivity.dataStorage.fetchEntryByIndexAlarm(id);
        //setting = alarmEntry.getSetting();
        context = this;

        connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        challenges = new ArrayList<String>();

        challenges = new ArrayList<String>();
        if (connected & prefs.getBoolean(SnoozeSettings.VOICE_ON, true))
            challenges.add(SnoozeSettings.VOICE_ON);
        if (prefs.getBoolean(SnoozeSettings.SUDOKU_ON, true))
            challenges.add(SnoozeSettings.SUDOKU_ON);
        if (prefs.getBoolean(SnoozeSettings.PUZZLE_ON, true))
            challenges.add(SnoozeSettings.PUZZLE_ON);
        if (prefs.getBoolean(SnoozeSettings.MATH_ON, true))
            challenges.add(SnoozeSettings.MATH_ON);

        Button dismiss = findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Log.d("pop up activity", "dismiss pressed");
                if (challenges.size() > 0) {

                    // choose a random activity among the enabled ones
                    Random rand = new Random();
                    int choice = rand.nextInt(challenges.size());
                    String challenge = challenges.get(choice);
                    Intent challengeIntent;
                    if (challenge.equals(SnoozeSettings.VOICE_ON)) {
                        Log.d("pop up activity", "challenge is voice");
                        challengeIntent = new Intent(PopupActivity.this, VoiceRecognitionActivity.class);
                        challengeIntent.putExtra("alarm", alarmEntry);
                        startActivity(challengeIntent);
                    }
                    else if (challenge.equals(SnoozeSettings.SUDOKU_ON)) {
                        Log.d("pop up activity", "challenge is sudoku");
                        challengeIntent = new Intent(PopupActivity.this, SudokuActivity.class);
                        challengeIntent.putExtra("alarm", alarmEntry);
                        startActivity(challengeIntent);
                    }
                    else if (challenge.equals(SnoozeSettings.PUZZLE_ON)) {
                        Log.d("pop up activity", "challenge is puzzle");
                        challengeIntent = new Intent(PopupActivity.this, PuzzleActivity.class);
                        challengeIntent.putExtra("alarm", alarmEntry);
                        startActivity(challengeIntent);
                    }
                    else {
                        Log.d("pop up activity", "challenge is math");
                        challengeIntent = new Intent(PopupActivity.this, MathActivity.class);
                    }




                } else {
                    alarm.stop_alert(context);
                    Toast.makeText(context, "Alarm dismissed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Log.d("popup oncreate", "setting retrieved is " + alarmEntry.getSetting());
        automaticBind();
        doBindService();
        startService(new Intent(PopupActivity.this, LocationService.class));
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
        //alarm.stop_alert(this);

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

    public void getPlaylistByDay(int id, Context context, String response){
        String uri = "";
        Log.d("playPlaylist by day", "id is " + id);

        Day today = MainActivity.dataStorage.fetchEntryByIndexDay(id);
        SpotifyPlaylist todayPlaylist = today.getSpotifyPlaylist();
        Log.d("playlist id is ", todayPlaylist.getPlaylistId());
        String tracks = todayPlaylist.getTrackInfo();
        displayPopUp(tracks, response);


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

                        if (alarmEntry.getSetting() != null) {
                            if (alarmEntry.getSetting().equals("day")) {
                                Calendar calendar = Calendar.getInstance();
                                int day = calendar.get(Calendar.DAY_OF_WEEK);
                                getPlaylistByDay(day, context, response);

                            } else {
                                getPlaylistByWeather(id, getApplicationContext(), response);
                            }
                        } else{
                            getPlaylistByWeather(id, getApplicationContext(), response);
                        }

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

    public void getPlaylistByWeather(int id, Context context, String response){
        Log.d("pop up activity", "play by weather");
        Weather weather = MainActivity.dataStorage.fetchEntryByIndexWeather(id);
        SpotifyPlaylist todayPlaylist = weather.getSpotifyPlaylist();
        Log.d("playlist id is ", todayPlaylist.getPlaylistId());
        String tracks = todayPlaylist.getTrackInfo();

        displayPopUp(tracks, response);

    }

    public void displayPopUp(String tracks, String weatherResponse){
        try {

            JSONObject jsonObject = new JSONObject(tracks);


            JSONArray arr = jsonObject.getJSONArray("items");

            int randomNum = ThreadLocalRandom.current().nextInt(0, arr.length() + 1);

            JSONObject item = arr.getJSONObject(randomNum);
            JSONObject track = item.getJSONObject("track");
            JSONObject album = track.getJSONObject("album");
            JSONArray images = album.getJSONArray("images");
            JSONObject image = images.getJSONObject(1);
            String imageUrl = image.getString("url");

            String songName = album.getString("name");
            //Log.d("song name is ", songName);
            TextView song = findViewById(R.id.song_name);
            song.setText(songName);

            JSONArray artists = album.getJSONArray("artists");
            JSONObject artist = artists.getJSONObject(0);
            String artistName = artist.getString("name");
            TextView artist_name = findViewById(R.id.song_artist);
            artist_name.setText(artistName);

            ImageView songPhoto = findViewById(R.id.playlist_img);
            Picasso.with(getApplicationContext()).
                    load(imageUrl).into(songPhoto);
            uri = album.getString("uri");


            JSONObject weatherObject = new JSONObject(weatherResponse);
            JSONArray weather = weatherObject.getJSONArray("weather");
            JSONObject weatherInfo = weather.getJSONObject(0);
            String main = weatherInfo.getString("main");
            String description = weatherInfo.getString("description");

            JSONObject mainInfo = weatherObject.getJSONObject("main");
            String temp = mainInfo.getString("temp");


            String location = weatherObject.getString("name");

            TextView w = findViewById(R.id.weather);
            w.setText(main);

            TextView d = findViewById(R.id.weather_description);
            d.setText(description);

            TextView temperature = findViewById(R.id.temp);
            temperature.setText(temp);

            TextView loc = findViewById(R.id.location);
            loc.setText(location);

            // display current time
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTimeInMillis(System.currentTimeMillis());
            int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
            int currentMinute = rightNow.get(Calendar.MINUTE);

            String ampm = "";
            if(currentHour >= 12){
                ampm = "PM";
            } else{
                ampm = "AM";
            }
            String time= "";
            if (currentMinute < 10){
                time = currentHour+":" + "0"+currentMinute;
            } else{
                time = currentHour+":" + currentMinute;
            }

            TextView timeDisplay = findViewById(R.id.labelExpanded);
            timeDisplay.setText(time);

            TextView amPm = findViewById(R.id.ampm);
            amPm.setText(ampm);

            Date now = new Date();
            SimpleDateFormat newDateFormat = new SimpleDateFormat("EEEE MMM d, yyyy");
            String MyDate = newDateFormat.format(now);

            TextView day = findViewById(R.id.day);
            day.setText(MyDate);

            LinearLayout weekdays = (LinearLayout) findViewById(R.id.weekday);
            TextView repeat = (TextView) findViewById(R.id.NoRepeatText);
            if (alarmEntry.getRepeated() == 1) {
                weekdays.setVisibility(View.VISIBLE);
                repeat.setVisibility(View.INVISIBLE);
            } else {
                weekdays.setVisibility(View.INVISIBLE);
                repeat.setVisibility(View.VISIBLE);
            }

            Boolean[] daysOfWeek = alarmEntry.getDaysofweek();

            Button buttonSun = (Button) findViewById(R.id.buttonSun);
            if(daysOfWeek[0]){
                buttonSun.setPaintFlags(buttonSun.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                buttonSun.setTextColor(Color.parseColor("#ffffff"));
            }

            Button buttonM =  findViewById(R.id.buttonM);
            if(daysOfWeek[1]){
                buttonM.setPaintFlags(buttonSun.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                buttonM.setTextColor(Color.parseColor("#ffffff"));
            }

            Button buttonTue = findViewById(R.id.buttonTue);
            if(daysOfWeek[2]){
                buttonTue.setPaintFlags(buttonSun.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                buttonTue.setTextColor(Color.parseColor("#ffffff"));
            }

            Button buttonW =  findViewById(R.id.buttonW);
            if(daysOfWeek[3]){
                buttonW.setPaintFlags(buttonSun.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                buttonW.setTextColor(Color.parseColor("#ffffff"));
            }

            Button buttonThur =findViewById(R.id.buttonThur);
            if(daysOfWeek[4]){
                buttonThur.setPaintFlags(buttonSun.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                buttonThur.setTextColor(Color.parseColor("#ffffff"));
            }

            Button buttonF = findViewById(R.id.buttonF);
            if(daysOfWeek[5]){
                buttonF.setPaintFlags(buttonSun.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                buttonF.setTextColor(Color.parseColor("#ffffff"));
            }

            Button buttonSat = findViewById(R.id.buttonSat);
            if(daysOfWeek[6]){
                buttonSat.setPaintFlags(buttonSun.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                buttonSat.setTextColor(Color.parseColor("#ffffff"));
            }

            voiceX = findViewById(R.id.voiceX);
            sudokuX = findViewById(R.id.sudokuX);
            puzzleX = findViewById(R.id.puzzleX);
            mathX = findViewById(R.id.mathX);

            if (prefs.getBoolean(SnoozeSettings.VOICE_ON, true))
                voiceX.setVisibility(View.INVISIBLE);
            if (prefs.getBoolean(SnoozeSettings.SUDOKU_ON, true))
                sudokuX.setVisibility(View.INVISIBLE);
            if (prefs.getBoolean(SnoozeSettings.PUZZLE_ON, true))
                puzzleX.setVisibility(View.INVISIBLE);
            if (prefs.getBoolean(SnoozeSettings.MATH_ON, true))
                mathX.setVisibility(View.INVISIBLE);

            Log.d("track uri is ", uri);
            alarm.start_alert(this, uri);

        } catch(JSONException e){Log.d("error", e.toString());}

    }


    public void onSnooze(View view) {

        Integer maxSnooze = prefs.getInt(SnoozeSettings.SNOOZE_MAX, 0); // if this returns 0, that means the user doesn't want a limit on the number of snoozes

        if (maxSnooze == 0 || alarmEntry.getSnooze() < maxSnooze) {
            alarm.stop_alert(this);
            Integer snoozeLength = prefs.getInt(SnoozeSettings.SNOOZE_LENGTH, 9);
            alarmEntry.setSnooze(this, snoozeLength);
            finish();
        } else {
            Toast.makeText(context, "You reached your maximum number of snoozes! Please dismiss the alarm.", Toast.LENGTH_LONG).show();
        }
    }

    public void onDismiss(View view) {
        Log.d("challenges", challenges.toString());

        if (alarmEntry.getRepeated() == 0) { // no repeats : this is the last time the alarm is going off, so this should be off
            alarmEntry.setOnOff(0);
            MainActivity.dataStorage.updateAlarmEntry(alarmEntry);
        }

        // at least one challenge enabled
        if (challenges.size() > 0) {
            // choose a random activity among the enabled ones
            Random rand = new Random();
            int choice = rand.nextInt(challenges.size());
            String challenge = challenges.get(choice);
            Intent challengeIntent;
            if (challenge.equals(SnoozeSettings.VOICE_ON))
                challengeIntent = new Intent(this, VoiceRecognitionActivity.class);
            else if (challenge.equals(SnoozeSettings.SUDOKU_ON))
                challengeIntent = new Intent(this, SudokuActivity.class);
            else if (challenge.equals(SnoozeSettings.PUZZLE_ON))
                challengeIntent = new Intent(this, PuzzleActivity.class);
            else
                challengeIntent = new Intent(this, MathActivity.class);

            challengeIntent.putExtra("alarm", alarmEntry);
            startActivity(challengeIntent);

        } else {
            alarm.stop_alert(context);
            Toast.makeText(context, "Alarm dismissed", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public void onVoiceX(View view) {
        if (connected) {
            challenges.add(SnoozeSettings.VOICE_ON);
            view.setVisibility(View.INVISIBLE);
            Log.d("challenges", challenges.toString());
        }
    }

    public void onSudokuX(View view) {
        challenges.add(SnoozeSettings.SUDOKU_ON);
        view.setVisibility(View.INVISIBLE);
        Log.d("challenges", challenges.toString());
    }

    public void onMathX(View view) {
        challenges.add(SnoozeSettings.MATH_ON);
        view.setVisibility(View.INVISIBLE);
        Log.d("challenges", challenges.toString());
    }

    public void onPuzzleX(View view) {
        challenges.add(SnoozeSettings.PUZZLE_ON);
        view.setVisibility(View.INVISIBLE);
        Log.d("challenges", challenges.toString());
    }

    public void onVoiceO(View view) {
        challenges.remove(SnoozeSettings.VOICE_ON);
        voiceX.setVisibility(View.VISIBLE);
        Log.d("challenges", challenges.toString());
    }
    public void onSudokuO(View view){
        challenges.remove(SnoozeSettings.SUDOKU_ON);
        sudokuX.setVisibility(View.VISIBLE);
        Log.d("challenges", challenges.toString());
    }
    public void onMathO(View view){
        challenges.remove(SnoozeSettings.MATH_ON);
        mathX.setVisibility(View.VISIBLE);
        Log.d("challenges", challenges.toString());
    }
    public void onPuzzleO(View view){
        challenges.remove(SnoozeSettings.PUZZLE_ON);
        puzzleX.setVisibility(View.VISIBLE);
        Log.d("challenges", challenges.toString());
    }
}
