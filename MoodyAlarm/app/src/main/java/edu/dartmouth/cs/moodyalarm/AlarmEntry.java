package edu.dartmouth.cs.moodyalarm;

import android.content.Intent;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by jiyunsung on 2/25/18.
 */

public class AlarmEntry implements Serializable {

    private Long id;
    private Integer onOff;
    private Integer hour;
    private Integer minute;
    private Integer repeat;
    private ArrayList<Boolean> daysofweek;


    public AlarmEntry(){

    }

    public AlarmEntry(long id, Integer onOff, Integer hour, Integer minute, Integer repeat, ArrayList<Boolean> list) {
        this.id = id;
        this.onOff = onOff;
        this.hour = hour;
        this.minute = minute;
        this.repeat = repeat;
        this.daysofweek = list;
    }

    public long getId() { return id; }
    public int getOnOff() { return onOff; }
    public int getHour() { return hour; }
    public int getMinute() { return minute; }
    public int  getRepeated() { return repeat; }
    public ArrayList<Boolean> getDaysofweek() { return daysofweek; }

    public void setId(long id) {
        this.id = id;
    }
    public void setOnOff(int onOff) { this.onOff = onOff; }
    public void setHour(int hour) { this.hour = hour; }
    public void setMinute(int minute) {  this.minute = minute; }
    public void setRepeat(int repeat) { this.repeat = repeat; }
    public void setDaysofweek(ArrayList<Boolean> daysofweek) { this.daysofweek = daysofweek; }

}