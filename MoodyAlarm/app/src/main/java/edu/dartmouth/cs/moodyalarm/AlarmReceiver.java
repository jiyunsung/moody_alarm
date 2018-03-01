package edu.dartmouth.cs.moodyalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver
{
    //Receive broadcast
    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.d("received??", "TT");
        intent.getAction();

        startPopup(context);
//        Toast.makeText(context, "Alarm! Wake up! Wake up!", Toast.LENGTH_LONG).show();
//        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        if (alarmUri == null)
//        {
//            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        }
//        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
//        ringtone.play();
    }

    // start the Alarm Popup
    private void startPopup(Context context) {

        Intent emaIntent = new Intent(context, VoiceRecognitionActivity.class); //The activity you  want to start.
        //Intent emaIntent = new Intent(context, PopupActivity.class); //The activity you  want to start.
        emaIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(emaIntent);
    }
}
