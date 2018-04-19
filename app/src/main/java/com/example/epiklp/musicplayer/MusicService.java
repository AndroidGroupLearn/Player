package com.example.epiklp.musicplayer;

import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;


/**
 * Created by epiklp on 19.04.18.
 */


public class MusicService extends Service
        implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener{
    //Service
    private final IBinder mMusicService = new MusicBinder();
    private final Context tmp;

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return mMusicService;
    }
    @Override
    public boolean onUnbind(Intent intent){
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return false;
    }

    //MediaPlayer
    private MediaPlayer         mMediaPlayer;
    private ArrayList<Song>     mSongs;
    private int                 mSongPos;
    private boolean             shuffle         = false;
    private boolean             loop            = false;
    private Random              mRand;

    //Effects
    private Equalizer           mEqualizer;

    public MusicService(Context c){
        tmp = c;
        super.onCreate();
        mSongPos = 0;
        mRand = new Random();
        mMediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    private void initMusicPlayer(){
        //mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        if(Build.VERSION.SDK_INT > 21){
            mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder().build());
        } else {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
    }

    public void playSong(){
        mMediaPlayer.reset();
        Song playSong = mSongs.get(mSongPos);
        long currSong = playSong.getId();
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try{
            mMediaPlayer.setDataSource(tmp.getApplicationContext(), trackUri);
            mMediaPlayer.prepareAsync();
        }
        catch(Exception e){
            Toast.makeText(getBaseContext().getApplicationContext(), "Playback Error", Toast.LENGTH_SHORT);
        }
    }


    public void start(){
        mMediaPlayer.start();
    }

    public void pause(){
        mMediaPlayer.pause();
    }

    public void playPrev(){
        mSongPos--;
        if(mSongPos<0) mSongPos=mSongs.size()-1;
        playSong();
    }

    //skip to next
    public void playNext(){
        if(shuffle){
            int newSong = mSongPos;
            while(newSong == mSongPos){
                newSong = mRand.nextInt(mSongs.size());
            }
            mSongPos=newSong;
        } else {
            mSongPos++;
            if(mSongPos >= mSongs.size()){
                mSongPos = 0;
            }
        }
        playSong();
    }

    public void setList(ArrayList<Song> theSongs){
        mSongs = theSongs;
    }

    public void setShuffle(){
        shuffle = !shuffle;
    }

    public void setSongPos(int pos){
        mSongPos = pos;
    }

    public int getSongPos(){
        return mSongPos;
    }

    public boolean getShuffle(){
        return shuffle;
    }

    public int getPosition(){
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDuration(){
        return mMediaPlayer.getDuration();
    }

    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }


    //Implementation
    @Override
    public void onCompletion(MediaPlayer mediaPlayer){
        if(mediaPlayer.getCurrentPosition()>0){
            mediaPlayer.reset();
            playNext();
        }
        if(loop){
            mediaPlayer.reset();
            mSongPos = 0;
            playSong();
        }else {
            mediaPlayer.reset();
            mSongPos = 0;
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1){
        Toast.makeText(getBaseContext().getApplicationContext(), "Playback Error", Toast.LENGTH_SHORT);
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer){
        mediaPlayer.start();
        //Toast.makeText(getBaseContext().getApplicationContext(),
        //        "Song:" + mSongs.get(mSongPos).getTitle(), Toast.LENGTH_SHORT);
    }

    @Override
    public void onDestroy(){
        stopForeground(true);
    }
}
