package cz.havlena.ffmpeg.ui;

import java.io.IOException;

import kab.tv.connection.ConnectivityReceiver;

import com.media.ffmpeg.FFMpeg;
import com.media.ffmpeg.FFMpegException;
import com.media.ffmpeg.android.FFMpegMovieViewAndroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.SurfaceView;

public class FFMpegPlayerActivity extends Activity {
	private static final String 	TAG = "FFMpegPlayerActivity";
	//private static final String 	LICENSE = "This software uses libraries from the FFmpeg project under the LGPLv2.1";
	
	private FFMpegMovieViewAndroid 	mMovieView;
	private SurfaceView 	mMovieViewSurface;
	public static  FFMpegPlayerActivity mSelf;
	ConnectivityReceiver mComNotifier;
	
	//public static  FFMpegPlayerActivity mSelf;
	//private WakeLock				mWakeLock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mSelf  = this;
		mComNotifier = new ConnectivityReceiver();
		registerReceiver(mComNotifier,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		 // attempt to get data from before device configuration change  
		Bundle returnData = (Bundle) getLastNonConfigurationInstance();        
		if (returnData == null) { 
		Intent i = getIntent();
		String filePath = i.getStringExtra(getResources().getString(R.string.input_stream));
		if(filePath == null) {
			Log.d(TAG, "Not specified video file");
			finish();
		} else {
			//PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		    //mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);

			try {
				FFMpeg ffmpeg = new FFMpeg();
				setContentView(R.layout.main);
				mMovieViewSurface =  (SurfaceView)findViewById(R.id.mMovieView);
				
				
				  WindowManager wm = getWindowManager(); 
			        Display d = wm.getDefaultDisplay();
			        

			        LayoutParams params;
			        
			        if (d.getWidth() > d.getHeight())
			        {
			            //---landscape mode---
			            params = new LayoutParams(d.getWidth(), d.getHeight());
			            
			        }
			        else
			        {
			            //---portrait mode---
			        	params = new LayoutParams(d.getWidth(), d.getHeight());
			        }
			       mMovieViewSurface.setMinimumWidth(d.getWidth());
			       mMovieViewSurface.setMinimumHeight(d.getHeight());

			        
			        
				mMovieView = ffmpeg.getMovieView(this,mMovieViewSurface);
				//mMovieView = ffmpeg.getMovieView(this);
				try {
					mMovieView.setVideoPath(filePath);
				} catch (IllegalArgumentException e) {
					Log.e(TAG, "Can't set video: " + e.getMessage());
					FFMpegMessageBox.show(this, e);
				} catch (IllegalStateException e) {
					Log.e(TAG, "Can't set video: " + e.getMessage());
					FFMpegMessageBox.show(this, e);
				} catch (IOException e) {
					Log.e(TAG, "Can't set video: " + e.getMessage());
					FFMpegMessageBox.show(this, e);
				}
					
				//setContentView(mMovieView);
				//LinearLayout mainLayout = (LinearLayout)findViewById(R.layout.main);

				//mainLayout.addView(mMovieView);
		//		setContentView(R.layout.main);
		//		mMovieViewSurface =  (SurfaceView)findViewById(R.id.mMovieView);
		//		mMovieViewSurfaceHolder = mMovieViewSurface.getHolder();
				//mMovieViewSurfaceHolder = mMovieView.getHolder();
				//mMovieView.setVideoPath(filePath);
				//mMovieViewSurface = mMovieView;
				//mMovieViewSurfaceHolder = mMovieViewSurface.getHolder();
				//mMovieViewSurfaceHolder = mMovieView.getHolder(); 
				
				//addContentView(mMovieView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT 
//,ViewGroup.LayoutParams.MATCH_PARENT ));
				//((ViewGroup) findViewById(R.layout.main)).addView(mMovieView, 320,240);
				 //---get the current display info---
		      
			} catch (FFMpegException e) {
				Log.d(TAG, "Error when inicializing ffmpeg: " + e.getMessage());
				FFMpegMessageBox.show(this, e);
				finish();
			}
		}
		
		}
		else
		{
		
		
		}
		
	}
	
	/* @Override    public Object onRetainNonConfigurationInstance() {  
		 // Device configuration changed        
		 // Save current video playback state      
		 Log.d(TAG, "Saving video playback state");     
		  
		 // Build bundle to save data for return       
		// Bundle data = new Bundle();       
		 //data.putString("LOCATION", videoLocation);       
		// data.putInt("POSITION", videoPosition);       
		 return mMovieView;    
		
	 } 
		*/ 
	 
	public void onBackPressed() 
	{
		mMovieView.onBackPressed();
		 unregisterReceiver (mComNotifier);
		finish();
		Log.d(TAG, "onBackPressed in FFMpegPlayerActivity");
	}
	
public static void checkCommunicationState(boolean status) {
		
		if(status && !mSelf.mMovieView.isPlaying())
			mSelf.mMovieView.start();
		else if(!status){

			AlertDialog alertDialog = new AlertDialog.Builder(mSelf).create();
			alertDialog.setTitle("Communication disconnected");
			alertDialog.setMessage("Do you want to wait or quit?");
			 alertDialog.setButton("Wait", new DialogInterface.OnClickListener() {
				     public void onClick(DialogInterface dialog, int which) {
				    	 mSelf.mMovieView.pause();
				     return;
				
				   } }); 
			 alertDialog.setButton2("Quit", new DialogInterface.OnClickListener() {
			     public void onClick(DialogInterface dialog, int which) {
			
			    	 mSelf.onBackPressed();
			
			   } }); 
			
			 alertDialog.show();
		}
			
		
	}
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.layout.ffmpeg_player_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		return mPlayer.pause();
	}
	
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		mPlayer.resume();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.player_menu_about:
			FFMpegMessageBox.show(this, "About", "Developed by Havlena Petr\n" + LICENSE);
			return true;
			
		case R.id.player_menu_decode_audio:
			mPlayer.decodeAudio(!mPlayer.isDecodingAudio());
			Drawable d = null;
			if(mPlayer.isDecodingAudio()) {
				d = getResources().getDrawable(R.drawable.ic_menu_block);
			} else {
				d = getResources().getDrawable(R.drawable.ic_menu_mark);
			}
			item.setIcon(d);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    protected void onResume() {
        //-- we will disable screen timeout, while scumm is running
        if( mWakeLock != null ) {
        	Log.d(TAG, "Resuming so acquiring wakeLock");
        	mWakeLock.acquire();
        }
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        //-- we will enable screen timeout, while scumm is paused
        if(mWakeLock != null ) {
        	Log.d(TAG, "Pausing so releasing wakeLock");
        	mWakeLock.release();
        }
        super.onPause();
    }
    
    private void startFileExplorer() {
    	Intent i = new Intent(this, FFMpegFileExplorer.class);
    	startActivity(i);
    }

	private class FFMpegPlayerHandler implements IFFMpegPlayer {

		public void onError(String msg, Exception e) {
			Log.e(TAG, "ERROR: " + e.getMessage());
			startFileExplorer();
		}

		public void onPlay() {
			Log.d(TAG, "starts playing");
		}

		public void onRelease() {
			Log.d(TAG, "released");
			//startFileExplorer();
		}

		public void onStop() {
			Log.d(TAG, "stopped");
			FFMpegPlay)erActivity.this.finish();
		}
		
	}
	*/
}
