package edu.dartmouth.cs.moodyalarm;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.text.format.Time;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import be.billington.calendar.recurrencepicker.EventRecurrence;
import be.billington.calendar.recurrencepicker.EventRecurrenceFormatter;
import be.billington.calendar.recurrencepicker.RecurrencePickerDialog;

import java.util.Date;

/**
 * Created by jiyunsung on 2/25/18.
 */

public class SetAlarmActivity extends AppCompatActivity {

    TimePicker alarmTimePicker;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    private AlarmEntryDbHelper dataStorage;
    private boolean isNew;
    public AlarmEntry alarmEntry = new AlarmEntry();
    private TextView recurrence;
    private String recurrenceRule;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmTimePicker.setIs24HourView(true);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = getIntent();
        isNew = intent.getBooleanExtra(AlarmsFragment.NEWALARM, true);
        if (!isNew) {
            alarmEntry = (AlarmEntry) intent.getSerializableExtra(AlarmsFragment.POSITION);
            alarmTimePicker.setCurrentHour(alarmEntry.getHour());
            alarmTimePicker.setCurrentMinute(alarmEntry.getMinute());
        }

        recurrence = (TextView) findViewById(R.id.recurrence);


        recurrence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecurrencePickerDialog recurrencePickerDialog = new RecurrencePickerDialog();

                if (recurrenceRule != null && recurrenceRule.length() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString(RecurrencePickerDialog.BUNDLE_RRULE, recurrenceRule);
                    recurrencePickerDialog.setArguments(bundle);
                }

                recurrencePickerDialog.setOnRecurrenceSetListener(new RecurrencePickerDialog.OnRecurrenceSetListener() {
                    @Override
                    public void onRecurrenceSet(String rrule) {
                        recurrenceRule = rrule;

                        if (recurrenceRule != null && recurrenceRule.length() > 0) {
                            EventRecurrence recurrenceEvent = new EventRecurrence();
                            recurrenceEvent.setStartDate(new Time("" + new Date().getTime()));
                            recurrenceEvent.parse(rrule);
                            String srt = EventRecurrenceFormatter.getRepeatString(SetAlarmActivity.this, getResources(), recurrenceEvent, true);
                            recurrence.setText(srt);
                        } else {
                            recurrence.setText("No recurrence");
                        }
                    }
                });
                recurrencePickerDialog.show(getSupportFragmentManager(), "recurrencePicker");
            }
        });
    }

    public void OnToggleClicked(View view)
    {
        long time;
        if (isNew) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
            Intent intent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
            if(System.currentTimeMillis()>time)
            {
                if (calendar.AM_PM == 0)
                    time = time + (1000*60*60*12);
                else
                    time = time + (1000*60*60*24);
            }
        } else {

        }

        if (((ToggleButton) view).isChecked())
        {
            Toast.makeText(SetAlarmActivity.this, "ALARM ON", Toast.LENGTH_SHORT).show();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
            Intent intent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            time=(calendar.getTimeInMillis()-(calendar.getTimeInMillis()%60000));
            if(System.currentTimeMillis()>time)
            {
                if (calendar.AM_PM == 0)
                    time = time + (1000*60*60*12);
                else
                    time = time + (1000*60*60*24);
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);
        }
        else
        {
            alarmManager.cancel(pendingIntent);
            Toast.makeText(SetAlarmActivity.this, "ALARM OFF", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveButtonClicked(View view){

        if (isNew) {

            new writeSchema().execute();

            Context context = getApplicationContext();
            CharSequence text = "Saved";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {

            new updateSchema().execute();

        }

        finish();

    }
    public void cancelButtonClicked(View view){
        finish();
    }

    private class writeSchema extends AsyncTask<Void, Void, Void> {

        // ui calling possible
        protected void onPreExecute() {

        }

        // run threads
        @Override
        protected Void doInBackground(Void... arg0) {
            alarmEntry.setHour(alarmTimePicker.getCurrentHour());
            alarmEntry.setMinute(alarmTimePicker.getCurrentMinute());
            alarmEntry.setOnOff(1);
            alarmEntry.setRepeat(1);
            alarmEntry.setDaysofweek(new ArrayList<>(Arrays.asList(false, false, false, false, true, true, true)));

            dataStorage= new AlarmEntryDbHelper(getApplicationContext());
            dataStorage.open();
            dataStorage.insertEntry(alarmEntry);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }

    }

    private class updateSchema extends AsyncTask<Void, Void, Void> {

        // ui calling possible
        protected void onPreExecute() {

        }

        // run threads
        @Override
        protected Void doInBackground(Void... arg0) {
            alarmEntry.setHour(alarmTimePicker.getCurrentHour());
            alarmEntry.setMinute(alarmTimePicker.getCurrentMinute());
            alarmEntry.setOnOff(1);
            alarmEntry.setRepeat(1);
            alarmEntry.setDaysofweek(new ArrayList<>(Arrays.asList(false, false, false, false, true, true, true)));

            dataStorage= new AlarmEntryDbHelper(getApplicationContext());
            dataStorage.open();
            dataStorage.updateEntry(alarmEntry);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }

    }
}
