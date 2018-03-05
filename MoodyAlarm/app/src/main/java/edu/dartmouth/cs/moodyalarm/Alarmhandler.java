package edu.dartmouth.cs.moodyalarm;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;

import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by jiyunsung on 2/28/18.
 * a class just to handle start_alert and stop_alert
 */

public class Alarmhandler {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;


    public void start_alert(Context context, String uri){
//        mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
//        mediaPlayer.start();

        if (uri.equals("Default")) {
            mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
            mediaPlayer.start();
        } else {
            Log.d("alarmhandler", "start alert access token is: " + MainActivity.accessToken);
            MainActivity.mPlayer.playUri(null, uri, 0, 0);

        }

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);
    }


    public void stop_alert(Context context){
        if (mediaPlayer != null){
            mediaPlayer.release();
        }

        //Spotify.destroyPlayer(MainActivity.class);
        MainActivity.mPlayer.pause(null);
        if (vibrator.hasVibrator()) {
            vibrator.cancel();
        }
    }



}
