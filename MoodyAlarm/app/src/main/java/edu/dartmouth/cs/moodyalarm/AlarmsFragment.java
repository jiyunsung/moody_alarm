package edu.dartmouth.cs.moodyalarm;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Range;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jiyunsung on 2/25/18.
 */

public class AlarmsFragment extends Fragment {

    private AlarmEntryDbHelper dataStorage;
    private ListView listView;
    private AlarmsAdapter adapter;

    public static final String POSITION = "POS";
    public static final String NEWALARM = "alarm";
    public static final String[] DAYS = new String[] {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.display_alarms, container, false);

        dataStorage = new AlarmEntryDbHelper(getActivity());
        dataStorage.open();

        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new AlarmsAdapter(getActivity(), dataStorage.fetchEntries());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(updateAlarm);

        return view;

    }

    @Override
    public void onResume(){
        super.onResume();
        new loadSchema().execute();
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
            entries = dataStorage.fetchEntries();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // apply to list adapter
            adapter = new AlarmsAdapter(getActivity(), entries);
            listView.setAdapter(adapter);
        }

    }

    public class AlarmsAdapter extends ArrayAdapter<AlarmEntry> {

        private final Activity _context;
        private final ArrayList<AlarmEntry> entries;

        public class ViewHolder {
            TextView Row0;
            TextView Row1;
            TextView Row2;
        }

        public AlarmsAdapter(Activity context, ArrayList<AlarmEntry> entries) {
            super(context, R.layout.display_alarms, R.id.row_id, entries);
            this._context = context;
            this.entries = entries;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater inflater = _context.getLayoutInflater();
                convertView = inflater.inflate(R.layout.display_alarms, parent, false);

                holder = new ViewHolder();
                holder.Row0 = (TextView) convertView.findViewById(R.id.row0);
                holder.Row1 = (TextView) convertView.findViewById(R.id.row1);
                holder.Row1.setTypeface(null, Typeface.BOLD);
                holder.Row2 = (TextView) convertView.findViewById(R.id.row2);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.Row1.setText(Integer.toString(entries.get(position).getHour()) + ":" + Integer.toString(entries.get(position).getMinute()));
            String days = "";
            int i = 0;
            for (boolean day: entries.get(position).getDaysofweek()) {
                if (day) {
                    if (days.equals(""))
                        days += DAYS[i];
                    else
                        days += ", " + DAYS[i];
                }
                i++;
            }
            holder.Row2.setText(days);

            return convertView;
        }

        public ArrayList<AlarmEntry> getEntries() {
            return entries;
        }
    }
}
