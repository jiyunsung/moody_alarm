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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
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


public class SpotifySettings extends DialogFragment implements AdapterView.OnItemClickListener{

    public View view;
    public String[] DEFAULT_PLAYLISTS;
    private final int NUMBER_DEFAULT_PLAYLISTS = 9;
    private GridView gridview;
    private TextView loading;
    private AnimationAdapter horizontalAdapter;
    RecyclerView horizontal_recycler_view;
    LinearLayoutManager horizontalLayoutManager;

    public ArrayList<SpotifyPlaylist> spotifyEntries;



    Button btn;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.spotify_settings, null);

        ArrayList<String> urls = new ArrayList<>();
        urls.add("https://i.scdn.co/image/7c543ab19dff5f50f187b0c0b0d50caf0ca34a76");
        urls.add("https://i.scdn.co/image/7c543ab19dff5f50f187b0c0b0d50caf0ca34a76");
        urls.add("https://i.scdn.co/image/7c543ab19dff5f50f187b0c0b0d50caf0ca34a76");
        urls.add("https://i.scdn.co/image/7c543ab19dff5f50f187b0c0b0d50caf0ca34a76");
        urls.add("https://i.scdn.co/image/7c543ab19dff5f50f187b0c0b0d50caf0ca34a76");
        urls.add("https://i.scdn.co/image/7c543ab19dff5f50f187b0c0b0d50caf0ca34a76");





       horizontal_recycler_view = view.findViewById(R.id.horizontal_recycler_view);

//        View view=inflater.inflate(R.layout.spotify_settings,null);
//
        new SpotifyAsyncTask().execute();

        spotifyEntries = new ArrayList<SpotifyPlaylist>();

        gridview = (GridView) view.findViewById(R.id.gridview);

        loading = (TextView) view.findViewById(R.id.loading);

        loading.setText("Loading playlists_default...");

        Log.d("oncreateview", "just set image adapter");

        builder.setView(view);


        Dialog dialog = builder.create();
        return dialog;

    }


    public void onItemClick(AdapterView<?> parent, View v,
                            int position, long id) {
        Toast.makeText(getActivity(), "" + position,
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), PlaylistDisplay.class);
        intent.putExtra("pos", position+1);
        startActivity(intent);
        dismiss();



    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("spotify settings", "on resume");


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Spotify Settings");
    }



    private class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<String> mUrls;
        public ImageAdapter(Context c, ArrayList<String> urls) {
            mContext = c;
            mUrls = urls;
        }

        public int getCount() {
            Log.d("image adapter: ", "size is: "+ mUrls.size());
            return mUrls.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d("getView", "in image adapter");
            ImageView imageView;
            if (convertView == null) {

                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(375, 375));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(0, 10, 0, 10);
                imageView.setAdjustViewBounds(true);
                //notifyDataSetChanged();
            } else {

                imageView = (ImageView) convertView;
                notifyDataSetChanged();
            }
            Log.d("imageadapter url: ", mUrls.get(position));

            Picasso.with(getActivity().getApplicationContext()).
                    load(mUrls.get(position)).into(imageView);


            return imageView;
        }

        // references to our images

    }


    private class SpotifyAsyncTask extends AsyncTask<Void, Void, ArrayList<ArrayList<String>>> {

        // ui calling possible
        protected void onPreExecute() {
            Log.d("onPreExecute", "spotifyasync task");
        }

        // run threads
        @Override
        protected ArrayList<ArrayList<String>> doInBackground(Void... params) {

            ArrayList<ArrayList<String>> mImageUrls = new ArrayList<ArrayList<String>>();

            ArrayList<SpotifyPlaylist> spotifyEntriesDefault = MainActivity.dataStorage.fetchSpotifyEntriesDefault();
            ArrayList<SpotifyPlaylist> spotifyEntriesUser = MainActivity.dataStorage.fetchSpotifyEntriesUser();

            Log.d("doInBackground", "entries size is "+ spotifyEntriesDefault.size());


            ArrayList<String> defaultUrls = new ArrayList<String>();
            Log.d("do in background", "fetching all entries");
            for (int i = 0; i < spotifyEntriesDefault.size(); i++){
                defaultUrls.add(spotifyEntriesDefault.get(i).getImageUrl());
                Log.d("default for loop", "img url is "+ spotifyEntriesDefault.get(i).getImageUrl());

            }

            ArrayList<String> userUrls = new ArrayList<String>();
            for (int i = 0; i < spotifyEntriesUser.size(); i++){
                userUrls.add(spotifyEntriesUser.get(i).getImageUrl());
                Log.d("user for loop", "img url is "+ spotifyEntriesUser.get(i).getImageUrl());

            }

            mImageUrls.add(defaultUrls);
            mImageUrls.add(userUrls);
            return mImageUrls;
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<String>> result) {
            Log.d("onPostExecute", "result length is "+result.size());


                loading.setText("");
                ImageAdapter adapter = new ImageAdapter(getActivity(), result.get(0));

                gridview.setAdapter(adapter);
                gridview.setOnItemClickListener(SpotifySettings.this);
                adapter.notifyDataSetChanged();

            horizontalAdapter=new AnimationAdapter(getContext(),result.get(1));
            horizontal_recycler_view.setAdapter(horizontalAdapter);
            horizontalAdapter.notifyDataSetChanged();

            horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            horizontal_recycler_view.setLayoutManager(horizontalLayoutManager);
            horizontal_recycler_view.setAdapter(horizontalAdapter);


        }

    }

}