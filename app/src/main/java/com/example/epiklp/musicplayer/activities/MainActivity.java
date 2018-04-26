package com.example.epiklp.musicplayer.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.epiklp.musicplayer.Service.MusicService;
import com.example.epiklp.musicplayer.R;
import com.example.epiklp.musicplayer.Values;
import com.example.epiklp.musicplayer.adapter.SongAdapter;

public class MainActivity extends AppCompatActivity {

    private final int PERMISSION_READ_EXTERNAL_STORAGE = 123;

    //look
    private TextView        timeText;
    private SeekBar         seekBar;
    private Button          backButton, playButton, forwardButton, infoButton, loopButton;

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
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if(isSDPresent) {
            if (Build.VERSION.SDK_INT >= 23) {
                askAboutPermision();
            } else {
                playWithPermission();
            }
        } else {
            playWithOutPermission();
        }
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
                mIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(mIntent);
            }
        });

        backButton = (Button) findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Values.mMusicService.playPrev();
            }
        });
        playButton = (Button) findViewById(R.id.play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Values.mMusicService.isStarted()){
                    Values.mMusicService.playSong();
                    new Thread(new Task()).start();
                    Values.mMusicService.setIsStarted();
                    playButton.setText(getString(R.string.pause));
                } else if(Values.mMusicService.isPlaying()){
                    playButton.setText(getString(R.string.play));
                    Values.mMusicService.pause();
                } else {
                    playButton.setText(getString(R.string.pause));
                    Values.mMusicService.start();
                }
            }
        });
        forwardButton = (Button) findViewById(R.id.forward);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Values.mMusicService.playNext();
            }
        });
        loopButton = findViewById(R.id.lr);
        loopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Values.mMusicService.setShuffle();
                if(Values.mMusicService.getShuffle()){
                    loopButton.setText("SHUFFLE");
                } else {
                    loopButton.setText("LOOP");
                }
            }
        });
        infoButton = findViewById(R.id.information);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(mIntent);
            }
        });
    }



    public void playWithPermission() {
            Values.mSongList.scanSong(this, "external");
            Values.mSongList.scanSong(this, "internal");
            Values.mMusicService = new MusicService(this);
            Values.mMusicService.setList(Values.mSongList.getSongs());
            if(Values.mMusicService.getSizeSong() > 0) {
                ListView mListView = findViewById(R.id.musicList);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Values.mMusicService.setSongPos(position);
                    }
                });
                SongAdapter songAdapter = new SongAdapter(this, Values.mSongList.getSongs());
                mListView.setAdapter(songAdapter);
                Values.mEqualizer = new Equalizer(0, Values.mMusicService.getAudioSessionId());
                Values.mBassBoost = new BassBoost(0, Values.mMusicService.getAudioSessionId());
                initButtons();
                registerReceiver(Values.mHeadSetReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
                initSeekBar();
            } else {
                Toast.makeText(this, "0 song on phone app will not work properly", Toast.LENGTH_LONG).show();
            }
    }

    public void playWithOutPermission(){
            Values.mSongList.scanSong(this, "internal");
            Values.mMusicService = new MusicService(this);
            Values.mMusicService.setList(Values.mSongList.getSongs());
            if(Values.mMusicService.getSizeSong() > 0) {
            ListView mListView = findViewById(R.id.musicList);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Values.mMusicService.setSongPos(position);
                }
            });
            SongAdapter songAdapter = new SongAdapter(this, Values.mSongList.getSongs());
            mListView.setAdapter(songAdapter);
            Values.mEqualizer = new Equalizer(0, Values.mMusicService.getAudioSessionId());
            Values.mBassBoost = new BassBoost(0, Values.mMusicService.getAudioSessionId());
            initButtons();
            registerReceiver(Values.mHeadSetReceiver, new IntentFilter("android.intent.action.HEADSET_PLUG"));
            initSeekBar();
        } else {
            Toast.makeText(this, "0 Music File, app will not work properly", Toast.LENGTH_LONG).show();
        }
    }

    public void initSeekBar() {
        seekBar = (SeekBar) findViewById(R.id.progress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Values.mMusicService.seekTo(seekBar.getProgress());
            }
        });
        timeText = (TextView) findViewById(R.id.timer);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(Values.mMusicService.isPlaying()) {
                    int position = Values.mMusicService.getPosition();
                    int duration = Values.mMusicService.getDuration();
                    timeText.setText(String.format("%02d:%02d", (position / (1000 * 60)) % 60,
                            (position / 1000) % 60) + "/"
                            + String.format("%02d:%02d", (duration / (1000 * 60)) % 60,
                            (duration / 1000) % 60));
                    seekBar.setProgress(position);
                }
                seekBar.setMax(Values.mMusicService.getDuration());
            }
        };

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
