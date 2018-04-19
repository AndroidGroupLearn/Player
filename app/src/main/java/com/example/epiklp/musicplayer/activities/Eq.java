package com.example.epiklp.musicplayer.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.epiklp.musicplayer.R;
import com.example.epiklp.musicplayer.Values;

import java.util.Vector;

/**
 * Created by epiklp on 12.04.18.
 */

public class Eq extends AppCompatActivity {
    private LinearLayout mLinearLayout;
    private TextView headText;

    public Eq(){
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear);
        setupEq();
    }

    public void setupEq(){

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
        short numberFrequency = Values.mEqualizer.getNumberOfBands();
        final short lower = Values.mEqualizer.getBandLevelRange()[0];
        final short upper = Values.mEqualizer.getBandLevelRange()[1];
        //Eq
        for(short counter = 0; counter < numberFrequency; ++counter) {
            final short bandLevel = counter;
            TextView frequencyTextView = new TextView(this);
            frequencyTextView.setLayoutParams(tableRowParams);
            frequencyTextView.setText(Values.mEqualizer.getCenterFreq(bandLevel)/1000 + "Hz");
            frequencyTextView.setGravity(Gravity.CENTER);
            rows.add(new TableRow(this));
            rows.lastElement().addView(frequencyTextView);
            table.addView(rows.lastElement());
            TextView lowerBand = new TextView(this);
            lowerBand.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            lowerBand.setGravity(Gravity.CENTER);
            lowerBand.setText((lower/100) + "dB");
            TextView upperBand = new TextView(this);
            upperBand.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
            upperBand.setText((upper/100) + "dB");
            upperBand.setGravity(Gravity.CENTER);

            //bar
            SeekBar seekBar = new SeekBar(this);
            seekBar.setId(counter);
            seekBar.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 2));
            seekBar.setMax(upper-lower);
            seekBar.setProgress(Values.mEqualizer.getBandLevel(bandLevel));
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    Values.mEqualizer.setBandLevel(bandLevel, (short) (i + lower));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            rows.add(new TableRow(this));
            rows.lastElement().addView(lowerBand);
            rows.lastElement().addView(seekBar);
            rows.lastElement().addView(upperBand);
            table.addView(rows.lastElement());
        }
        mLinearLayout.addView(table);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
