package com.example.epiklp.musicplayer.Model;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by epiklp on 24.04.18.
 */

public class SongList {

    private HashMap<String, String>                 genreIdToGenreNameMap   = new HashMap<>();;
    private HashMap<String, String>                 songIdToGenreIdMap      = new HashMap<>();
    private ArrayList<Song>                         mSongList               = new ArrayList<>();

    public void scanSong(final Context mContext, String type){
        try {
            String GENRE_ID = MediaStore.Audio.Genres._ID;
            String GENRE_NAME = MediaStore.Audio.Genres.NAME;
            String SONG_ID = MediaStore.Audio.Media._ID;
            String SONG_TITLE = MediaStore.Audio.Media.TITLE;
            String SONG_ARTIST = MediaStore.Audio.Media.ARTIST;
            String SONG_ALBUM = MediaStore.Audio.Media.ALBUM;
            String SONG_YEAR = MediaStore.Audio.Media.YEAR;
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
            Uri songUri;
            Uri genreUri;
            if (type.equals("external")) {
                songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                genreUri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
            } else {
                songUri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
                genreUri = MediaStore.Audio.Genres.INTERNAL_CONTENT_URI;

            }
            ContentResolver contentResolver = mContext.getContentResolver();
            Cursor mCursor;
            mCursor = contentResolver.query(genreUri, genreColumns, null, null, null);
            for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext())
                genreIdToGenreNameMap.put(mCursor.getString(0), mCursor.getString(1));
            mCursor.close();
            for (String genreID : genreIdToGenreNameMap.keySet()) {
                Uri uri = MediaStore.Audio.Genres.Members.getContentUri(type,
                        Long.parseLong(genreID));
                mCursor = contentResolver.query(uri, new String[]{SONG_ID}, null, null, null);
                for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                    long currentSongID = mCursor.getLong(mCursor.getColumnIndex(SONG_ID));
                    songIdToGenreIdMap.put(Long.toString(currentSongID), genreID);
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
                    String currentGenreID = songIdToGenreIdMap.get(Long.toString(song.getId()));
                    String currentGenreName = genreIdToGenreNameMap.get(currentGenreID);
                    song.setGenre(currentGenreName);
                    mSongList.add(song);
                } while (mCursor.moveToNext());
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
            //Toast.makeText(mContext, "0 Music File on " + type, Toast.LENGTH_LONG).show();
        }
    }

    public ArrayList<Song> getSongs() {
        return mSongList;
    }
}
