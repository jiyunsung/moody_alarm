package edu.dartmouth.cs.moodyalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by jiyunsung on 2/25/18.
 */

public class AlarmEntry implements Serializable {

    private Long id;
    private Integer onOff;
    private Integer hour;
    private Integer minute;
    private Integer repeat; // an indicator of recurrence. 0 if no repeat. 1 for weekly, 2 for daily, 3 for monthly, and 4 for yearly
    private ArrayList<Boolean> daysofweek; // only works with weekly alarms. otherwise just list of falses. length of 7.
    private String setting;
    private Integer vibrate;
    private String date;

    // number of times user clicked snooze. this doesn't have to be saved in the database because it needs to be reinitialized when the user turns off the app.
    private int snooze = 0;

    public AlarmEntry(){
    }

    public AlarmEntry(long id, Integer onOff, Integer hour, Integer minute, Integer repeat, ArrayList<Boolean> list, String s, Integer v, String d) {
        this.id = id;
        this.onOff = onOff; // 1 if on
        this.hour = hour;
        this.minute = minute;
        this.repeat = repeat;
        this.daysofweek = list;
        this.setting = s;
        this.vibrate = v;
        this.snooze = 0;
        this.date = d;
    }

    public long getId() { return id; }
    public int getOnOff() { return onOff; }
    public int getHour() { return hour; }
    public int getMinute() { return minute; }
    public int  getRepeated() { return repeat; }
    public Boolean[] getDaysofweek() {
        Boolean[] daysList = new Boolean[]{false, false, false, false, false, false, false};
        int i = 0;
        if (this.daysofweek != null) {
            for (boolean day : this.daysofweek) {
                daysList[i] = day;
                i++;
            }
        } else{
            Log.d("alarmentry", "days of week was null");
        }
        return daysList; }
    public ArrayList<Boolean> getDaysOfWeek2() {
        return this.daysofweek;
    }
    public int getVibrate() { return this.vibrate; }
    public int getSnooze() { return this.snooze; }
    public String getDate() { return this.date; }

    public void setId(long id) {
        this.id = id;
    }
    public void setOnOff(int onOff) { this.onOff = onOff; }
    public void setHour(int hour) { this.hour = hour; }
    public void setMinute(int minute) {  this.minute = minute; }
    public void setRepeat(int repeat) { this.repeat = repeat; }
    public void setDaysofweek(Boolean[] daysList) {
        this.daysofweek = new ArrayList<>();
        for (boolean day : daysList) {
            daysofweek.add(day);
        }
    }
    public void setDaysOfWeek2(ArrayList<Boolean> daysofweek) {
        this.daysofweek = daysofweek;
    }
    public String getSetting() { return this.setting; }

    public void setSetting(String s) {this.setting = s;}
    public void setVibrate(int v) {this.vibrate = v; }
    public void setDate(String d) { this.date = d; }

    public void cancelSchedule(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarm id", this.id);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Log.d("alarmEntry set schedule", "alarm is on");

        if (this.repeat == 0) { // canceling non-repeating alarm

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, this.hour);
            calendar.set(Calendar.MINUTE, this.minute);
            calendar.set(Calendar.SECOND, 0);

            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.DATE, 1);
            }

            // the request code distinguish different stress meter schedule instances
            int requestCode = this.hour * 10000 + this.minute * 100 + this.repeat * 10;

            PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent,
                    PendingIntent.FLAG_CANCEL_CURRENT); //set pending intent to call AlarmReceiver.

            alarmManager.cancel(pi);
            pi.cancel();

            //cancel weekly repeating alarm, and pass the pending intent
        }  else if (this.repeat == 1) {

            int i = 0;
            int dow; // day of week integer
            for (boolean day : this.daysofweek) {

                if (day) { // if this day of week is set to repeat
                    if (i == 0)
                        dow = 7; // since Sunday in Android coding is 7, From Monday to Saturday it is 1-6 respectively
                    else
                        dow = i;

                    Calendar date = Calendar.getInstance();
                    int diff = dow - date.get(Calendar.DAY_OF_WEEK);
                    if (diff < 0)
                        diff += 7;

                    date.add(Calendar.DAY_OF_MONTH, diff);

                    // the request code distinguish different stress meter schedule instances
                    int requestCode = this.hour * 10000 + this.minute * 100 + this.repeat * 10 + i;

                    PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent,
                            PendingIntent.FLAG_CANCEL_CURRENT); //set pending intent to call AlarmReceiver.

                    alarmManager.cancel(pi);
                    pi.cancel();
                }

                i++;
            }
        }
    }

    // a method to set schedule based on the alarm entry
    public void setSchedule(Context context) {

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarm id", this.id);
        Log.d("setSchedule", "setting is " + this.setting);
        Log.d("setSchedule", "id is " + this.id);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // check if alarm is on
        if (true) { // alarm on
            // if (this.onOff == 1) { // alarm on

            Log.d("alarmEntry set schedule", "alarm is on");

            if (this.repeat == 0) { // alarm is on but does not repeat

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, this.hour);
                calendar.set(Calendar.MINUTE, this.minute);
                calendar.set(Calendar.SECOND, 0);

                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.DATE, 1);
                }

                // the request code distinguish different stress meter schedule instances
                int requestCode = this.hour * 10000 + this.minute * 100 + this.repeat * 10;

                PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT); //set pending intent to call AlarmReceiver.

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);

            //set weekly repeating alarm, and pass the pending intent,
            //so that the broadcast is sent every time the alarm
            // is triggered
            }  else if (this.repeat == 1) {

                int i = 0;
                int dow; // day of week integer
                for (boolean day : this.daysofweek) {

                    if (day) { // if this day of week is set to repeat
                        if (i == 0)
                            dow = 7; // since Sunday in Android coding is 7, From Monday to Saturday it is 1-6 respectively
                        else
                            dow = i;

                        Calendar date = Calendar.getInstance();
                        int diff = dow - date.get(Calendar.DAY_OF_WEEK);
                        if (diff < 0) {
                            diff += 7;
                        }
                        date.add(Calendar.DAY_OF_MONTH, diff);

                        // the request code distinguish different stress meter schedule instances
                        int requestCode = this.hour * 10000 + this.minute * 100 + this.repeat * 10 + i;

                        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT); //set pending intent to call AlarmReceiver.

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(),
                                AlarmManager.INTERVAL_DAY * 7, pi);
                    }

                    i++;
                }
            }
        }
    }

    public void setSnooze(Context context, int snooze_length) {
        this.snooze += 1;
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarm id", this.id);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, snooze_length);
        calendar.set(Calendar.SECOND, 0);

        // the request code distinguish snooze
        int requestCode = MainActivity.SNOOZE_REQUESTCODE;

        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT); //set pending intent to call AlarmReceiver.

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
    }


}
