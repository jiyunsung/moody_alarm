package edu.dartmouth.cs.moodyalarm;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import java.util.ArrayList;


import android.support.annotation.Nullable;

/**
 * Created by jiyunsung on 2/25/18.
 */

public class AlarmsFragment extends Fragment {

    private EntryDbHelper dataStorage;
    private ListView listView;
    private AlarmsAdapter adapter;

    public static final String POSITION = "POS";
    public static final String NEWALARM = "alarm";
    public static final String[] DAYS = new String[] {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.display_alarms, container, false);

        dataStorage = new EntryDbHelper(getActivity());
        dataStorage.open();

        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new AlarmsAdapter(getActivity(), dataStorage.fetchAlarmEntries());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(updateAlarm);

        return view;

    }

    @Override
    public void onResume(){
        super.onResume();
        new loadSchema().execute();
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Alarms");
    }

    private AdapterView.OnItemClickListener updateAlarm = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int i, long l) {

            AlarmEntry entry = adapter.getEntries().get(i);

            Intent intent = new Intent(getActivity(), SetAlarmActivity.class);
            intent.putExtra(NEWALARM, false);
            intent.putExtra(POSITION, entry);
            startActivity(intent);

        }
    };

    private class loadSchema extends AsyncTask<Void, Void, Void> {
        ArrayList<AlarmEntry> entries;

        // ui calling possible
        protected void onPreExecute() {

        }

        // no ui from this one
        @Override
        protected Void doInBackground(Void... arg0) {
            entries = dataStorage.fetchAlarmEntries();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // apply to list adapter
            adapter = new AlarmsAdapter(getActivity(), entries);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(updateAlarm);
        }

    }

    public class AlarmsAdapter extends ArrayAdapter<AlarmEntry> {

        private final Activity _context;
        private final ArrayList<AlarmEntry> entries;

        public class ViewHolder {
            TextView Row0;
            TextView Label;
            TextView Row2;
            Switch OnOff;
        }

        public AlarmsAdapter(Activity context, ArrayList<AlarmEntry> entries) {
            super(context, R.layout.row_switch_layout, R.id.row_id, entries);
            this._context = context;
            this.entries = entries;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;

            if (convertView == null) {
                LayoutInflater inflater = _context.getLayoutInflater();
                view = inflater.inflate(R.layout.row_switch_layout, parent, false);

                final  ViewHolder holder = new ViewHolder();

                holder.Row0 = (TextView) view.findViewById(R.id.row0);
                holder.Label = (TextView) view.findViewById(R.id.label);
                holder.Label.setTypeface(null, Typeface.BOLD);
                holder.Row2 = (TextView) view.findViewById(R.id.row2);
                holder.OnOff = (Switch) view.findViewById(R.id.switchOnOff);

                holder.OnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        AlarmEntry element = (AlarmEntry) holder.OnOff.getTag();

                        if (isChecked) {
                            element.setOnOff(1);
                            element.setSchedule(getActivity());
                            Log.d("TAG", "Set ON!!!");
                        }
                        else {
                            element.setOnOff(0);
                            element.cancelSchedule(getActivity());
                            Log.d("TAG", "SET OFF!");
                        }

                        dataStorage.updateAlarmEntry(element);
                    }
                });
                view.setTag(holder);
                holder.OnOff.setTag(entries.get(position));

            } else {
                view = convertView;
                ((ViewHolder) view.getTag()).OnOff.setTag(entries.get(position));
            }



            ViewHolder holder = (ViewHolder) view.getTag();
            holder.Label.setText(Integer.toString(entries.get(position).getHour()) + ":" + Integer.toString(entries.get(position).getMinute()));

            if (entries.get(position).getOnOff() == 1)
                holder.OnOff.setChecked(true);
            else
                holder.OnOff.setChecked(false);

            if (entries.get(position).getRepeated() == 1) {
                String days = "";
                int i = 0;
                for (boolean day : entries.get(position).getDaysofweek()) {
                    if (day) {
                        if (days.equals(""))
                            days += DAYS[i];
                        else
                            days += ", " + DAYS[i];
                    }
                    i++;
                }
                holder.Row2.setText(days);
            } else if (entries.get(position).getRepeated() == 0) {
                holder.Row2.setText("No Recurrence");
            }

            return view;
        }

        public ArrayList<AlarmEntry> getEntries() {
            return entries;
        }

    }

}

