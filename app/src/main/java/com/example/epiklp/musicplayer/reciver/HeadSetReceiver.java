package com.example.epiklp.musicplayer.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.epiklp.musicplayer.Values;

/**
 * Created by epiklp on 21.04.18.
 */

public class HeadSetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)){
            int state = intent.getIntExtra("state", -1);
            if(state == 0) {
                if(Values.mMusicService.isPlaying()){
                    Toast.makeText(context.getApplicationContext(),
                            "Headphones unplugged", Toast.LENGTH_SHORT).show();
                    Values.mMusicService.pause();
                }
            }
        }
    }
}
