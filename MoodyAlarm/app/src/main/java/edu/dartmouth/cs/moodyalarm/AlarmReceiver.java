package edu.dartmouth.cs.moodyalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver
{
    //Receive broadcast
    @Override
    public void onReceive(final Context context, Intent intent) {

        intent.getAction();
        Log.d("Alarm receiver", "received intent");

        long alarmId = intent.getLongExtra("alarm", -1);
        Log.d("id", Long.toString(alarmId));
//        AlarmEntry entry = MainActivity.dataStorage.fetchEntryByIndexAlarm(alarmId);
//        if (entry.getRepeated() == 0 ) { // no repeats : this is the last time the alarm is going off, so this should be off
//            entry.setOnOff(0);
//            MainActivity.dataStorage.updateAlarmEntry(entry);
//        }

        String setting = "";
////        setting = intent.getStringExtra("setting");
////        if (!setting.isEmpty()) {
////            Log.d("Alarm receiver", "on receive setting is " + setting);
////        }else {
////            Log.d("Alarm receiver", "on receive setting is null");
////        }
//
        Long id  = intent.getLongExtra("alarm", 1);
//
//        Log.d("alarmReceiver ", "on receive id is " + id);
        //AlarmEntry a = MainActivity.dataStorage.fetchEntryByIndexAlarm(1);
//        if (a!= null){
//            setting = "day";
//        } else{
//            setting = "weather";
//        }
////        setting = a.getSetting();
//        Log.d("alarmReceiver ", "alarm setting is " + setting);

        startPopup(context, id);
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
    private void startPopup(Context context, Long id) {

        //Intent emaIntent = new Intent(context, VoiceRecognitionActivity.class); //The activity you  want to start.
        //Intent emaIntent = new Intent(context, PopupActivity.class); //The activity you  want to start.
        //Log.d("Alarm receiver", "received intent");
        Random random = new Random();
        int value = random.nextInt(1);
        Intent emaIntent = new Intent(context, PopupActivity.class);
        emaIntent.putExtra("pos", id);



//        if (value == 1) {
//            emaIntent = new Intent(context, PopupActivity.class); //The activity you  want to start.
//        } else {
//            emaIntent = new Intent(context, VoiceRecognitionActivity.class);
//        }



        emaIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(emaIntent);
    }
}
