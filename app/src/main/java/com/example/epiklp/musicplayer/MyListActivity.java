package com.example.epiklp.musicplayer;

import android.app.ListActivity;
import android.os.Bundle;

import com.example.epiklp.musicplayer.activities.SongAdapter;

/**
 * Created by epiklp on 18.04.18.
 */

public class MyListActivity extends ListActivity {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        SongAdapter mSongAdapter = new SongAdapter(this, Values.mSongList);
    }
}
