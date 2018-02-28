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

    public ArrayList<SpotifyPlaylist> spotifyEntries;



    Button btn;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view=inflater.inflate(R.layout.spotify_settings,null);


        super.onCreate(savedInstanceState);


        new SpotifyAsyncTask().execute();


        spotifyEntries = new ArrayList<SpotifyPlaylist>();

        gridview = (GridView) view.findViewById(R.id.gridview);

        loading = (TextView) view.findViewById(R.id.loading);

        loading.setText("Loading playlists...");

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
                imageView.setLayoutParams(new GridView.LayoutParams(425, 425));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(0, 0, 0, 0);
                imageView.setAdjustViewBounds(true);
                //notifyDataSetChanged();
            } else {

                imageView = (ImageView) convertView;
                notifyDataSetChanged();
            }
            Log.d("imageadapter url: ", mUrls.get(position));

            new DownloadImageTask(imageView)
                    .execute(mUrls.get(position));

            return imageView;
        }

        // references to our images

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




    private class SpotifyAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {
        EntryDbHelper dataStorage;
        // ui calling possible
        protected void onPreExecute() {
            Log.d("onPreExecute", "spotifyasync task");
        }

        // run threads
        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            dataStorage= new EntryDbHelper(getActivity().getApplicationContext());
            dataStorage.open();

            ArrayList<SpotifyPlaylist> spotifyEntries = dataStorage.fetchSpotifyEntries();

            Log.d("doInBackground", "entries size is "+ spotifyEntries.size());


            ArrayList<String> mImageUrls = new ArrayList<String>();
            Log.d("do in background", "fetching all entries");
            for (int i = 0; i < spotifyEntries.size(); i++){
                mImageUrls.add(spotifyEntries.get(i).getImageUrl());
                Log.d("for loop", "img url is "+ spotifyEntries.get(i).getImageUrl());

            }


            return mImageUrls;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            Log.d("onPostExecute", "result length is "+result.size());


                loading.setText("");
                ImageAdapter adapter = new ImageAdapter(getActivity(), result);

                gridview.setAdapter(adapter);
                gridview.setOnItemClickListener(SpotifySettings.this);
                adapter.notifyDataSetChanged();

        }

    }

}