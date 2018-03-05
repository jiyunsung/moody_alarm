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

    public AlarmEntry(){
    }

    public AlarmEntry(long id, Integer onOff, Integer hour, Integer minute, Integer repeat, ArrayList<Boolean> list, String s, Integer v) {
        this.id = id;
        this.onOff = onOff; // 1 if on
        this.hour = hour;
        this.minute = minute;
        this.repeat = repeat;
        this.daysofweek = list;
        this.setting = s;
        this.vibrate = v;
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

    public String getSetting() { return setting; }
    public void setSetting(String s) {this.setting = s;}
    public void setVibrate(int v) {this.vibrate = v; }

    public void cancelSchedule(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(Long.toString(System.currentTimeMillis()));

        // the request code distinguish different stress meter schedule instances
        int requestCode = this.hour * 10000 + this.minute * 100 + this.repeat;

        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT); //set pending intent to call AlarmReceiver.

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
    }

    // a method to set schedule based on the alarm entry
    public void setSchedule(Context context) {

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(Long.toString(System.currentTimeMillis()));
        intent.putExtra("alarm", this.id);
        Log.d("setSchedule", "setting is " + this.setting);
        Log.d("setSchedule", "id is " + this.id);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // check if alarm is on
        if (this.onOff == 1) { // alarm on

            Log.d("alarmEntry set schedule", "alarm is on");

            if (this.repeat == 0) { // alarm is on but does not repeat

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, this.hour);
                calendar.set(Calendar.MINUTE, this.minute);

                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.DATE, 1);
                }

                // the request code distinguish different stress meter schedule instances
                int requestCode = this.hour * 10000 + this.minute * 100 + this.repeat * 10;

                PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT); //set pending intent to call AlarmReceiver.

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
            }
            //set repeating alarm, and pass the pending intent,
            //so that the broadcast is sent everytime the alarm
            // is triggered
            else if (this.repeat == 2) { // daily repeats

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, this.hour);
                calendar.set(Calendar.MINUTE, this.minute);

                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.DATE, 1);
                }

                // the request code distinguish different stress meter schedule instances
                int requestCode = this.hour * 10000 + this.minute * 100 + this.repeat * 10;

                PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT); //set pending intent to call AlarmReceiver.

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pi);

            } else if (this.repeat == 1) { // weekly

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
                                PendingIntent.FLAG_CANCEL_CURRENT); //set pending intent to call AlarmReceiver.

                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, date.getTimeInMillis(),
                                AlarmManager.INTERVAL_DAY * 7, pi);
                    }

                    i++;
                }
            }
        }



    }

    public void setSnooze(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(Long.toString(System.currentTimeMillis()));
        intent.putExtra("alarm", this.id);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
    }


}
