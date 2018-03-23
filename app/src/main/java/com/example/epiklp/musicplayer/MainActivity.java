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

import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    //look
    private LinearLayout mLinearLayout;
    private TextView currentText;
    private ProgressBar progressBar;
    private TextView headText;
    private int[] time = new int[2];
    private String timeSong;

    //Player
    private MediaPlayer mMediaPlayer;
    private Equalizer mEqualizer;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        time[0]=0;
        time[1]=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mMediaPlayer = MediaPlayer.create(this, R.raw.music);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                time[1] = time[0] = 0;
            }
        });
        mMediaPlayer.start();
        mMediaPlayer.setLooping(true);

        mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);

        setupFxAndUI();
        updateProgressBar();
    }

    public void updateProgressBar()
    {
        timeSong = String.format("%.2f", mMediaPlayer.getDuration() / 60000f);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(time[1] < 10)
                    currentText.setText(time[0] + ".0" + time[1]+"/"+timeSong);
                else
                    currentText.setText(time[0] + "." + time[1]+"/"+timeSong);
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
                    time[1]++;
                    if(time[1] == 60)
                    {
                        time[0]++;
                        time[1]=0;
                    }
                    handler.sendMessage(handler.obtainMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void setupFxAndUI() {

        //head
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        mLinearLayout = (LinearLayout) findViewById(R.id.LinearLinearLayout);

        TableLayout table;
        Vector<TableRow> rows = new Vector<>();

        table = new TableLayout(this);
        table.setLayoutParams(linearLayoutParams);
        headText = new TextView(this);
        headText.setText("Equalizer");
        headText.setTextSize(20);
        headText.setGravity(Gravity.CENTER_HORIZONTAL);
        headText.setLayoutParams(tableRowParams);
        rows.add(new TableRow(this));
        rows.get(0).addView(headText);
        rows.get(0).getChildAt(0);
        table.addView(rows.get(0));
        //Timer
        currentText = new TextView(this);
        currentText.setText("0/");
        currentText.setTextSize(16);
        currentText.setGravity(Gravity.CENTER);
        currentText.setLayoutParams(tableRowParams);
        rows.add(new TableRow(this));
        rows.get(1).addView(currentText);
        table.addView(rows.get(1));
        //progressBar
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(mMediaPlayer.getDuration());
        progressBar.setProgress(0);
        progressBar.setLayoutParams(tableRowParams);
        rows.add(new TableRow(this));
        rows.get(2).addView(progressBar);
        table.addView(rows.get(2));

        short numberFrequency = mEqualizer.getNumberOfBands();
        final short lower = mEqualizer.getBandLevelRange()[0];
        final short upper = mEqualizer.getBandLevelRange()[1];
        for(short counter = 0; counter < numberFrequency; ++counter) {
            TextView frequencyTextView = new TextView(this);
            frequencyTextView.setLayoutParams(tableRowParams);
            frequencyTextView.setText(mEqualizer.getCenterFreq(counter)/1000 + "Hz");
            rows.add(new TableRow(this));
            rows.lastElement().addView(frequencyTextView);
            table.addView(rows.lastElement());
        }
        mLinearLayout.addView(table);
    }

}
