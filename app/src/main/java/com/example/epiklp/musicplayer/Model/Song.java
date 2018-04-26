package com.example.epiklp.musicplayer.Model;

import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by epiklp on 18.04.18.
 */

public class Song {
    private long mId;
    private String mPath;
    private String mFileName;
    private String mArtist;
    private String mTitle;
    private String mGenre;
    private String mAlbum;
    private int mYear;
    private int mTrackNumber;
    private int mDuration = 0;


    public Song(int id, String filePath){
        mId = id;
        mPath = filePath;
        File tmp = new File(filePath);
        mFileName = tmp.getName();
    }

    public void setAlbum(String album) {
        mAlbum = album;
    }

    public void setTitle(String name){
        mTitle = name;
    }

    public void setArtist(String artist){
        mArtist = artist;
    }

    public void setGenre(String genres){
        mGenre = genres;
    }

    public void setDuration(int duration){
        mDuration = duration;
    }

    public void setTrackNumber(int trackNumber){
        mTrackNumber = trackNumber;
    }
    public void setYear(int Year){
        mDuration = Year;
    }

    public long getId(){
        return mId;
    }

    public String getPath() {
            return mPath;
    }

    public String getArtist() {
        if(mArtist != null) {
            return mArtist;
        } else {
            return "unknown";
        }
    }

    public String getTitle() {
        if(mTitle != null) {
            return mTitle;
        }  else {
            return "unknown";
        }
    }

    public String getmFileName() {
        return mFileName;
    }

    public String getGenre() {
        if(mGenre != null) {
            return mGenre;
        } else {
            return "unknown";
        }
    }

    public String getAlbum(){
        return mAlbum;
    }

    public int getmYear() {
        return mYear;
    }

    public int getTrackNumber(){
        return mTrackNumber;
    }

    public int getmDuration() {
        return mDuration;
    }
}
