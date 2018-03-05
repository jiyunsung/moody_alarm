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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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
    AlarmManager alarmManager;
    private EntryDbHelper dataStorage;
    private boolean isNew;
    public AlarmEntry alarmEntry = new AlarmEntry();
    public Boolean[] daysList;
    private boolean saved;
    private LinearLayout weekdays;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d("oncreate", "in set alarm activity");
        setContentView(R.layout.activity_set_alarm);
        alarmTimePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = getIntent();
        isNew = intent.getBooleanExtra(AlarmsFragment.NEWALARM, true);
        saved = false;

        weekdays = (LinearLayout) findViewById(R.id.weekday);


        if (!isNew) {
            alarmEntry = (AlarmEntry) intent.getSerializableExtra(AlarmsFragment.POSITION);
            alarmTimePicker.setCurrentHour(alarmEntry.getHour());
            alarmTimePicker.setCurrentMinute(alarmEntry.getMinute());
            daysList = alarmEntry.getDaysofweek();
            CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_repeat);
            if (alarmEntry.getRepeated() == 1) {
                weekdays.setVisibility(View.VISIBLE);
                checkBox.setChecked(true);

                ToggleButton buttonSun = (ToggleButton) findViewById(R.id.buttonSun);
                buttonSun.setChecked(daysList[0]);
                ToggleButton buttonM = (ToggleButton) findViewById(R.id.buttonM);
                buttonM.setChecked(daysList[1]);
                ToggleButton buttonTue = (ToggleButton) findViewById(R.id.buttonTue);
                buttonTue.setChecked(daysList[2]);
                ToggleButton buttonW = (ToggleButton) findViewById(R.id.buttonW);
                buttonW.setChecked(daysList[3]);
                ToggleButton buttonThur = (ToggleButton) findViewById(R.id.buttonThur);
                buttonThur.setChecked(daysList[4]);
                ToggleButton buttonF = (ToggleButton) findViewById(R.id.buttonF);
                buttonF.setChecked(daysList[5]);
                ToggleButton buttonSat = (ToggleButton) findViewById(R.id.buttonSat);
                buttonSat.setChecked(daysList[6]);

            } else {
                weekdays.setVisibility(View.INVISIBLE);
                checkBox.setChecked(false);
            }
        } else {
            alarmEntry = new AlarmEntry();
            alarmEntry.setOnOff(1);
            alarmEntry.setRepeat(0);
            weekdays.setVisibility(View.INVISIBLE);
            daysList = new Boolean[]{false, false, false, false, false, false, false};
        }
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

                dataStorage = new EntryDbHelper(this);
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

        return super.onOptionsItemSelected(item);
    }

    public void saveButtonClicked(View view){

        if (!saved) {
            saved = true; // no double clicking!

            if (isNew) {
                Log.d("save button clicked", "new alarm");

                new writeSchema().execute();

                Context context = getApplicationContext();
                CharSequence text = "Saved";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } else {
                Log.d("save button clicked", "not new alarm");
                new updateSchema().execute();

            }
        }
        finish();


    }
    public void cancelButtonClicked(View view){
        finish();
    }

    public void voiceRecog(View view){

        Intent emaIntent = new Intent(this, PuzzleActivity.class); //The activity you  want to start.
        emaIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(emaIntent);
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
            alarmEntry.setDaysofweek(daysList);

            dataStorage= new EntryDbHelper(getApplicationContext());
            dataStorage.open();
            dataStorage.insertAlarmEntry(alarmEntry);
            Log.d("writeSchema", "do in background");

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
            alarmEntry.setDaysofweek(daysList);

            dataStorage= new EntryDbHelper(getApplicationContext());
            dataStorage.open();
            dataStorage.updateAlarmEntry(alarmEntry);

            alarmEntry.setSchedule(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }

    }

    public void onRepeatClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        if (checked) {
            alarmEntry.setRepeat(1);
            weekdays.setVisibility(View.VISIBLE);
        } else {
            alarmEntry.setRepeat(0);
            weekdays.setVisibility(View.INVISIBLE);
        }
    }

    public void onToggleClicked(View view) {

        switch(view.getId()) {
            case R.id.buttonSun:
                if(((ToggleButton) view).isChecked()) {
                    daysList[0] = true;
                } else {
                    daysList[0] = false;
                }
                break;
            case R.id.buttonM:
                if(((ToggleButton) view).isChecked()) {
                    daysList[1] = true;
                } else {
                    daysList[1] = false;
                }
                break;
            case R.id.buttonTue:
                if(((ToggleButton) view).isChecked()) {
                    daysList[2] = true;
                } else {
                    daysList[2] = false;
                }
                break;
            case R.id.buttonW:
                if(((ToggleButton) view).isChecked()) {
                    daysList[3] = true;
                } else {
                    daysList[3] = false;
                }
                break;
            case R.id.buttonThur:
                if(((ToggleButton) view).isChecked()) {
                    daysList[4] = true;
                } else {
                    daysList[4] = false;
                }
                break;
            case R.id.buttonF:
                if(((ToggleButton) view).isChecked()) {
                    daysList[5] = true;
                } else {
                    daysList[5] = false;
                }
                break;
            case R.id.buttonSat:
                if(((ToggleButton) view).isChecked()) {
                    daysList[6] = true;
                } else {
                    daysList[6] = false;
                }
                break;
        }
    }

}
