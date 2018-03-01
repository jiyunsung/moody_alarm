package edu.dartmouth.cs.moodyalarm;

import java.io.Serializable;

/**
 * Created by vivianjiang on 2/27/18.
 */

public class Weather implements Serializable {


    private Long mId;
    private SpotifyPlaylist mPlaylist;
    private String mName;


    public Weather() {

    }

    public Weather(Long id, String data, SpotifyPlaylist playlist) {
        this.mId = id;
        this.mPlaylist = playlist;
        this.mName = data;
    }


    public Long getId() {
        return this.mId;
    }

    public void setId(Long id) {
        this.mId = id;

    }

    public String getName() {
        return this.mName;
    }

    public void setName(String data) {
        this.mName = data;

    }

    public SpotifyPlaylist getSpotifyPlaylist() {
        return this.mPlaylist;
    }

    public void setSpotifyPlaylist(SpotifyPlaylist playlist) {
        this.mPlaylist = playlist;

    }
}


