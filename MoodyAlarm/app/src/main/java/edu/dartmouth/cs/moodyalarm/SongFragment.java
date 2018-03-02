package edu.dartmouth.cs.moodyalarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vivianjiang on 2/25/18.
 */


public class SongFragment extends DialogFragment{

    public View view;

    private TextView loading;

    public ArrayList<SpotifyPlaylist> spotifyEntries;



    Button btn;
    static SongFragment newInstance(String data) {
        SongFragment s = new SongFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("data", data);
        s.setArguments(args);

        return s;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view=inflater.inflate(R.layout.song,null);


        super.onCreate(savedInstanceState);



        String data = getArguments().getString("data");

        try {

            JSONObject jsonObject = new JSONObject(data);


            JSONObject track = jsonObject.getJSONObject("track");
            JSONObject album = track.getJSONObject("album");

            String songName = album.getString("name");
            Log.d("song name is ", songName);

            JSONArray artists = album.getJSONArray("artists");
            JSONObject artist = artists.getJSONObject(0);
            String artistName = artist.getString("name");

            JSONArray images = album.getJSONArray("images");
            JSONObject image = images.getJSONObject(0);
            String imageUrl = image.getString("url");
            Log.d("image url is ", imageUrl);

            String songUri = album.getString("uri");
            Log.d("song uri is ", songUri);


            TextView song_name = view.findViewById(R.id.song_name);
            song_name.setText(songName);

            TextView song_artist = view.findViewById(R.id.artist_name);
            song_artist.setText(artistName);

            ImageView albumPhoto = view.findViewById(R.id.album_photo);
            Picasso.with(getActivity().getApplicationContext()).
                    load(imageUrl).into(albumPhoto);

            MainActivity.mPlayer.playUri(null, songUri, 0, 0);


        } catch(JSONException e){Log.d("error", e.toString());}



        builder.setView(view);


        Dialog dialog = builder.create();
        return dialog;

    }


    @Override
    public void onResume(){
        super.onResume();
        Log.d("song fragment", "on resume");


    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d("song fragment", "on pause");
        MainActivity.mPlayer.pause(null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Spotify Settings");
    }



}