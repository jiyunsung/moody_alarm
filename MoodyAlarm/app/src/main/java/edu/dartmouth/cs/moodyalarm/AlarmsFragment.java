package edu.dartmouth.cs.moodyalarm;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;

import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.Inflater;


import android.support.annotation.Nullable;

import com.squareup.picasso.Picasso;

/**
 * Created by jiyunsung on 2/25/18.
 */



public class AlarmsFragment extends Fragment implements OnStartDragListener {


    private ListView listView;
    private RecyclerView recyclerView;
    private AlarmsAdapter adapter;
    LinearLayoutManager verticalLayoutManager;

    private Handler mDelayedTransactionHandler = new Handler();
    private Runnable mRunnable;



    public static final String POSITION = "POS";
    public static final String NEWALARM = "alarm";
    public static final String[] DAYS = new String[] {"Sun", "Mon", "Tues", "Wed", "Thu", "Fri", "Sat"};

    private OnStartDragListener mDragListener;
    private ItemTouchHelper mItemTouchHelper;
    private GestureDetector gestureDetector;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.display_alarms, container, false);
        MainActivity.fab.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) view.findViewById(R.id.horizontal_recycler_view);
        new loadSchema().execute();
        //listView.setOnItemClickListener(updateAlarm);

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
        getActivity().setTitle("Alarmify");

        mDragListener = this;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }



    private class loadSchema extends AsyncTask<Void, Void, Void> {
        ArrayList<AlarmEntry> entries;

        // ui calling possible
        protected void onPreExecute() {

        }

        // no ui from this one
        @Override
        protected Void doInBackground(Void... arg0) {
            entries = MainActivity.dataStorage.fetchAlarmEntries();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // apply to list adapter

            adapter = new AlarmsAdapter(getActivity(), entries, mDragListener);
            Log.d("loadschema", "on post execute entries length is " + entries.size());


            verticalLayoutManager = new LinearLayoutManager(getContext());
            verticalLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(verticalLayoutManager);
            recyclerView.setAdapter(adapter);


            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
            mItemTouchHelper = new ItemTouchHelper(callback);
            mItemTouchHelper.attachToRecyclerView(recyclerView);
            adapter.notifyDataSetChanged();
            //recyclerView.setOnItemClickListener(updateAlarm);
        }

    }

    public class AlarmsAdapter extends RecyclerView.Adapter<AlarmsAdapter.MyViewHolder> implements ItemTouchHelperAdapter  {

        private Context context;
        private final ArrayList<AlarmEntry> entries;
        private View view;
        private View expandedView;
        private final OnStartDragListener mDragStartListener;


        public class MyViewHolder extends RecyclerView.ViewHolder implements
                ItemTouchHelperViewHolder{

            TextView Row0;
            TextView Label;
            TextView Row2;
            Switch OnOff;
            View v;
            CardView card_view;
            TextView ampm;


            public MyViewHolder(View view) {
                super(view);

                Row0 = (TextView) view.findViewById(R.id.row0);
                Label = (TextView) view.findViewById(R.id.label);

                Row2 = (TextView) view.findViewById(R.id.row2);
                OnOff = (Switch) view.findViewById(R.id.switchOnOff);
                v = view.findViewById(R.id.relative_layout);
                card_view = view.findViewById(R.id.card_view);

                ampm = view.findViewById(R.id.ampm);


            }
            @Override
            public void onItemSelected() {
                //itemView.setBackgroundColor(Color.LTGRAY);
            }

            @Override
            public void onItemClear() {
                itemView.setBackgroundColor(0);
            }


        }

//        public class ViewHolder {
//            TextView Row0;
//            TextView Label;
//            TextView Row2;
//            Switch OnOff;
//        }

        public AlarmsAdapter(Context context, ArrayList<AlarmEntry> entries,OnStartDragListener dragStartListener) {

            this.context = context;
            this.entries = entries;
            mDragStartListener = dragStartListener;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_switch_layout, parent, false);


            if (view.getLayoutParams ().width == RecyclerView.LayoutParams.MATCH_PARENT)
                view.getLayoutParams ().width = RecyclerView.LayoutParams.WRAP_CONTENT;

            return new MyViewHolder(view);
        }


        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            final AlarmEntry entry = entries.get(position);
            Log.d("onbindviewholder", "entry id is " + entry.getId());

            holder.OnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    AlarmEntry element = (AlarmEntry) holder.OnOff.getTag();
                    if(buttonView.isPressed()) {
                        Log.d("alarmsfragment", "onoff checked changed");
                        if (isChecked) {
                            element.setOnOff(1);
                            element.setSchedule(getActivity());
                            Log.d("TAG", "Set ON!!!");
                        } else {
                            element.setOnOff(0);
                            element.cancelSchedule(getActivity());
                            Log.d("TAG", "SET OFF!");
                        }

                        MainActivity.dataStorage.updateAlarmEntry(element);
                    }
                }
            });

            view.setTag(holder);
            holder.OnOff.setTag(entry);
            String minute="";
            if ((entry.getMinute() < 10)){
                minute = "0"+Integer.toString(entry.getMinute());
            } else{
                minute = Integer.toString(entry.getMinute());
            }
            String ampm="";
            if(entry.getHour() < 12){
                ampm="AM";
            }else{
                ampm="PM";
            }

            final String time = (entry.getHour() + ":" + minute);


            holder.Label.setText(time);
            holder.ampm.setText(ampm);
            if (entry.getOnOff() == 1)
                holder.OnOff.setChecked(true);
            else
                holder.OnOff.setChecked(false);

            if (entry.getRepeated() == 1) {
                String days = "";
                int i = 0;
                for (boolean day : entry.getDaysofweek()) {
                    if (day) {
                        if (days.equals(""))
                            days += DAYS[i];
                        else
                            days += ", " + DAYS[i];
                    }
                    i++;
                }
                holder.Row2.setText(days);
            } else if (entry.getRepeated() == 0) {
                // TODO : save calendar and display the date set
                holder.Row2.setText("Today");
            }

            ViewCompat.setTransitionName(holder.card_view, String.valueOf(position) + "_image");
            gestureDetector = new GestureDetector(getActivity(), new SingleTapConfirm());

            holder.card_view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        mDragStartListener.onStartDrag(holder);
                        return true;
                    }
                    return false;
                }


            });
            holder.card_view.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if(entry.getDaysofweek() != null){
                        Log.d("alarms fragment", "getDaysof week not null");
                    } else{
                        Log.d("alarms fragment", "getDaysof week  null");
                    }

                    AlarmDetailsDisplay alarmDetails = new AlarmDetailsDisplay().newInstance(time, entry, false);
                    //alarmDetails.setTargetFragment(AlarmsFragment.this,0);

                    // Note that we need the API version check here because the actual transition classes (e.g. Fade)
                    // are not in the support library and are only available in API 21+. The methods we are calling on the Fragment
                    // ARE available in the support library (though they don't do anything on API < 21)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        alarmDetails.setSharedElementEnterTransition(new DetailsTransition());
                        alarmDetails.setEnterTransition(new Fade());
                        setExitTransition(new Fade());
                        alarmDetails.setSharedElementReturnTransition(new DetailsTransition());
                    }

                    FragmentTransaction ft = getActivity().getSupportFragmentManager()
                            .beginTransaction();

                            ft.addSharedElement(holder.card_view, "expand")
                            .replace(R.id.content_frame, alarmDetails)
                            .addToBackStack(null)
                            .commit();
                    MainActivity.fab.setVisibility(View.INVISIBLE);


                }
            });

        }

        @Override
        public void onItemDismiss(int position) {
            entries.get(position).cancelSchedule(context);
            MainActivity.dataStorage.removeEntry(entries.get(position).getId());
            entries.remove(position);

            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
            new loadSchema().execute();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(entries, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(entries, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        public ArrayList<AlarmEntry> getEntries() {
            return entries;
        }

        @Override
        public int getItemCount() {

            Log.d("getItemCount", "entries size is " + entries.size());
            return entries.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 0) {
                String setting=data.getStringExtra("setting");
                Log.d("onActivityResult", "in alarms fragment setting is " + setting);
        }
    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

}

