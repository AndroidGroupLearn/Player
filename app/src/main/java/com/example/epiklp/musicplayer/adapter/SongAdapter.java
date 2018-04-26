package com.example.epiklp.musicplayer.adapter;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.epiklp.musicplayer.R;
import com.example.epiklp.musicplayer.Model.Song;

import java.util.ArrayList;

/**
 * Created by epiklp on 18.04.18.
 */

public class SongAdapter extends BaseAdapter {

    private ArrayList<Song> mSong;
    private Activity mContext;

    public SongAdapter(Activity c, ArrayList<Song> theSongs) {
        mSong = theSongs;
        mContext = c;
    }

    @Override
    public int getCount() {
        return mSong.size();
    }

    @Override
    public Object getItem(int position  ) {
        return mSong.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        Display display = mContext.getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        View rowView = inflater.inflate(R.layout.rowlayout, null);
        rowView.setLayoutParams(new LinearLayout.LayoutParams(p.x, LinearLayout.LayoutParams.MATCH_PARENT));
        TextView mSongName = rowView.findViewById(R.id.nameSong);
        TextView mArtistName = rowView.findViewById(R.id.nameArtist);
        Song CurentSont = mSong.get(i);
        mSongName.setText(CurentSont.getTitle());
        mArtistName.setText(CurentSont.getArtist());
        return rowView;
    }

}
