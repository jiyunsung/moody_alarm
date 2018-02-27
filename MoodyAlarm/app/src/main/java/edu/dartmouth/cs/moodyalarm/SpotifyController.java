package edu.dartmouth.cs.moodyalarm;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.*;

/**
 * Created by vivianjiang on 2/26/18.
 */

public class SpotifyController {

    private EntryDbHelper dataStorage;

    private String [] default_playlists;
    private Activity activity;
    
    public SpotifyController(Activity a){
        super();
        activity = a;
    }
    
    public void fetchDefaultPlaylists(Activity input){
        activity = input;

        RequestQueue queue = Volley.newRequestQueue(activity);

        //String url = "https://api.spotify.com/v1/browse/categories/mood/playlists?limit=50";
        default_playlists = new String[9];

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

        for (int i = 0; i< default_playlists.length;i++) {
            StringRequest jsObjRequest = new StringRequest
                    (Request.Method.GET, default_playlists[i], new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.d("Response", response);
                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                String images = jsonObject.getJSONArray("images").get(0).toString();
                                String[] imageFields = images.split(",");
                                String imageUrl = imageFields[1].split(" : ")[0].split("\":\"")[1];
                                imageUrl = imageUrl.replace("\\", "");
                                StringBuilder url = new StringBuilder(imageUrl);
                                url.deleteCharAt(url.length()-1);
                                Log.d("image url", url.toString());

                                imageUrls.add(url.toString());
                                Log.d("imageUrls size: ", "size is " + imageUrls.size());
                                if(imageUrls.size() == default_playlists.length){
                                    Log.d("spotify controller", "imageURls size equals default");
                                   new SpotifyAsyncSave().execute(imageUrls);

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

    private class SpotifyAsyncSave extends AsyncTask<ArrayList<String>, Void, Void> {

        // ui calling possible
        protected void onPreExecute() {

        }

        // run threads
        @Override
        protected Void doInBackground(ArrayList<String>... params) {
            ArrayList<String> playlists = params[0];
            dataStorage= new EntryDbHelper(activity);
            dataStorage.open();
            for (int i = 0; i < playlists.size(); i++){
                SpotifyEntry entry = new SpotifyEntry();
                Log.d("spotifyentry", "async task do in background url is "+ playlists.get(i));
                entry.setImageUrl(playlists.get(i));
                dataStorage.insertEntry(entry);
            }



            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //dataStorage.close();
        }

    }
    

}
