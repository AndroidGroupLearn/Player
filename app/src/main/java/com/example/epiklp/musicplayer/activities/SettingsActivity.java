package com.example.epiklp.musicplayer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.example.epiklp.musicplayer.R;
import com.example.epiklp.musicplayer.Values;

/**
 * Created by epiklp on 13.04.18.
 */

public class SettingsActivity extends AppCompatActivity {
    public SettingsActivity(){

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button eqButton = (Button)findViewById(R.id.eqalizer);
        eqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingsActivity.this, EqualizerActivity.class);
                startActivity(i);
            }
        });

        Switch eqSwitch = (Switch)findViewById(R.id.eqTurn);
        eqSwitch.setChecked(Values.mEqualizer.getEnabled());
        eqSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Values.mEqualizer.setEnabled(!Values.mEqualizer.getEnabled());
            }
        });
        Switch bassSwitch = (Switch) findViewById(R.id.bassTurn);
        bassSwitch.setChecked(Values.mBassBoost.getEnabled());
        bassSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!Values.mBassBoost.getStrengthSupported()){
                    Toast.makeText(getApplicationContext(), "BassBoost unsupported", Toast.LENGTH_SHORT);
                }
                Values.mBassBoost.setEnabled(!Values.mBassBoost.getEnabled());
            }
        });
        final SeekBar bassBar = (SeekBar) findViewById(R.id.bassBar);
        bassBar.setMax(1000);
        bassBar.setProgress(Values.mBassBoost.getRoundedStrength());
        bassBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Values.mBassBoost.setStrength((short)i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
