package com.example.s5922214.bettermusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Created by s5922214 on 11/1/2017.
 */

public class BetterMusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;

    private final IBinder musicBind = new MusicBinder();
    @Override
    public IBinder onBind(Intent intent){
        return musicBind;
    }
    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }
    public void onCreate(){
        //create the service
        super.onCreate();
        //initialize position
        songPosn = 0;
        //create player
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }
    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }
    public class MusicBinder extends Binder {
        BetterMusicService getService() {
            return BetterMusicService.this;
        }
    }
    public void playSong(){
        player.reset();
        //get song
        Song playSong = songs.get(songPosn);
        //get id
        long currSong = playSong.getID();
        //set Uri
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }
    @Override
    public void onPrepared(MediaPlayer mp){
        //start playback
        mp.start();
    }
    @Override
    public void onCompletion(MediaPlayer mp){
        if(player.getCurrentPosition() > 0){
            mp.reset();
            //playNext();
        }
    }
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra){
        mp.reset();
        return false;
    }
    public void setSong(int songIndex){
        songPosn=songIndex;
    }
}
