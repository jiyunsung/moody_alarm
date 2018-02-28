package edu.dartmouth.cs.moodyalarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    public ArrayList<SpotifyEntry> spotifyEntries;





    Button btn;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view=inflater.inflate(R.layout.spotify_settings,null);


        super.onCreate(savedInstanceState);


        new SpotifyAsyncTask().execute();


        spotifyEntries = new ArrayList<SpotifyEntry>();

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



    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Spotify Settings");
    }

//    public void showEditPrefsDialog(View v) {
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getActivity());
//        LayoutInflater inflater = this.getLayoutInflater();
//        final View dialogView = inflater.inflate(R.layout.edit_playlist_preferences_dialog, null);
//        dialogBuilder.setView(dialogView);
//
//        final AlertDialog b = dialogBuilder.create();
//        b.show();
//
//        CheckBox weather = dialogView.findViewById(R.id.useWeather);
//        weather.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v)
//            {
//                Log.d("onclick", "weather checkbox pressed");
//                Fragment fragment = new Weather();
//                if (fragment != null) {
//                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                    ft.replace(R.id.content_frame, fragment);
//                    ft.commit();
//                }
//                b.cancel();
//                //showEditPrefsDialog(v);
//
//            }
//        });
//
//        CheckBox weekDay = dialogView.findViewById(R.id.useWeekday);
//        weekDay.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v)
//            {
//                Log.d("onclick", "weekDay checkbox pressed");
//                //showEditPrefsDialog(v);
//                Fragment fragment = new Day();
//                if (fragment != null) {
//                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                    ft.replace(R.id.content_frame, fragment);
//                    ft.commit();
//                }
//                b.cancel();
//
//            }
//        });
//    }



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
                imageView.setLayoutParams(new GridView.LayoutParams(325, 325));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(0, 0, 0, 0);
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


    public void fetchDefaultPlaylists(Context input){

        RequestQueue queue = Volley.newRequestQueue(input);

        //String url = "https://api.spotify.com/v1/browse/categories/mood/playlists?limit=50";

        final String [] default_playlists = new String[9];

        default_playlists[0] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DWSiZVO2J6WeI";
        default_playlists[1] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DX5Q5wA1hY6bS";
        default_playlists[2] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DWUNIrSzKgQbP";
        default_playlists[3] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DX3YSRoSdA634";
        default_playlists[4] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DX6ALfRKlHn1t";
        default_playlists[5] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DXbvABJXBIyiY";
        default_playlists[6] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DWVV27DiNWxkR";
        default_playlists[7] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DWU0ScTcjJBdj";
        default_playlists[8] = "https://api.spotify.com/v1/users/spotify/playlists/37i9dQZF1DX3rxVfibe1L0";


        final ArrayList<String> imageUrls = new ArrayList<String>();
        Log.d("Spotify service", "in fetch default playlists access token is "+ MainActivity.accessToken);


        for (int i = 0; i< default_playlists.length;i++) {


            StringRequest jsObjRequest = new StringRequest
                    (Request.Method.GET, default_playlists[i], new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Response", response);
                            try {

                                JSONObject jsonObject = new JSONObject(response);

                                String id = jsonObject.getString("id");

                                JSONArray arr = jsonObject.getJSONArray("images");
                                for (int i = 0; i < arr.length(); i++)
                                {
                                    String imageUrl= arr.getJSONObject(i).getString("url");
                                    Log.d("image url", imageUrl);
                                    SpotifyEntry entry = new SpotifyEntry();
                                    entry.setPlaylistId(id);
                                    entry.setImageUrl(imageUrl);

                                    spotifyEntries.add(entry);
                                    imageUrls.add(imageUrl.toString());
                                    Log.d("imageUrls size: ", "size is " + imageUrls.size());

                                    if(spotifyEntries.size() == default_playlists.length){
                                        Log.d("spotify service", "imageURls size equals default");
                                        try {
                                            new SpotifyAsyncSave().execute(imageUrls);
                                        } catch(Exception e){
                                            Log.d("error", e.toString());
                                        }

                                    }

                                }

                            } catch (JSONException e) {
                                Log.d("json error", e.toString());
                            }


                        }
                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO Auto-generated method stub
                                    Log.d("ERROR", "error => " + error.toString());
                                }
                            }
                    ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer " + MainActivity.accessToken);
                    params.put("Accept", "application/json");

                    return params;
                }
            };

            queue.add(jsObjRequest);
        }



    }


    private class SpotifyAsyncSave extends AsyncTask<ArrayList<String>, Void, ArrayList<String>> {

        // ui calling possible
        protected void onPreExecute() {

        }

        // run threads
        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... params) {
            ArrayList<String> playlists = params[0];


            EntryDbHelper dataStorage= new EntryDbHelper(getActivity().getApplicationContext());
            dataStorage.open();
            for (int i = 0; i < spotifyEntries.size(); i++){

                dataStorage.insertSpotifyEntry(spotifyEntries.get(i));
            }



            return playlists;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {

            loading.setText("");
            ImageAdapter adapter = new ImageAdapter(getActivity(), result);

            gridview.setAdapter(adapter);
            gridview.setOnItemClickListener(SpotifySettings.this);
            adapter.notifyDataSetChanged();
            //dataStorage.close();
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

            ArrayList<SpotifyEntry> spotifyEntries = dataStorage.fetchSpotifyEntries();

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
            if (result.size() != NUMBER_DEFAULT_PLAYLISTS){
                fetchDefaultPlaylists(getActivity().getApplicationContext());
            } else{
                Log.d("results complete", "resetting adapter");

                loading.setText("");
                ImageAdapter adapter = new ImageAdapter(getActivity(), result);

                gridview.setAdapter(adapter);
                gridview.setOnItemClickListener(SpotifySettings.this);
                adapter.notifyDataSetChanged();
            }
        }

    }

}