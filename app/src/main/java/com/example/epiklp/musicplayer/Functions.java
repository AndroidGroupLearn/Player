package com.example.epiklp.musicplayer;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by epiklp on 17.04.18.
 */
public class Functions {

    public static AlertDialog mAlertDialog;

    public static void findSong(final Context mContext){
        Functions.setProgresDialog(mContext);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String GENRE_ID      = MediaStore.Audio.Genres._ID;
                String GENRE_NAME    = MediaStore.Audio.Genres.NAME;
                String SONG_ID       = MediaStore.Audio.Media._ID;
                String SONG_TITLE    = MediaStore.Audio.Media.TITLE;
                String SONG_ARTIST   = MediaStore.Audio.Media.ARTIST;
                String SONG_ALBUM    = MediaStore.Audio.Media.ALBUM;
                String SONG_YEAR     = MediaStore.Audio.Media.YEAR;
                String SONG_TRACK_NO = MediaStore.Audio.Media.TRACK;
                String SONG_FILEPATH = MediaStore.Audio.Media.DATA;
                String SONG_DURATION = MediaStore.Audio.Media.DURATION;
                String[] columns = {
                        SONG_ID,
                        SONG_TITLE,
                        SONG_ARTIST,
                        SONG_ALBUM,
                        SONG_YEAR,
                        SONG_TRACK_NO,
                        SONG_FILEPATH,
                        SONG_DURATION
                };
                String[] genreColumns = {
                        GENRE_ID,
                        GENRE_NAME
                };
                Values.genreIdToGenreNameMap = new HashMap<>();
                Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Uri genrerUri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = mContext.getContentResolver();
                Cursor mCursor;
                mCursor = contentResolver.query(genrerUri, genreColumns, null, null, null);
                for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext())
                    Values.genreIdToGenreNameMap.put(mCursor.getString(0), mCursor.getString(1));
                mCursor.close();
                Values.songIdToGenreIdMap = new HashMap<>();
                for (String genreID : Values.genreIdToGenreNameMap.keySet()) {
                    Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external",
                            Long.parseLong(genreID));
                    mCursor = contentResolver.query(uri, new String[] { SONG_ID }, null, null, null);
                    for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                        long currentSongID = mCursor.getLong(mCursor.getColumnIndex(SONG_ID));
                        Values.songIdToGenreIdMap.put(Long.toString(currentSongID), genreID);
                    }
                    mCursor.close();
                }
                mCursor = contentResolver.query(songUri, columns, MediaStore.Audio.Media.IS_MUSIC + "=1", null, null);
                if (mCursor != null && mCursor.moveToFirst()) {
                    do {
                        Song song = new Song(mCursor.getInt(mCursor.getColumnIndex(SONG_ID)),
                                mCursor.getString(mCursor.getColumnIndex(SONG_FILEPATH)));

                        song.setTitle(mCursor.getString(mCursor.getColumnIndex(SONG_TITLE)));
                        song.setArtist(mCursor.getString(mCursor.getColumnIndex(SONG_ARTIST)));
                        song.setAlbum(mCursor.getString(mCursor.getColumnIndex(SONG_ALBUM)));
                        song.setYear(mCursor.getInt(mCursor.getColumnIndex(SONG_YEAR)));
                        song.setTrackNumber(mCursor.getInt(mCursor.getColumnIndex(SONG_TRACK_NO)));
                        song.setDuration(mCursor.getInt(mCursor.getColumnIndex(SONG_DURATION)));
                        String currentGenreID   = Values.songIdToGenreIdMap.get(Long.toString(song.getId()));
                        String currentGenreName = Values.genreIdToGenreNameMap.get(currentGenreID);
                        song.setGenre(currentGenreName);
                        Values.mSongList.add(song);
                    } while (mCursor.moveToNext());
                }

                songUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;

                Functions.dismissAlertDialog();
            }
        }).start();
    }


    public static void setProgresDialog(Context mContext){
        LinearLayout.LayoutParams mLinLayoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout mLinearLayout = new LinearLayout(mContext);
        mLinearLayout.setLayoutParams(mLinLayoutParams);
        mLinearLayout.setPadding(0,30,0,30);
        mLinearLayout.setGravity(Gravity.CENTER);

        ProgressBar mProgressBar = new ProgressBar(mContext);
        mProgressBar.setScrollBarSize(100);
        mProgressBar.setPadding(0,0,30,0);


        TextView mTextView = new TextView(mContext);
        mTextView.setTextSize(20);
        mTextView.setTextColor(mContext.getResources().getColor(R.color.black));
        mTextView.setText(R.string.loading);

        mLinearLayout.addView(mProgressBar);
        mLinearLayout.addView(mTextView);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setView(mLinearLayout);

        mAlertDialog = mBuilder.create();
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);

        mAlertDialog.show();
        Window window = mAlertDialog.getWindow();
        if(window != null){
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(mAlertDialog.getWindow().getAttributes());
            layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            mAlertDialog.getWindow().setAttributes(layoutParams);
        }
    }

    public static void dismissAlertDialog(){
        mAlertDialog.dismiss();
    }
}
