//package edu.dartmouth.cs.moodyalarm;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ListView;
//
//import java.io.InputStream;
//import java.util.ArrayList;
//
///**
// * Created by vivianjiang on 2/25/18.
// */
//
//public class old extends Fragment implements View.OnClickListener {
//
//    public View view;
//    Button btn;
//    public static int day;
//
//    public static ArrayList<SpotifyPlaylist> playlists;
//    private ListView listView;
//    @Nullable
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//
//        view = inflater.inflate(R.layout.weekday, container, false);
//
//        super.onCreate(savedInstanceState);
//        Button monday = view.findViewById(R.id.monday);
//        monday.setOnClickListener(this);
//        Button tuesday = view.findViewById(R.id.tuesday);
//        tuesday.setOnClickListener(this);
//        Button wednesday = view.findViewById(R.id.wednesday);
//        wednesday.setOnClickListener(this);
//        Button thursday = view.findViewById(R.id.thursday);
//        thursday.setOnClickListener(this);
//        Button friday = view.findViewById(R.id.friday);
//        friday.setOnClickListener(this);
//        Button saturday= view.findViewById(R.id.saturday);
//        saturday.setOnClickListener(this);
//        Button sunday= view.findViewById(R.id.sunday);
//        sunday.setOnClickListener(this);
//
//        return view;
//    }
//
//
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        //you can set the title for your toolbar here for different fragments different titles
//        getActivity().setTitle("Alarm Settings");
//    }
//
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.monday:
//                showPlaylistDialog(v, R.id.monday);
//                // do something
//                break;
//            case R.id.tuesday:
//                // do something else
//                showPlaylistDialog(v, R.id.tuesday);
//                break;
//            case R.id.wednesday:
//                // i'm lazy, do nothing
//                showPlaylistDialog(v, R.id.wednesday);
//                break;
//            case R.id.thursday:
//                // i'm lazy, do nothing
//                showPlaylistDialog(v, R.id.thursday);
//                break;
//            case R.id.friday:
//                // i'm lazy, do nothing
//                showPlaylistDialog(v, R.id.friday);
//                break;
//            case R.id.saturday:
//                // i'm lazy, do nothing
//                showPlaylistDialog(v, R.id.saturday);
//                break;
//            case R.id.sunday:
//                // i'm lazy, do nothing
//                showPlaylistDialog(v, R.id.sunday);
//        }
//    }
//
//
//    public void showPlaylistDialog(View v, int id) {
//        DialogFragment fragment = new SpotifySettings();
//        day = id;
//
//        fragment.show(getFragmentManager(), "playlistPicker");
//    }
//
//
//}
//
//
