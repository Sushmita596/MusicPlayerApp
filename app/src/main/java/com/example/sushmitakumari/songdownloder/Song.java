package com.example.sushmitakumari.songdownloder;

import android.app.Activity;

/**
 * Created by sushmita.kumari on 03-02-2017.
 */

public class Song extends Activity {

    private long id;
    private String title;
    private String artist;

    public Song(long songId, String songTitle, String songArtist){
        id=songId;
        title=songTitle;
        artist=songArtist;
    }
    public long getId(){
        return id;
    }

    public String getArtist(){
        return artist;
    }


    public String getTitles(){
        return title;
    }


}
