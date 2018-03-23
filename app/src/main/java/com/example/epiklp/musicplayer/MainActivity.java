package com.example.epiklp.musicplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //look
    private LinearLayout mLinearLayout;
    private TextView currentText, maxText;
    private ProgressBar progressBar;
    private TextView headText;
    private TableLayout table;
    private TableRow[] rows;

    //Player
    private MediaPlayer mMediaPlayer;
    private Equalizer mEqualizer;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mMediaPlayer = MediaPlayer.create(this, R.raw.music);
        mMediaPlayer.start();
        mMediaPlayer.setLooping(true);

        mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);

        setupFxAndUI();
        updateProgressBar();
    }

    public void updateProgressBar()
    {
        maxText.setText(String.format("%.2f", mMediaPlayer.getDuration() / 60000f));
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                currentText.setText(String.format("%.2f", mMediaPlayer.getCurrentPosition() / 60000f) + "/");
                progressBar.setProgress(mMediaPlayer.getCurrentPosition());
            }
        };
        new Thread(new Task()).start();
    }

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


    public void setupFxAndUI() {
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1f);
        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
        mLinearLayout = (LinearLayout) findViewById(R.id.LinearLinearLayout);
        table = new TableLayout(this);
        table.setLayoutParams(linearLayoutParams);
        rows = new TableRow[3];
        headText = new TextView(this);
        headText.setText("Equalizer");
        headText.setTextSize(20);
        headText.setGravity(Gravity.CENTER_HORIZONTAL);
        headText.setLayoutParams(tableRowParams);
        rows[0] = new TableRow(this);
        rows[0].addView(headText);
        rows[0].getChildAt(0);
        table.addView(rows[0]);
        currentText = new TextView(this);
        currentText.setText("0/");
        currentText.setTextSize(16);
        currentText.setGravity(Gravity.RIGHT);
        currentText.setLayoutParams(tableRowParams);
        maxText = new TextView(this);
        maxText.setText("9");
        maxText.setTextSize(16);
        maxText.setGravity(Gravity.LEFT);
        maxText.setLayoutParams(tableRowParams);
        rows[1] = new TableRow(this);
        rows[1].addView(currentText);
        rows[1].addView(maxText);
        table.addView(rows[1]);
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(mMediaPlayer.getDuration());
        progressBar.setProgress(0);
        progressBar.setLayoutParams(tableRowParams);
        rows[2] = new TableRow(this);
        rows[2].addView(progressBar);
        table.addView(rows[2]);
        mLinearLayout.addView(table);
    }

}
