package edu.dartmouth.cs.moodyalarm;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vivianjiang on 2/26/18.
 */

public class SpotifyEntry implements Serializable {

    private Long mId;
    private String mImage_url;


    public SpotifyEntry(){

    }

    public SpotifyEntry(long id, String image_url){
        this.mImage_url = image_url;
        this.mId = id;
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
}
