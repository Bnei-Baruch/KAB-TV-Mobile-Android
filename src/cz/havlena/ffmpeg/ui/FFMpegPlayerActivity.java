package cz.havlena.ffmpeg.ui;

import java.io.IOException;

import com.media.ffmpeg.FFMpeg;
import com.media.ffmpeg.FFMpegException;
import com.media.ffmpeg.android.FFMpegMovieViewAndroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class FFMpegPlayerActivity extends Activity {
	private static final String 	TAG = "FFMpegPlayerActivity";
	//private static final String 	LICENSE = "This software uses libraries from the FFmpeg project under the LGPLv2.1";
	
	private FFMpegMovieViewAndroid 	mMovieView;
	//private WakeLock				mWakeLock;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent i = getIntent();
		String filePath = i.getStringExtra(getResources().getString(R.string.input_file));
		if(filePath == null) {
			Log.d(TAG, "Not specified video file");
			finish();
		} else {
			//PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		    //mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);

			try {
				FFMpeg ffmpeg = new FFMpeg();
				mMovieView = ffmpeg.getMovieView(this);
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
				setContentView(mMovieView);
			} catch (FFMpegException e) {
				Log.d(TAG, "Error when inicializing ffmpeg: " + e.getMessage());
				FFMpegMessageBox.show(this, e);
				finish();
			}
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
			FFMpegPlayerActivity.this.finish();
		}
		
	}
	*/
}
