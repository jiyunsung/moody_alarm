package edu.dartmouth.cs.moodyalarm;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
//    private TextView loading;
    private AnimationAdapter horizontalAdapter;
    RecyclerView horizontal_recycler_view;
    LinearLayoutManager horizontalLayoutManager;

    public ArrayList<SpotifyPlaylist> spotifyEntries;

    public String newUrl;
    public String setting;



    Button btn;

    static SpotifySettings newInstance(String setting) {
        SpotifySettings s = new SpotifySettings();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("settings", setting);
        s.setArguments(args);

        return s;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setting = getArguments().getString("settings");
        Log.d("spotify settings ", "setting is " + setting);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.spotify_settings, null);
        RelativeLayout v = view.findViewById(R.id.dialog_screen);


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
        //v.setOnDragListener(new DragEventListener());

//        loading = (TextView) view.findViewById(R.id.loading);
//
//        loading.setText("Loading playlists_default...");

        Log.d("oncreateview", "just set image adapter");

        builder.setView(view);


        Dialog dialog = builder.create();
        return dialog;

    }


    public void onItemClick(AdapterView<?> parent, View v,
                            int position, long id) {


        Intent intent = new Intent(getActivity(), PlaylistDisplay.class);
        intent.putExtra("pos", position+1);
        intent.putExtra("id", "spotify");
        intent.putExtra("settings", setting);
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
        private ArrayList<SpotifyPlaylist> mPlaylists;
        public ImageAdapter(Context c, ArrayList<SpotifyPlaylist> playlists) {
            mContext = c;
            mPlaylists = playlists;
        }

        public int getCount() {
            Log.d("image adapter: ", "size is: "+ mPlaylists.size());
            return mPlaylists.size();
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
                imageView.setOnDragListener(new DragEventListener(position));

                //notifyDataSetChanged();
            } else {

                imageView = (ImageView) convertView;
                notifyDataSetChanged();
            }

            String url = mPlaylists.get(position).getImageUrl();

            Log.d("imageadapter url: ", url);


                Picasso.with(getActivity().getApplicationContext()).
                        load(url).into(imageView);



            return imageView;
        }

        // references to our images

    }


    private class SpotifyAsyncTask extends AsyncTask<Void, Void, ArrayList<ArrayList<SpotifyPlaylist>>> {

        // ui calling possible
        protected void onPreExecute() {
            Log.d("onPreExecute", "spotifyasync task");
        }

        // run threads
        @Override
        protected ArrayList<ArrayList<SpotifyPlaylist>> doInBackground(Void... params) {

            ArrayList<ArrayList<SpotifyPlaylist>> playlists = new ArrayList<ArrayList<SpotifyPlaylist>>();

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

            playlists.add(spotifyEntriesDefault);
            playlists.add(spotifyEntriesUser);
            return playlists;
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList<SpotifyPlaylist>> result) {
            Log.d("onPostExecute", "result length is "+result.size());


//                loading.setText("");
                ImageAdapter adapter = new ImageAdapter(getActivity(), result.get(0));

                gridview.setAdapter(adapter);
                gridview.setOnItemClickListener(SpotifySettings.this);
                adapter.notifyDataSetChanged();

            horizontalAdapter=new AnimationAdapter(getContext(),result.get(1), setting);
            horizontal_recycler_view.setAdapter(horizontalAdapter);
            horizontalAdapter.notifyDataSetChanged();

            horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            horizontal_recycler_view.setLayoutManager(horizontalLayoutManager);
            horizontal_recycler_view.setAdapter(horizontalAdapter);



        }



    }


    protected class DragEventListener implements View.OnDragListener {
        int position;

        // This is the method that the system calls when it dispatches a drag event to the
        // listener.
        public DragEventListener(int pos){
            position = pos;
        }
        public boolean onDrag(View v, DragEvent event) {
           // Log.d("Drag event listener", "on drag triggered");

            // Defines a variable to store the action type for the incoming event
            final int action = event.getAction();
            View view = (View) event.getLocalState();
            ImageView img = (ImageView) v;

            // Handles each of the expected events
            switch(action) {

                case DragEvent.ACTION_DRAG_STARTED:
                    Log.d("ACTION DRAG STARTED", "view id is: " + view.getId());

                    ClipDescription description = event.getClipDescription();
                    //String url = description.toString();
                    Log.d("ACTION DRAG STARTED", description.toString());

                    break;

                case DragEvent.ACTION_DRAG_ENTERED:

                    Log.d("ACTION DRAG ENTERED", "view id is: " + view.getId());

                    // Applies a green tint to the View. Return true; the return value is ignored.

                    img.setColorFilter(new PorterDuffColorFilter(Color.LTGRAY, PorterDuff.Mode.LIGHTEN));
//
//                    // Invalidate the view to force a redraw in the new tint
//                    v.invalidate();
//
                    break;

                case DragEvent.ACTION_DRAG_LOCATION:

                    // Ignore the event
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    Log.d("ACTION DRAG EXITED", "view id is: " + view.getId());
                    img.setColorFilter(null);

                    // Re-sets the color tint to blue. Returns true; the return value is ignored.
//                    v.setColorFilter(Color.BLUE);
//
//                    // Invalidate the view to force a redraw in the new tint
//                    v.invalidate();

                    break;

                case DragEvent.ACTION_DROP:
                    Log.d("ACTION drop", "view id is: " + view.getId());

                    ClipData data = event.getClipData();
                    ClipData.Item item = data.getItemAt(0);

                    Intent intent = item.getIntent();

                    SpotifyPlaylist user_playlist= (SpotifyPlaylist) intent.getSerializableExtra("playlist");
                    String url = user_playlist.getImageUrl();
                    String playlistId = user_playlist.getPlaylistId();
//
//                    Log.d("ACTION DROP", "before data switching");
//                    Log.d("ACTION DROP", "user playlist url is " + url);
//                    Log.d("ACTION DROP", "user playlist id is " + playlistId);
//                    Log.d("ACTION DROP", "user playlist db id is " + user_playlist.getId());
//                    Log.d("ACTION DROP", "user playlist name is " + user_playlist.getPlaylistName());


                    SpotifyPlaylist default_playlist = MainActivity.dataStorage.fetchEntryByIndexSpotifyDefault(position + 1);
//                    Log.d("ACTION DROP", "default playlist db id is " + default_playlist.getId());
//                    Log.d("ACTION DROP", "default playlist id is " + default_playlist.getPlaylistId());
//                    Log.d("ACTION DROP", "default playlist url is " + default_playlist.getImageUrl());
//                    Log.d("ACTION DROP", "default playlist name is " + default_playlist.getPlaylistName());
//
//
//
//                    Log.d("ACTION DROP", "default playlist id getting set to user playlist db id of" + user_playlist.getId());


                    default_playlist.setId(user_playlist.getId());
                    user_playlist.setId(position+1);

                    MainActivity.dataStorage.updateSpotifyEntryDefault(user_playlist);
                    MainActivity.dataStorage.updateSpotifyEntryUser(default_playlist);

//                    Log.d("ACTION DROP", "after data switching fetching updated entries");
                    default_playlist = MainActivity.dataStorage.fetchEntryByIndexSpotifyDefault(user_playlist.getId());
//                    Log.d("ACTION DROP", "default playlist db id is " + default_playlist.getId());
//                    Log.d("ACTION DROP", "default playlist id is " + default_playlist.getPlaylistId());
//                    Log.d("ACTION DROP", "default playlist url is " + default_playlist.getImageUrl());
//                    Log.d("ACTION DROP", "default playlist name is " + default_playlist.getPlaylistName());

                    user_playlist = MainActivity.dataStorage.fetchEntryByIndexSpotifyDefault(default_playlist.getId());
//                    Log.d("ACTION DROP", "user playlist db id is " + user_playlist.getId());
//                    Log.d("ACTION DROP", "user playlist id is " + user_playlist.getPlaylistId());
//                    Log.d("ACTION DROP", "user playlist url is " + user_playlist.getImageUrl());
//                    Log.d("ACTION DROP", "user playlist name is " + user_playlist.getPlaylistName());


                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);

                    Picasso.with(getActivity().getApplicationContext()).
                            load(url).into(img);
                    newUrl = url;

                    img.invalidate();


                    img.setVisibility(View.VISIBLE);
                    new SpotifyAsyncTask().execute();



                    // Returns true. DragEvent.getResult() will return true.
                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    Log.d("ACTION DRAG ENDED", "view id is: " + view.getId());
                    view.setVisibility(View.VISIBLE);

                    // returns true; the value is ignored.
                    break;

                // An unknown action type was received.
                default:
                    Log.e("DragDrop Example","Unknown action type received by OnDragListener.");
                    break;
            }

            return true;
        }
    };

}