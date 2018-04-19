package com.example.epiklp.musicplayer.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.epiklp.musicplayer.Functions;
import com.example.epiklp.musicplayer.MusicService;
import com.example.epiklp.musicplayer.R;
import com.example.epiklp.musicplayer.Values;

import java.util.Comparator;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private final int PERMISSION_READ_EXTERNAL_STORAGE = 123;

    //look
    private TextView        currentText;
    private SeekBar         progressBar;
    private String          timeSong;
    private Button          backButton, playButton, forwardButton;

    private static Handler  handler;
    private Button          settingsButton;
    private Intent          mIntent;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ_EXTERNAL_STORAGE:
                    if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {
                        playWithPermission();
                    }
                    else
                    {
                        playWithOutPermission();
                    }
                    break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_player);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if(Build.VERSION.SDK_INT >= 23) {
            askAboutPermision();
        } else {
            playWithPermission();
        }
        initButtons();
    }

    //Zapytanie o zgode wczytywania z SD
    public void askAboutPermision(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showMessageOKCancel(getString(R.string.message_permission),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                        PERMISSION_READ_EXTERNAL_STORAGE);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            playWithPermission();
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .create()
                .show();
    }

    //Zainicjowanie przycisków
    public void initButtons(){
        settingsButton = (Button) findViewById(R.id.settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent = new Intent(getApplicationContext(), mySettings.class);
                startActivity(mIntent);
            }
        });

        backButton = (Button) findViewById(R.id.back);
        playButton = (Button) findViewById(R.id.play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Values.mMusicService.isPlaying()){
                    playButton.setText(getString(R.string.play));
                    if(Values.mMediaPlayer == null) {
                        Values.mMediaPlayer.pause();
                    } else {
                        Values.mMusicService.pause();
                    }
                } else {
                    playButton.setText(getString(R.string.pause));
                    Values.mMusicService.playSong();
                }
            }
        });
        forwardButton = (Button) findViewById(R.id.forward);
    }



    public void playWithPermission() {
        Functions.findSong(this);
        ListView mListView = findViewById(R.id.musicList);
        //mListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
          //                              LinearLayout.LayoutParams.MATCH_PARENT));
        SongAdapter songAdapter = new SongAdapter(this, Values.mSongList);
        mListView.setAdapter(songAdapter);
        Values.mMusicService = new MusicService(getApplicationContext());
        Values.mMusicService.setList(Values.mSongList);
        /*Values.mMediaPlayer = MediaPlayer.create(this, R.raw.music);
        Values.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });

        Values.mMediaPlayer.setLooping(true);
        Values.mEqualizer = new Equalizer(0, Values.mMediaPlayer.getAudioSessionId());
        Values.mEqualizer.setEnabled(Values.mEqualizerTurn);
        Values.mBassBoost = new BassBoost(0, Values.mMediaPlayer.getAudioSessionId());
        Values.mBassBoost.setEnabled(Values.mBassBoostTurn);
        initProgressBar();*/

    }

    public void playWithOutPermission(){
        Values.mMediaPlayer = MediaPlayer.create(this, R.raw.music);
        Values.mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

            }
        });

        Values.mMediaPlayer.setLooping(true);
        Values.mEqualizer = new Equalizer(0, Values.mMediaPlayer.getAudioSessionId());
        Values.mEqualizer.setEnabled(Values.mEqualizerTurn);
        Values.mBassBoost = new BassBoost(0, Values.mMediaPlayer.getAudioSessionId());
        Values.mBassBoost.setEnabled(Values.mBassBoostTurn);
        initProgressBar();
    }

    public void initProgressBar() {
        progressBar = (SeekBar) findViewById(R.id.progress);
        progressBar.setMax(Values.mMediaPlayer.getDuration());
        progressBar.setProgress(0);
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Values.mMediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        currentText = (TextView) findViewById(R.id.timer);
        timeSong = String.format("%02d:%02d", (Values.mMediaPlayer.getDuration()/(1000 * 60)) % 60 , (Values.mMediaPlayer.getDuration()/ 1000) % 60);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                    currentText.setText(String.format("%02d:%02d", (Values.mMediaPlayer.getCurrentPosition()/(1000 * 60)) % 60 , (Values.mMediaPlayer.getCurrentPosition()/ 1000) % 60) + "/"+timeSong);
                progressBar.setProgress(Values.mMediaPlayer.getCurrentPosition());
            }
        };
        new Thread(new Task()).start();
    }

    class loader implements Runnable{
        @Override
        public void run() {

        }
    }

    //Watek odpowiedzialny za timer oraz progresBar
    class Task implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    handler.sendMessage(handler.obtainMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Values.mEqualizer.release();
        Values.mMediaPlayer.release();
        finish();
    }
}
