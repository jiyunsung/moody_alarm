package edu.dartmouth.cs.moodyalarm;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
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
    private Boolean[] daysList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = getIntent();
        isNew = intent.getBooleanExtra(AlarmsFragment.NEWALARM, true);
        if (!isNew) {
            alarmEntry = (AlarmEntry) intent.getSerializableExtra(AlarmsFragment.POSITION);
            alarmTimePicker.setCurrentHour(alarmEntry.getHour());
            alarmTimePicker.setCurrentMinute(alarmEntry.getMinute());
        } else {
            alarmEntry = new AlarmEntry();
            alarmEntry.setOnOff(1);
        }

        recurrence = (TextView) findViewById(R.id.recurrence);
        daysList = new Boolean[]{false, false, false, false, false, false, false};

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
                            Log.d("TAG", recurrenceEvent.toString());
                            Log.d("TAG", rrule.toString());
                            String srt = EventRecurrenceFormatter.getRepeatString(SetAlarmActivity.this, getResources(), recurrenceEvent, true);
                            recurrence.setText(srt);

                            String[] separated = recurrenceEvent.toString().split(";");
                            String byDay = separated[separated.length - 1].split("=")[1];

                            if (byDay.contains("SU"))
                                daysList[0] = true;
                            if (byDay.contains("MO"))
                                daysList[1] = true;
                            if (byDay.contains("TU"))
                                daysList[2] = true;
                            if (byDay.contains("WE"))
                                daysList[3] = true;
                            if (byDay.contains("TH"))
                                daysList[4] = true;
                            if (byDay.contains("FR"))
                                daysList[5] = true;
                            if (byDay.contains("SA"))
                                daysList[6] = true;

                            alarmEntry.setRepeat(1);
                        } else {
                            recurrence.setText("No recurrence");
                            alarmEntry.setRepeat(0);
                        }
                    }
                });
                recurrencePickerDialog.show(getSupportFragmentManager(), "recurrencePicker");
            }
        });
    }

    // create the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);
        return true;
    }

    // delete button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_name) {

            if (!isNew) {

                dataStorage = new AlarmEntryDbHelper(this);
                dataStorage.open();
                dataStorage.removeEntry(alarmEntry.getId());
                finish();
                return true;
            } else {
                Context context = getApplicationContext();
                CharSequence text = "Not Saved Yet";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }

//        if (id == R.id.switchOnOff) {
//
//            Switch s = (Switch) findViewById(R.id.switchOnOff);
//
//            if (s.isChecked()) {
//
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
//                calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
//                Intent intent = new Intent(this, AlarmReceiver.class);
//                pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//
//                long time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
//                if (System.currentTimeMillis() > time) {
//                    if (calendar.AM_PM == 0)
//                        time = time + (1000 * 60 * 60 * 12);
//                    else
//                        time = time + (1000 * 60 * 60 * 24);
//                }
//                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);
//            } else {
//                alarmManager.cancel(pendingIntent);
//            }
//
//            return true;
//        }
        return super.onOptionsItemSelected(item);
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
            alarmEntry.setDaysofweek(new ArrayList<Boolean>(Arrays.asList(daysList)));

            dataStorage= new AlarmEntryDbHelper(getApplicationContext());
            dataStorage.open();
            dataStorage.insertEntry(alarmEntry);

            alarmEntry.setSchedule(getApplicationContext());
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

            if (daysList != null) {
                ArrayList<Boolean> daysOfWeek = new ArrayList<>();
                for (boolean day : daysList) {
                    daysOfWeek.add(day);
                }
                alarmEntry.setDaysofweek(daysOfWeek);
            }

            dataStorage= new AlarmEntryDbHelper(getApplicationContext());
            dataStorage.open();
            dataStorage.updateEntry(alarmEntry);

            alarmEntry.setSchedule(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }

    }

}
