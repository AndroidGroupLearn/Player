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
