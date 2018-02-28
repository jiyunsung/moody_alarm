package edu.dartmouth.cs.moodyalarm;

import java.io.Serializable;

/**
 * Created by vivianjiang on 2/27/18.
 */

public class Day implements Serializable {


        private Long mId;
        private SpotifyPlaylist mPlaylist;
        private String mName;


        public Day() {

        }

        public Day(Long id, String name, SpotifyPlaylist playlist) {
                this.mId = id;
                this.mPlaylist = playlist;
                this.mName = name;
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

        public void setName(String name) {
                this.mName = name;

        }

        public SpotifyPlaylist getSpotifyPlaylist() {
                return this.mPlaylist;
        }

        public void setSpotifyPlaylist(SpotifyPlaylist playlist) {
                this.mPlaylist = playlist;

        }
}


