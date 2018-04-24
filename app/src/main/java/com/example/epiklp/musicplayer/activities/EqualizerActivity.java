package com.example.epiklp.musicplayer.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.epiklp.musicplayer.R;
import com.example.epiklp.musicplayer.Values;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by epiklp on 12.04.18.
 */

public class EqualizerActivity extends AppCompatActivity {
    private LinearLayout mLinearLayout;
    private TextView headText;

    public EqualizerActivity(){
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear);
        setupEq();
    }

    public void setupEq(){
        //Layout
        ArrayList<String> equalizerPresets = new ArrayList<String>();
        for(short i = 0; i < Values.mEqualizer.getNumberOfPresets(); i++) {
            equalizerPresets.add(Values.mEqualizer.getPresetName(i));
        }
        ArrayAdapter<String> equalizerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, equalizerPresets);
        equalizerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner equalizerSpinner = new Spinner(this);
        equalizerSpinner.setAdapter(equalizerAdapter);
        equalizerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Values.mEqualizer.usePreset((short)position);
                for(short i = 0; i < Values.mEqualizer.getNumberOfBands(); i++){
                    SeekBar seekBar = findViewById(i);
                    seekBar.setProgress(Values.mEqualizer.getBandLevel(i)+1500);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        rows.get(0).addView(equalizerSpinner);
        table.addView(rows.get(0));
        short numberFrequency = Values.mEqualizer.getNumberOfBands();
        final short lower = Values.mEqualizer.getBandLevelRange()[0];
        final short upper = Values.mEqualizer.getBandLevelRange()[1];
        //EqualizerActivity
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
            seekBar.setProgress(Values.mEqualizer.getBandLevel(bandLevel)+1500);
            seekBar.setId(counter);
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
