package com.example.epiklp.musicplayer;

import android.content.BroadcastReceiver;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;

import com.example.epiklp.musicplayer.Model.SongList;
import com.example.epiklp.musicplayer.Service.MusicService;
import com.example.epiklp.musicplayer.reciver.HeadSetReceiver;

/**
 * Created by epiklp on 13.04.18.
 */

public class Values {
    public static MediaPlayer                           mMediaPlayer;
    public static Equalizer                             mEqualizer;
    public static BassBoost                             mBassBoost;
    public static boolean                               mEqualizerTurn          = false;
    public static boolean                               mBassBoostTurn          = false;
//    public static HashMap<String, String>               genreIdToGenreNameMap;
//    public static HashMap<String, String>               songIdToGenreIdMap;
//    public static ArrayList<Song>                       mSongList               = new ArrayList<>();
    public static SongList mSongList               = new SongList();
    public static MusicService                          mMusicService;
    public static BroadcastReceiver                     mHeadSetReceiver        = new HeadSetReceiver();

    //public static final String                          MEDIA_PATH              = Environment.getExternalStorageDirectory().getAbsolutePath();
    //public static String[]                              mBlockFolders  = new String[]{"LOST.DIR", "Android", ".android_secure",
    //                                                                                  "DCIM", "Wi-FI Direct", "."};

}
