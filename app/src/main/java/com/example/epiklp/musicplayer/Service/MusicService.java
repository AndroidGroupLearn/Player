package com.example.epiklp.musicplayer.Service;

import android.app.Activity;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epiklp.musicplayer.R;
import com.example.epiklp.musicplayer.Model.Song;

import java.util.ArrayList;
import java.util.Random;


/**
 * Created by epiklp on 19.04.18.
 */


public class MusicService extends Service
        implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener{
    //Service
    private final IBinder mMusicService = new MusicBinder();
    private final Activity tmp;

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
    private Song                playSong;
    private TextView            nameSong, time;
    private SeekBar             seekBar;
    private int                 mSongPos;
    private boolean             shuffle         = false;
    private boolean             loop            = false;
    private boolean             isStarted       = false;
    private boolean             isPlaying;
    private Random              mRand;

    //Effects
    private Equalizer           mEqualizer;

    public MusicService(Activity c){
        super.onCreate();
        tmp = c;
        nameSong = (TextView) tmp.findViewById(R.id.musicName);
        time = (TextView) tmp.findViewById(R.id.timer);
        seekBar = (SeekBar) tmp.findViewById(R.id.progress);
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
        isPlaying = true;
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        playSong = mSongs.get(mSongPos);
        long currSong = playSong.getId();
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try{
            mMediaPlayer.setDataSource(tmp.getApplicationContext(), trackUri);
            mMediaPlayer.prepareAsync();
        }
        catch(Exception e){
            Toast.makeText(tmp.getApplicationContext(), "Playback Error", Toast.LENGTH_SHORT).show();
        }
        setTitle();
    }

    private void setTitle(){
        String name;
        int duration = mSongs.get(mSongPos).getmDuration();
        time.setText("00:00/" + String.format("%02d:%02d", (duration/(1000 * 60)) % 60 ,
                (duration/ 1000) % 60));
        if(mSongs.get(mSongPos).getArtist().equals("unknowon")){
            name = mSongs.get(mSongPos).getTitle();
        } else {
            name = mSongs.get(mSongPos).getArtist() + "-" +
                    mSongs.get(mSongPos).getTitle();
        }
        nameSong.setText(name);
    }

    public void start(){
        if(playSong.getId() != mSongs.get(mSongPos).getId()){
            playSong();
        } else {
            mMediaPlayer.start();
            isPlaying = true;
        }
    }

    public void pause(){
        mMediaPlayer.pause();
        isPlaying = false;
    }

    public void playPrev(){
        mSongPos--;
        if(mSongPos<0){
            mSongPos=mSongs.size()-1;
        }
        if(mMediaPlayer.isPlaying()){
            playSong();
        } else {
            setTitle();
        }
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
        if(isPlaying) {
            playSong();
        } else {
            setTitle();
        }
    }

    public void setList(ArrayList<Song> theSongs){
        mSongs = theSongs;
        setTitle();
    }

    public void setShuffle(){
        shuffle = !shuffle;
    }

    public void setSongPos(int pos){
        mSongPos = pos;
        if(mMediaPlayer.isPlaying()) {
            playSong();
        } else {
            setTitle();
        }
    }

    public void setIsStarted(){
        isStarted = !isStarted;
    }
    public int getSongPos(){
        return mSongPos;
    }

    public boolean getShuffle(){
        return shuffle;
    }

    public void seekTo(int i){
        mMediaPlayer.seekTo(i);
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

    public boolean isStarted(){
        return isStarted;
    }

    public Song getCurrentSong(){
        return mSongs.get(mSongPos);
    }

    public int getAudioSessionId(){
        return mMediaPlayer.getAudioSessionId();
    }

    public int getSizeSong(){
        return mSongs.size();
    }


    //Implementation
    @Override
    public void onCompletion(MediaPlayer mediaPlayer){
        mSongPos++;
        playNext();
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
        Toast.makeText(tmp.getApplicationContext(),
                "Song:" + mSongs.get(mSongPos).getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy(){
        stopForeground(true);
    }
}
