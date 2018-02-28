package edu.dartmouth.cs.moodyalarm;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vivianjiang on 2/26/18.
 */

public class SpotifySong implements Serializable {


    private String mImage_url;
    private String mSongName;
    private String mSongArtist;


    public SpotifySong(){

    }

    public SpotifySong(String songName, String songArtist, String imgUrl){
        this.mImage_url = imgUrl;
        this.mSongName = songName;
        this.mSongArtist = songArtist;
    }



    public String getImageUrl(){
        return this.mImage_url;
    }

    public void setImageUrl(String image_url){
        this.mImage_url = image_url;

    }

    public String getSongName(){
        return this.mSongName;
    }

    public void setSongName(String songName){
        this.mSongName = songName;

    }

    public String getSongArtist(){
        return this.mSongArtist;
    }

    public void setSongArtist(String songArtist){
        this.mSongArtist = songArtist;

    }
}
