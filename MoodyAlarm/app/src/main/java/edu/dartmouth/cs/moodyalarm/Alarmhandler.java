package edu.dartmouth.cs.moodyalarm;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.MenuItem;

import com.spotify.sdk.android.player.Player;

/**
 * Created by jiyunsung on 2/28/18.
 * a class just to handle start_alert and stop_alert
 */

public class Alarmhandler {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;


    public void start_alert(Context context){
        mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer.start();


        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);
    }


    public void stop_alert(Context context){
        if (mediaPlayer != null){
            mediaPlayer.release();
        }
        if (vibrator.hasVibrator()) {
            vibrator.cancel();
        }
    }

}
