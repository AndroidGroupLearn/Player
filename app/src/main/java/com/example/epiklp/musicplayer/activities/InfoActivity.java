package com.example.epiklp.musicplayer.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.epiklp.musicplayer.Model.Song;
import com.example.epiklp.musicplayer.R;
import com.example.epiklp.musicplayer.Values;

/**
 * Created by epiklp on 26.04.18.
 */

public class InfoActivity extends AppCompatActivity {

    private TextView mPath;
    private TextView mFileName;
    private TextView mArtist;
    private TextView mTitle;
    private TextView mGenre;
    private TextView mAlbum;
    private TextView mYear;
    private TextView mTrackNumber;
    private TextView mDuration;

    public InfoActivity(){

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        initInfoSong();
    }

    private void initInfoSong(){
        Song song = Values.mMusicService.getCurrentSong();
        mPath = findViewById(R.id.namePath);
        mPath.setText(song.getPath());
        mFileName = findViewById(R.id.nameFile);
        mFileName.setText(song.getmFileName());
        mArtist = findViewById(R.id.nameArtist);
        mArtist.setText(song.getArtist());
        mTitle = findViewById(R.id.nameTitle);
        mTitle.setText(song.getTitle());
        mGenre = findViewById(R.id.nameGenre);
        mGenre.setText(song.getGenre());
        mAlbum = findViewById(R.id.nameAlbum);
        mAlbum.setText(song.getAlbum());
        mYear = findViewById(R.id.nameYear);
        mYear.setText(Integer.toString(song.getmYear()));
        mTrackNumber = findViewById(R.id.nameTrNr);
        mTrackNumber.setText(Integer.toString(song.getTrackNumber()));
        mDuration = findViewById(R.id.nameDuration);
        int duration = song.getmDuration();
        mDuration.setText(String.format("%2d:%02d", (duration / (1000 * 60)) % 60,
                (duration / 1000) % 60));
    }
}
