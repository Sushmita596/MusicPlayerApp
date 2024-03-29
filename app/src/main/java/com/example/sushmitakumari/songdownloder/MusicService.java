package com.example.sushmitakumari.songdownloder;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;

import java.util.ArrayList;

/**
 * Created by sushmita.kumari on 07-02-2017.
 */

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener  {

    private final IBinder musicBind = new MusicBinder();
    private MediaPlayer player;
    Notification.Builder quot;
    private ArrayList<Song> songs;
    private String songTitle;



    private static final int NOTIFY_ID=1;
    private int songPosn;

    private boolean shuffle=false;
    private Random rand;
    //private String contentText;


    public void onCreate(){
        super.onCreate();
        songPosn=0;
        player=new MediaPlayer();
        initMusicPlayer();
        rand=new Random();
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

   // public void setContentText(String contentText) {
    //    this.contentText = contentText;
   // }

    public class MusicBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){

        player.stop();
        player.release();
        return false;
    }

    public void playSong(){
        player.reset();
        Song playSong=songs.get(songPosn);
        songTitle= (String) playSong.getTitle();
        long currSong=playSong.getId();
        Uri trackUri= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,currSong);
        try {
            player.setDataSource(getApplicationContext(),trackUri);
        }catch (Exception e){
            Log.e("Music Service","Error setting data source",e);
        }
        player.prepareAsync();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition()==0){
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);


        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle((CharSequence) quot)
        .setContentText(songTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
    }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public void playPrev(){
        songPosn--;
        if(songPosn==0) songPosn=songs.size()-1;
        playSong();
    }



    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }

    public void playNext(){
        if(shuffle){
            int newSong = songPosn;
            while(newSong==songPosn){
                newSong=rand.nextInt(songs.size());
            }
            songPosn=newSong;
        }
        else{
            songPosn++;
            if(songPosn==songs.size()) songPosn=0;
        }
        playSong();
    }
}


