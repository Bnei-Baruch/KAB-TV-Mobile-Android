/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kab.tv.ui;




import java.io.IOException;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;


public class MediaPlayer_Android extends Activity implements
        OnBufferingUpdateListener, OnCompletionListener,
        OnPreparedListener, OnVideoSizeChangedListener, SurfaceHolder.Callback {

    private static final String TAG = "MediaPlayerDemo";
    private int mVideoWidth;
    private int mVideoHeight;
    private MediaPlayer mMediaPlayer;
    private VideoViewCustom mPreview;
    private TextView mTitle;
    private SurfaceHolder holder;
    private String path;
    private Bundle extras;
    private static final String MEDIA = "media";
    private static final int LOCAL_AUDIO = 1;
    private static final int STREAM_AUDIO = 2;
    private static final int RESOURCES_AUDIO = 3;
    private static final int LOCAL_VIDEO = 4;
    private static final int STREAM_VIDEO = 5;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;
    public static  MediaPlayer_Android mSelf;
	private static boolean mStatus;
	private ProgressDialog dialog;
    
    private PowerManager.WakeLock wl;
    GoogleAnalyticsTracker tracker;
	private boolean mRotated;

    /**
     * 
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
       // dialog = ProgressDialog.show(MediaPlayer_Android.this, "",
       // 		"Please wait for few seconds...",true, true);
       
       
        
        InitializeUI();
        
        
        
        
        
        mSelf = this;
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
        
        
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mPreview.setDimensions(320, 480);
   	 	mPreview.getHolder().setFixedSize(320, 480);
        tracker = GoogleAnalyticsTracker.getInstance();
		 
		
		 
				 
		 tracker.trackPageView("/Playing video stream");
		 
		 tracker.dispatch();
		 
    }

    public void InitializeUI()
    {
        //get views from ID's
    	 mPreview = (VideoViewCustom) findViewById(R.id.mMovieView);
    	 holder = mPreview.getHolder();
    	 holder.addCallback(this);
         holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
         
         extras = getIntent().getExtras();
         
         
         mTitle = (TextView) findViewById(R.id.title);
         Intent i = getIntent();
         mTitle.setText(i.getStringExtra(getResources().getString(R.string.programtitle)));

        //etc... hook up click listeners, whatever you need from the Views
    }
    
    private void SetVideoViewSize()
    {
    	//Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        //Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = mPreview.getLayoutParams();

        //Set the width of the SurfaceView to the width of the screen
        lp.width = screenWidth;

        //Set the height of the SurfaceView to match the aspect ratio of the video 
        //be sure to cast these as floats otherwise the calculation will likely be 0
        lp.height =(int) (((float)mVideoHeight / (float)mVideoWidth) * (float)screenWidth);

        //Commit the layout parameters
        mPreview.setLayoutParams(lp);
        
        mPreview.setDimensions( lp.width,  lp.height);
        mPreview.getHolder().setFixedSize(lp.width,  lp.height);
    }
    @Override
   public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
     
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	 getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
             getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

             SetVideoViewSize();
 
        } else {
        	 getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        	 SetVideoViewSize();


        }
    }
    
    private void playVideo(Integer Media) {
        doCleanUp();
        try {

            switch (Media) {
                case LOCAL_VIDEO:
                    /*
                     * TODO: Set the path variable to a local media file path.
                     */
                    path = "";
                    if (path == "") {
                        // Tell the user to provide a media file URL.
                        Toast
                                .makeText(
                                        MediaPlayer_Android.this,
                                        "Please edit MediaPlayerDemo_Video Activity, "
                                                + "and set the path variable to your media file path."
                                                + " Your media file must be stored on sdcard.",
                                        Toast.LENGTH_LONG).show();

                    }
                    break;
                case STREAM_VIDEO:
                    /*
                     * TODO: Set path variable to progressive streamable mp4 or
                     * 3gpp format URL. Http protocol should be used.
                     * Mediaplayer can only play "progressive streamable
                     * contents" which basically means: 1. the movie atom has to
                     * precede all the media data atoms. 2. The clip has to be
                     * reasonably interleaved.
                     * 
                     */
                	Intent i = getIntent();
                	path = i.getStringExtra(getResources().getString(R.string.input_stream));
                    //path = "";
                    if (path == "") {
                        // Tell the user to provide a media file URL.
                        Toast
                                .makeText(
                                        MediaPlayer_Android.this,
                                        "Please edit MediaPlayerDemo_Video Activity,"
                                                + " and set the path variable to your media file URL.",
                                        Toast.LENGTH_LONG).show();

                    }

                    break;


            }

            // Create a new media player and set the listeners
           // if(mRotated){
          //  	mMediaPlayer.pause();
         //   	 mRotated = false;
         //   }
          
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setDisplay(holder);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            

        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
    }

    public void onBufferingUpdate(MediaPlayer arg0, int percent) {
        Log.e(TAG, "onBufferingUpdate percent:" + percent);
        if(dialog ==null)
        	dialog = ProgressDialog.show(MediaPlayer_Android.this, "",
        	       		"Please wait for few seconds...",true, true);
       
        
        if(percent != 100)
        {
        	dialog.setMessage("Please wait while loading..." + percent + "%");
        	
        }
        else
        {
        	dialog.dismiss();
        }
    }

    public void onCompletion(MediaPlayer arg0) {
        Log.d(TAG, "onCompletion called");
    }

    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.v(TAG, "onVideoSizeChanged called");
        if (width == 0 || height == 0) {
            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    public void onPrepared(MediaPlayer mediaplayer) {
        Log.d(TAG, "onPrepared called");
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
        Log.d(TAG, "surfaceChanged called");
       
       
    }

    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        Log.d(TAG, "surfaceDestroyed called");
    }


    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated called");
        playVideo(extras.getInt(MEDIA));


    }

    @Override
    protected void onResume() {
            super.onResume();
            if(mMediaPlayer!=null)
            	mMediaPlayer.start();
            wl.acquire();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayer.pause();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
        doCleanUp();
        wl.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        doCleanUp();
        
       
          // Stop the tracker when it is no longer needed.
          tracker.stop();
      
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }

    private void startVideoPlayback() {
        Log.v(TAG, "startVideoPlayback");
      
        SetVideoViewSize();
        mMediaPlayer.start();
        
      
    }
    public void setDataSource(String source)
    {
    	path = source;
    
    }
    
    
    
public static void checkCommunicationState(boolean status) throws IllegalStateException, IOException {
	
	mStatus = status;
	if(mSelf == null || mSelf.mMediaPlayer == null)
		return;
	
		if(status && !mSelf.mMediaPlayer.isPlaying())
		{
			
			mSelf.mMediaPlayer.prepare();
			mSelf.mMediaPlayer.start();
		}
		else if(!status){

		/*	AlertDialog alertDialog = new AlertDialog.Builder(mSelf).create();
			alertDialog.setTitle("Communication disconnected");
			alertDialog.setMessage("Do you want to wait or quit?");
			 alertDialog.setButton("Wait", new DialogInterface.OnClickListener() {
				     public void onClick(DialogInterface dialog, int which) {*/
				    	 mSelf.mMediaPlayer.pause();
				     return;
				
	/*			   } }); 
			 alertDialog.setButton2("Quit", new DialogInterface.OnClickListener() {
			     public void onClick(DialogInterface dialog, int which) {
			
			    	 mSelf.onBackPressed();
			
			   } }); 
			
			 alertDialog.show();*/
		}
			
		
	}

}