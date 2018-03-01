package edu.dartmouth.cs.moodyalarm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Calendar;

public class PopupActivity extends AppCompatActivity {

    public static Alarmhandler alarm = new Alarmhandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        String uri = "";

        switch (day) {
            case Calendar.MONDAY:
                uri = playPlaylistByDay(1, this);

            case Calendar.TUESDAY:
                uri = playPlaylistByDay(2, this);

            case Calendar.WEDNESDAY:
                uri = playPlaylistByDay(3, this);

            case Calendar.THURSDAY:
                uri = playPlaylistByDay(4, this);

            case Calendar.FRIDAY:
                uri = playPlaylistByDay(5, this);

            case Calendar.SATURDAY:
                uri = playPlaylistByDay(6, this);

            case Calendar.SUNDAY:
                uri = playPlaylistByDay(7, this);
                // etc.
        }

        alarm.start_alert(this, uri);
    }

    @Override
    public void onBackPressed() {
        alarm.stop_alert(this);
        finish();
    }

    public String playPlaylistByDay(int id, Context context){
        String uri = "";
        Log.d("savesong asynctask", "day is wednesday");
        EntryDbHelper dataStorage = new EntryDbHelper(context);
        dataStorage.open();
        Day today = dataStorage.fetchEntryByIndexDay(id);
        SpotifyPlaylist todayPlaylist = today.getSpotifyPlaylist();
        Log.d("playlist id is ", todayPlaylist.getPlaylistId());
        String tracks = todayPlaylist.getTrackInfo();
        try {

            JSONObject jsonObject = new JSONObject(tracks);


            JSONArray arr = jsonObject.getJSONArray("items");

            JSONObject item = arr.getJSONObject(0);
            JSONObject track = item.getJSONObject("track");
            JSONObject album = track.getJSONObject("album");
            JSONArray images = album.getJSONArray("images");
            JSONObject image = images.getJSONObject(1);
            String imageUrl = image.getString("url");

            String songName = album.getString("name");
            Log.d("song name is ", songName);
            TextView song = findViewById(R.id.song_name);
            song.setText(songName);

            TextView day = findViewById(R.id.day);
            day.setText(today.getName());
            uri = album.getString("uri");


            ImageView songPhoto = findViewById(R.id.playlist_img);
            new DownloadImageTask(songPhoto)
                    .execute(imageUrl);

            Log.d("track uri is ", uri);

        } catch(JSONException e){Log.d("error", e.toString());}
        return uri;


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
