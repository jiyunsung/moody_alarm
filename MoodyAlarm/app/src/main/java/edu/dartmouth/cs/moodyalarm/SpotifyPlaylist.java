package edu.dartmouth.cs.moodyalarm;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vivianjiang on 2/26/18.
 */

public class SpotifyPlaylist implements Serializable {

    private Long mId;
    private String mImage_url;
    private String mPlaylist_id;
    private String mTrackInfo;
    private byte[] mImageBitmap;



    public SpotifyPlaylist(){

    }

    public SpotifyPlaylist(long id, String image_url, byte[] bitmap, String playlist_id, String trackInfo){
        this.mImage_url = image_url;
        this.mId = id;
        this.mPlaylist_id = playlist_id;
        this.mTrackInfo = trackInfo;
        this.mImageBitmap = bitmap;

    }


    public long getId(){
        return this.mId;
    }

    public void setId(long id){
        this.mId = id;

    }

    public String getImageUrl(){
        return this.mImage_url;
    }

    public void setImageUrl(String image_url){
        this.mImage_url = image_url;

    }

    public String getPlaylistId(){
        return this.mPlaylist_id;
    }

    public void setPlaylistId(String playlist_id){
        this.mPlaylist_id = playlist_id;

    }

    public String getTrackInfo(){
        return this.mTrackInfo;
    }

    public void setTrackInfo(String trackInfo){
        this.mTrackInfo = trackInfo;

    }



}
