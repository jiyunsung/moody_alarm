package edu.dartmouth.cs.moodyalarm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by vivianjiang on 2/25/18.
 */

public class Weather extends Fragment implements View.OnClickListener {

    public View view;
    Button btn;
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.weather, container, false);

        super.onCreate(savedInstanceState);

        Button sunny = view.findViewById(R.id.sunny);
        sunny.setOnClickListener(this);
        Button rainy = view.findViewById(R.id.rainy);
        rainy.setOnClickListener(this);
        Button cloudy = view.findViewById(R.id.cloudy);
        cloudy.setOnClickListener(this);
        Button foggy = view.findViewById(R.id.foggy);
        foggy.setOnClickListener(this);
        Button windy = view.findViewById(R.id.windy);
        windy.setOnClickListener(this);
        Button snowy= view.findViewById(R.id.snowy);
        snowy.setOnClickListener(this);



        return view;
    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Alarm Settings");
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sunny:
                showPlaylistDialog(v, R.id.sunny);
                // do something
                break;
            case R.id.rainy:
                // do something else
                showPlaylistDialog(v, R.id.rainy);
                break;
            case R.id.cloudy:
                // i'm lazy, do nothing
                showPlaylistDialog(v, R.id.cloudy);
                break;
            case R.id.snowy:
                // i'm lazy, do nothing
                showPlaylistDialog(v, R.id.snowy);
                break;
            case R.id.windy:
                // i'm lazy, do nothing
                showPlaylistDialog(v, R.id.windy);
                break;
            case R.id.foggy:
                // i'm lazy, do nothing
                showPlaylistDialog(v, R.id.foggy);
                break;
        }
    }


    public void showPlaylistDialog(View v, int id) {
        DialogFragment fragment = new SpotifySettings();

        fragment.show(getFragmentManager(), "playlistPicker");
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}

